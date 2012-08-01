/*
 * Copyright (C) 2010 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman.impl.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.gearman.impl.core.GearmanCallbackHandler;
import org.gearman.impl.core.GearmanConnectionManager.ConnectCallbackResult;

/**
 * The interface and manager for the underlying facilities<br>
 * <br>
 * Use this class to open ports, create sockets, and schedule tasks to run on
 * the worker's thread-pool
 * 
 * @author isaiah.v
 */
public final class NioReactor {

	/** The Acceptor */
	Acceptor acceptor;

	/** Dispatcher Pool */
	Dispatcher[] dispatchers;

	/** The index of the next dispatcher */
	private int count = 0;

	/** Indicates if this Reactor is shutdown */
	private boolean isShutdown = false;

	/** The worker pool */
	private final ExecutorService workerPool;

	/**
	 * Creates new Reactor with a specified worker ExecutorService
	 * @param dispatchers
	 * 		The number of dispatcher threads
	 * @param workerSet
	 * 		The work's ExecutorService
	 * @throws IOException
	 * 		If an I/O exception occurs while setting up the Reactor
	 */
	public NioReactor(final int dispatchers, final ExecutorService workerSet) throws IOException {
		if (dispatchers < 1)
			throw new IllegalArgumentException(dispatchers + "Dispatchers");
		if (workerSet == null || workerSet.isShutdown())
			throw new IllegalArgumentException("Invalid worker set");

		this.dispatchers = new Dispatcher[dispatchers];
		for (int i = 0; i < dispatchers; i++) {
			this.dispatchers[i] = new Dispatcher(this);
		}

		this.workerPool = workerSet;
	}
	
	public NioReactor(final ExecutorService workerSet) throws IOException {
		this(1, workerSet);
	}

	/**
	 * Attempts to open a port to listen on.
	 * 
	 * @param <X>
	 *            The attachment type for sockets created on this port
	 * @param port
	 *            The port number to listen on
	 * @param sHandler
	 *            The event handler for all sockets created on this port
	 * @throws IOException
	 *             Thrown if an I/O exception occurs while opening the port
	 */
	public final <X> void openPort(final int port, final SocketHandler<X> sHandler) throws IOException {
		if (this.isShutdown)
			throw new IllegalArgumentException("Reactor Closed");

		if (this.acceptor == null || this.acceptor.isClosed())
			this.acceptor = new Acceptor(this);

		this.acceptor.openPort(port, sHandler);
	}
	
	/**
	 * Attempts to close a port opened by this Reactor
	 * @param port
	 * 		The port to close
	 * @return
	 * 		<true>true</code> if and only if the port was successfully closed
	 */
	public final boolean closePort(final int port) throws IOException {
		return this.acceptor==null? false: this.acceptor.closePort(port);
	}
	
	/**
	 * Returns an unmodifiable set of port numbers controlled by this </code>Reactor</code>
	 * @return
	 * 		The unmodifiable set of port numbers
	 */
	public final Set<Integer> getOpenPorts() {
		return this.acceptor==null? Collections.<Integer>emptySet(): this.acceptor.getOpenPorts();
	}
		
	/**
	 * Attempts to close all ports controlled by this <code>Reactor</code>
	 */
	public final void closePorts() {
		if(this.acceptor!=null) this.acceptor.closeAll();
	}

	/**
	 * Opens a client socket
	 * 
	 * @param <X>
	 *            The socket's argument type
	 * @param adrs
	 *            The address of the server to connect to
	 * @param sHandler
	 *            The event handler for this socket
	 * @param ioeHandler
	 *            The event handler for the chance that an I/O exception occurs
	 */


	final <X> void openSocket(final SocketChannel socket, final SocketHandler<X> sHandler) throws IOException {
		final Dispatcher[] dispatchers = this.dispatchers;
		if (this.isShutdown)
			throw new IllegalStateException("Reactor Closed");

		assert dispatchers != null;

		final Dispatcher d = this.dispatchers[count++];
		count = count % this.dispatchers.length;

		d.openSocket(socket, sHandler);
	}
	
	public final <X> void openSocket(final InetSocketAddress adrs, final SocketHandler<X> sHandler, final GearmanCallbackHandler<InetSocketAddress, ConnectCallbackResult> callback) {
		final Dispatcher[] dispatchers = this.dispatchers;
		if (this.isShutdown)
			callback.onComplete(adrs, ConnectCallbackResult.SERVICE_SHUTDOWN);

		assert dispatchers != null;

		final Dispatcher d = this.dispatchers[count++];
		count = count % this.dispatchers.length;

		d.openSocket(adrs, sHandler, callback);
	}

	/**
	 * Shutdown the reactor. Closes all underlying facilities and frees
	 * resources held by this reactor
	 */
	public final void shutdown() {
		if (this.isShutdown)
			return;
		this.isShutdown = true;

		final Acceptor acceptor = this.acceptor;
		this.acceptor = null;

		final Dispatcher[] dispatchers = this.dispatchers;
		this.dispatchers = null;

		if (acceptor != null)
			acceptor.close();

		for (Dispatcher d : dispatchers) {
			d.close();
		}

		this.workerPool.shutdown();
	}

	/**
	 * Tests if this reactor has been shutdown
	 * 
	 * @return <code>true<code> if this reactor has been shutdown. <code>false</code>
	 *         if this reactor is still running
	 */
	public final boolean isShutdown() {
		return this.isShutdown;
	}

	final Executor getWorkerPool() {
		return this.workerPool;
	}
}
