/*
 * Copyright (C) 2010 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman.impl.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.gearman.impl.core.GearmanCallbackHandler;
import org.gearman.impl.core.GearmanConnectionManager.ConnectCallbackResult;

/**
 * Dispatches events. 
 * @author isaiah.v
 */
final class Dispatcher implements Runnable, Closeable {

	private final NioReactor reactor;
	private final Selector selector;
	private boolean isShutdown = false;
	
	private final Queue<Connector<?>> regQueue = new LinkedBlockingQueue<Connector<?>>();
	
	public Dispatcher(final NioReactor reactor) throws IOException {
		this.selector = Selector.open();
		this.reactor = reactor;
		
		new Thread(this).start();
	}
	
	/**
	 * Called to add a connected SocketChannel to this Dispatcher.  This is an internal method and should only
	 * be called indirectly by the Acceptor.
	 * @param <X>
	 * 		Socket attachment type
	 * @param sChannel
	 * 		The connected SocketChannel
	 * @param sHandler
	 * 		The event handler for this socket
	 * @throws IOException
	 * 		
	 */
	final <X> void openSocket(final SocketChannel sChannel, final SocketHandler<X> sHandler) throws IOException {
		assert sChannel.isConnected();
		
		sChannel.configureBlocking(false);
		
		final DispatcherHandler dHandler = new SocketImpl<X>(sChannel, sHandler, this.reactor);
		this.regQueue.add(new ConnectedConnector<X>(dHandler));
		
		/*
		 * There is a possibility that a thread may try to open a socket at the same
		 * time that another thread is closing the reactor. We need to make 100% sure
		 * that all sockets are closed properly and the user's are properly notified.
		 */
		if(this.isShutdown) {
			/*
			 * At this point there is a chance that the Dispatcher's driving thread
			 * has already completed. This Implies that there is a chance that the
			 * object we just added to the refQueue will never be closed. So it'll
			 * need to be done in this thread in such a way that the user will get
			 * notified of the close exactly once. This is where the cleanQueue()
			 * method comes in.
			 */
			this.cleanQueue();
		} else
			this.selector.wakeup();
	}
	
	public final <X> void openSocket(final InetSocketAddress adrs, final SocketHandler<X> sHandler, final GearmanCallbackHandler<InetSocketAddress, ConnectCallbackResult> callback) {
		/* Runs on non-dispatcher thread */
		 
		// check parameters
		if(adrs==null || sHandler==null) throw new IllegalArgumentException("parameter is null");
		
		try {
			final SocketChannel sChannel = SocketChannel.open();
			sChannel.configureBlocking(false);
			
			if(sChannel.connect(adrs)) {
				final DispatcherHandler dHandler = new SocketImpl<X>(sChannel, sHandler, this.reactor);
				this.regQueue.add(new ConnectedConnector<X>(dHandler));
			} else {
				final DispatcherHandler dHandler = new SocketImpl<X>(sChannel, sHandler, this.reactor);
				this.regQueue.add(new PendingConnector<X>(dHandler, adrs, callback));
			}
			
			/*
			 * There is a possibility that a thread may try to open a socket at the same
			 * time that another thread is closing the reactor. We need to make 100% sure
			 * that all sockets are closed properly and the user's are properly notified.
			 */
			if(this.isShutdown) {
				/*
				 * At this point there is a chance that the Dispatcher's driving thread
				 * has already completed. This Implies that there is a chance that the
				 * object we just added to the refQueue will never be closed. So it'll
				 * need to be done in this thread in such a way that the user will get
				 * notified of the close exactly once. This is where the cleanQueue()
				 * method comes in.
				 */
				this.cleanQueue();
			} else
				this.selector.wakeup();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
			assert callback!=null;
			callback.onComplete(adrs, ConnectCallbackResult.CONNECTION_FAILED);
		}
	}
	
	private void register() {
		/* Run on dispatcher thread */
		
		Connector<?> conn;
		while(!this.regQueue.isEmpty()) {
			if((conn=regQueue.poll())==null) continue; 
			conn.register();
		}
	}

