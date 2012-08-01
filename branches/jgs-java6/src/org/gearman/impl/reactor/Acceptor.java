/*
 * Copyright (C) 2010 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman.impl.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Accepts socket connections from client.
 * 
 * @author isaiah.v
 */
final class Acceptor implements Runnable, Closeable {

	/**
	 * Accepts connections on a specific port 
	 * @author isaiah.v
	 *
	 * @param <X>
	 * 		The Socket Argument Type
	 */
	private final class PortAcceptor<X> {
		private final ServerSocketChannel ssChannel;
		private final SocketHandler<X> sHandler;
		
		/**
		 * Creates a new PortAcceptor
		 * @param ssChannel
		 * 		The ServerSocketChannel bound to a 
		 * @param sHandler
		 */
		public PortAcceptor(final ServerSocketChannel ssChannel, final SocketHandler<X> sHandler) {
			this.ssChannel = ssChannel;
			this.sHandler = sHandler;
		}
		
		public void close() {
			try {
				this.ssChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void accept() {
			
			try {
				final SocketChannel sChannel = ssChannel.accept();
				if(sChannel==null) return;
				
				Acceptor.this.reactor.openSocket(sChannel, this.sHandler);
			} catch(IllegalStateException ise) {
				// It's possible for the reactor to be shutdown at the same time as the a new
				// client is connecting. Thus it is also possible for the reactor to throw
				// an IllegalStateException
				ise.printStackTrace();
				assert Acceptor.this.reactor.isShutdown();
			} catch(ClosedChannelException cce){
				// It's possible for the ssChannel to close between the isOpen check and the
				// accept
				cce.printStackTrace();
				assert !ssChannel.isOpen();
			}catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/** The <code>Acceptor</code>'s Selector */
	private final Selector selector;
	
	/** The {@link NioReactor} that created this <code>Acceptor</code>*/ 
	private final NioReactor reactor;
	
	/** Tests if this <code>Acceptor</code> has been shutdown */
	private boolean isShutdown = false;
	
	/** A map from the port number to the ServerSocketChannel who is bound to that port */
	private final Map<Integer,ServerSocketChannel> ports = new ConcurrentHashMap<Integer,ServerSocketChannel>();
	
	/** A queue of ports to be registered with the selector */
	private final Queue<PortAcceptor<?>> registerQueue = new LinkedBlockingQueue<PortAcceptor<?>>();
	
	/**
	 * Creates a new <code>Acceptor</code>
	 * @param reactor
	 * 		The {@link NioReactor} who is creating this Acceptor
	 * @throws IOException
	 * 		if any I/O exceptions occur
	 */
	public Acceptor(final NioReactor reactor) throws IOException {
		/* Called by worker thread */
		
		this.selector = Selector.open();
		this.reactor = reactor;
		
		new Thread(this).start();
	}
	
	private final void clean() {
		for(SelectionKey key : selector.keys()) {
			((PortAcceptor<?>)key.attachment()).close();
		}
		
		try {
			this.selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.closeAll();
		this.closeQueue();
	}
	
	/**
	 * Closes all ports controlled by this <code>Acceptor</code>
	 */
	public void closeAll() {
		final Iterator<ServerSocketChannel> it = this.ports.values().iterator();
		ServerSocketChannel value;
		while(it.hasNext()) {
			value = it.next();
			it.remove();
			try {
				value.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/** 
	 * Attempts to close a port. Only ports created by this <code>Acceptor</code> can
	 * be closed by this <code>Acceptor</code>.  
	 * @param port
	 * 		The port to close
	 * @return
	 * 		<code>true</code> if and only if the port is controlled by this <code>Acceptor</code>
	 * 		and the port was successfully closed
	 */
	public boolean closePort(final int port) {
		/* Called by worker thread */
		
		if(this.isShutdown)
			throw new IllegalStateException("Acceptor closed");
		
		final ServerSocketChannel ssChannel = this.ports.remove(port);
		if(ssChannel==null) return false;
		
		try {
			ssChannel.close();
			return true;
		} catch(IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
	}
	
	@Override
	protected final void finalize() throws Throwable {
		this.close();
	}
	
	/**
	 * Returns an unmodifiable set of ports controlled by this <code>Acceptor</code>. 
	 * @return
	 * 		An unmodifiable set of ports controlled by this <code>Acceptor</code>.
	 * 		
	 */
	public final Set<Integer> getOpenPorts() {
		return Collections.unmodifiableSet(this.ports.keySet());
	}
	
	/**
	 * Attempts to open a port to listen for connections
	 * @param <X>
	 * 		The attachment type for {@link Socket}s created on this port 
	 * @param port
	 * 		The port number to listen on.
	 * @param sHandler
	 * 		The {@link SocketHandler} that will be used for socket connections
	 * 		created on this port.
	 * @throws IOException
	 * 		if any I/O exceptions occur
	 */
	public final <X> void openPort(final int port, final SocketHandler<X> sHandler) throws IOException {
		/* Called by worker thread */
				
		if(sHandler==null)
			throw new IllegalArgumentException("SocketHandler is null");
		
		// Create Server Socket
		final ServerSocketChannel ssChannel = ServerSocketChannel.open();
		ssChannel.socket().setPerformancePreferences(1, 0, 0);
		
		// Configure Socket
		ssChannel.configureBlocking(false);
		ssChannel.socket().bind(new InetSocketAddress(port));
		
		final Object o = this.ports.put(port, ssChannel);
		assert o==null;
		
		this.registerQueue.add(new PortAcceptor<X>(ssChannel,sHandler));
		
		/*
		 * There is a possibility that a thread may try to open a port at the same
		 * time that another thread is closing the reactor. We need to make 100% sure
		 * that all sockets are closed properly and the user's are properly notified.
		 */
		if(this.isShutdown) {
			/*
			 * At this point there is a chance that the Acceptor's driving thread
			 * has already completed. This Implies that there is a chance that the
			 * object we just added to the registerQueue will never be closed. So it'll
			 * need to be done in this thread in such a way that the user will get
			 * notified of the close exactly once. This is where the cleanQueue()
			 * method comes in.
			 */
			this.closeQueue();
		} else {
			this.selector.wakeup();
		}
	}
	
	private final void closeQueue() {
		PortAcceptor<?> pa;
		while(!this.registerQueue.isEmpty()) {
			if((pa = this.registerQueue.poll())==null) continue;
			pa.close();
		}
	}
	
	private final void register() {
		while(!this.registerQueue.isEmpty()) {
			PortAcceptor<?> pa = this.registerQueue.poll();
			if(pa==null) continue;
			
			try {pa.ssChannel.register(this.selector, SelectionKey.OP_ACCEPT, pa);}
			catch (ClosedChannelException e) { /* If channel closed, do nothing */ }
		}
	}
	
	@Override
	public final void run() {
		try {
			while(!this.isShutdown) {
				// Register any new PortAcceptors
				this.register();
				
				// Block until an accept event occurs
				this.selector.select();
				
				// Create Sockets
				for(SelectionKey key : this.selector.selectedKeys())
					((PortAcceptor<?>)key.attachment()).accept();
				this.selector.selectedKeys().clear();
			}
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} finally {
			this.clean();
		}
	}

	@Override
	public void close() {
		this.isShutdown = true;
		this.selector.wakeup();
	}

	@Override
	public boolean isClosed() {
		return this.isShutdown;
	}
}