	@Override
	public void run() {
		
		try {
			while(!this.isShutdown) {
				register();
				this.selector.select();
				
				Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
				while(it.hasNext()) {
					SelectionKey key = it.next();
					if(key.isValid()) {
						try {
							switch(key.readyOps()) {
							case SelectionKey.OP_CONNECT: {
								final PendingConnector<?> pc = (PendingConnector<?>) key.attachment();
								pc.finishConnect(key);
								break;
							}
							case SelectionKey.OP_READ: {
								final DispatcherHandler dHandler = (DispatcherHandler)key.attachment();
								dHandler.onRead();
								break;
							}
							case SelectionKey.OP_WRITE: {
								final DispatcherHandler dHandler = (DispatcherHandler)key.attachment();
								dHandler.onWrite();
								break;
							}
							case (SelectionKey.OP_READ | SelectionKey.OP_WRITE): {
								final DispatcherHandler dHandler = (DispatcherHandler)key.attachment();
								dHandler.onRead();
								dHandler.onWrite();
								break;
							}
							default:
								assert false;
							}
							
						} catch (CancelledKeyException cke) {
							cke.printStackTrace();
							// Do nothing
						} finally {
							it.remove();
						}
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.clean();
		}
	}
	
	private final void clean() {
		for(SelectionKey key : this.selector.keys()) {
			try {
				((Closeable)key.attachment()).close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			this.selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cleanQueue();
	}
	
	private final void cleanQueue() {
		Closeable c;
		while(!this.regQueue.isEmpty()) {
			if((c=regQueue.poll())==null) continue;
			
			try {
				c.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public final NioReactor getReactor() {
		return this.reactor;
	}
	
	private interface Connector<X> extends Closeable {
		public void register();
	}
	
	private final class PendingConnector<X> implements Connector<X> {
		private final DispatcherHandler dHandler;
		GearmanCallbackHandler<InetSocketAddress, ConnectCallbackResult> callback;
		InetSocketAddress adrs;
		
		public PendingConnector(DispatcherHandler dHandler, InetSocketAddress adrs, GearmanCallbackHandler<InetSocketAddress, ConnectCallbackResult> callback){
			this.dHandler = dHandler;
			this.callback = callback;
		}
		
		@Override
		public final void register() {
			try {
				dHandler.getSelectableChannel().register(selector, SelectionKey.OP_CONNECT, this);
			} catch (ClosedChannelException cce) {
				cce.printStackTrace();
				assert callback!=null;
				callback.onComplete(adrs, ConnectCallbackResult.SERVICE_SHUTDOWN);
			}
		}
		
		public final void finishConnect(final SelectionKey key) {
			try {
				key.interestOps(0);
				dHandler.getSelectableChannel().finishConnect();
				key.attach(dHandler);
				dHandler.onComplete(key);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				key.attach(dHandler);
				key.cancel();
				callback.onComplete(adrs, ConnectCallbackResult.CONNECTION_FAILED);
			}
		}

		@Override
		public synchronized void close() throws IOException {
			/*
			 * If the dispatcher was closed before the socket was completely connected,
			 * throw exception, otherwise just close the socket 
			 */
			if(this.dHandler.isClosed()) return;
			
			try {
				dHandler.onComplete(null);
			} finally {
				callback.onComplete(adrs, ConnectCallbackResult.SERVICE_SHUTDOWN);
			}
		}

		@Override
		public boolean isClosed() {
			return dHandler.isClosed();
		}
	}
	
	private final class ConnectedConnector<X> implements Connector<X> {
		private final DispatcherHandler dHandler;
		
		public ConnectedConnector(DispatcherHandler dHandler) {
			this.dHandler = dHandler;
		}
		
		@Override
		public void register() {
			try {
				final SelectionKey key = dHandler.getSelectableChannel().register(selector, 0, dHandler);
				dHandler.onComplete(key);
			} catch (ClosedChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		@Override
		public void close() throws IOException {
			if(this.dHandler.isClosed()) return;
			
			this.dHandler.onComplete(null);
		}

		@Override
		public boolean isClosed() {
			return this.dHandler.isClosed();
		}
	}

	@Override
	public void close() {
		this.isShutdown=true;
		this.selector.wakeup();
	}

	@Override
	public boolean isClosed() {
		return this.isShutdown;
	}
	
	@Override
	protected final void finalize() throws Throwable {
		this.close();
	}
}
