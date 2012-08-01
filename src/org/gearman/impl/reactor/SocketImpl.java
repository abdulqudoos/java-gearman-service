/*
 * Copyright (C) 2010 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman.impl.reactor;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

final class SocketImpl<X> extends AbstractSocket<X> implements DispatcherHandler {

	private final class WriteEvent<A> implements Runnable {
		public final ByteBuffer data;
		public final A attachment;
		public final CompletionHandler<ByteBuffer, A> handler;
		
		public Throwable th = null;
		
		public WriteEvent(final ByteBuffer data, A att, CompletionHandler<ByteBuffer, A> handler) {
			this.data = data;
			this.attachment = att;
			this.handler = handler;
		}

		@Override
		public void run() {
			if(th!=null)
				handler.onFailure(th, attachment);
			else
				handler.onComplete(data, attachment);
		}
	}
	
	private final NioReactor reactor; 
	private final SocketChannel sChannel;
	
	private final SocketHandler<X> sHandler;
	
	private final Queue<WriteEvent<?>> writeEvents = new ConcurrentLinkedQueue<WriteEvent<?>>();
	private SelectionKey key;
	
	private ByteBuffer buffer;
	private int bytes;
	
	private final Runnable onAccept = new Runnable() {
		public void run() {
			assert SocketImpl.this.sHandler!=null;
			assert SocketImpl.this.key!=null;
			
			SocketImpl.this.sHandler.onAccept(SocketImpl.this);
			SocketImpl.this.markReadable();
		}
	};
	
	private final Runnable onDisconnect = new Runnable() {
		public void run() {
			try {
				SocketImpl.this.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	private final Runnable onRead = new Runnable() {
		public void run() {
			assert SocketImpl.this.sHandler!=null;
			assert SocketImpl.this.key!=null;
			assert (SocketImpl.this.key.interestOps() & SelectionKey.OP_READ) == 0;
			
			SocketImpl.this.sHandler.onRead(bytes, SocketImpl.this);
			SocketImpl.this.markReadable();
		}
	};
	
	public SocketImpl(final SocketChannel sChannel, final SocketHandler<X> sHandler, final NioReactor reactor) {
		this.sChannel = sChannel;
		try {
			sChannel.socket().setTcpNoDelay(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sHandler = sHandler;
		this.reactor = reactor;
		
		if((this.buffer=sHandler.createSocketBuffer())==null) {
			this.buffer = ByteBuffer.allocateDirect(1024);
		}
	}

	@Override
	public void close() throws IOException {
		synchronized(this.sChannel) {
			if(!this.sChannel.isOpen()) return;
			this.sChannel.close();
		}
		
		this.sHandler.onDisconnect(this);
	}

	@Override
	public ByteBuffer getByteBuffer() {
		return this.buffer;
	}

	@Override
	public final SocketChannel getSelectableChannel() {
		/* Runs on dispatcher thread */
		return this.sChannel;
	}

	@Override
	protected Socket getUnderlyingSocket() {
		return this.sChannel.socket();
	}

	@Override
	public boolean isClosed() {
		return !this.sChannel.isOpen();
	}
	
	private final void markNotReadable() {
		assert this.key!=null;
		
		try {
			synchronized(this.key) {
				this.key.interestOps(this.key.interestOps() & (~SelectionKey.OP_READ));
			}
		} catch (CancelledKeyException cke) {
			cke.printStackTrace();
			/*
			 * There is nothing to do here. If this exception has been thrown, it's because the
			 * client has disconnected or been disconnected. The catch is simple to prevent the
			 * the stack trace from being printed.
			 */
		}
		this.key.selector().wakeup();
	}
	
	private final void markNotWritable() {
		assert this.key!=null;
		
		try {
			synchronized(this.key) {
				this.key.interestOps(this.key.interestOps() & (~SelectionKey.OP_WRITE));
			}
		} catch (CancelledKeyException cke) {
			cke.printStackTrace();
			/*
			 * There is nothing to do here. If this exception has been thrown, it's because the
			 * client has disconnected or been disconnected. The catch is simple to prevent the
			 * the stack trace from being printed.
			 */
		}
		this.key.selector().wakeup();
	}
	
	private final void markReadable() {
		assert this.key!=null;
		
		try {
			synchronized(this.key) {
				this.key.interestOps(this.key.interestOps() | SelectionKey.OP_READ);
			}
		} catch (CancelledKeyException cke) {
			cke.printStackTrace();
			/*
			 * There is nothing to do here. If this exception has been thrown, it's because the
			 * client has disconnected or been disconnected. The catch is simple to prevent the
			 * the stack trace from being printed.
			 */
		}
		
		this.key.selector().wakeup();
		
	}
	
	private final void markWritable() {
		assert this.key!=null;
		
		try {
			synchronized(this.key) {
				this.key.interestOps(this.key.interestOps() | SelectionKey.OP_WRITE);
			}
		} catch (CancelledKeyException cke) {
			cke.printStackTrace();
			/*
			 * There is nothing to do here. If this exception has been thrown, it's because the
			 * client has disconnected or been disconnected. The catch is simple to prevent the
			 * the stack trace from being printed.
			 */
		}
		this.key.selector().wakeup();
	}
	
	@Override
	public final void onComplete(final SelectionKey key) throws IOException {
		
		if(key==null) {
			synchronized(this.sChannel) {
				this.sChannel.close();
			}
		} else {
			this.key = key;
			this.reactor.getWorkerPool().execute(this.onAccept);
		}
	}
	
	@Override
	public final void onRead() {
		try {
			if(!this.sChannel.isOpen()) return;
			
			this.bytes =  this.sChannel.read(buffer);
			if(bytes==-1) {
				this.close();
				this.reactor.getWorkerPool().execute(this.onDisconnect);
				return;
			}
			
			SocketImpl.this.markNotReadable();
			this.reactor.getWorkerPool().execute(this.onRead);
			
		} catch (NotYetConnectedException nyce){
			nyce.printStackTrace();
			// If a read event comes in, the socket should be connected
			assert false;
		} catch(ClosedByInterruptException cbie) {
			cbie.printStackTrace();
			// Should never end up here
			assert false;
		} catch( AsynchronousCloseException ace) {
			// Do nothing, allow other thread to close the socket
			ace.printStackTrace();
		} catch (ClosedChannelException cce) {
			// It is possible for the connection to be closed before the read method is called.  But this is normal, do nothing
			cce.printStackTrace();
		} catch (IOException e) {
			// TODO do something with this
			try {
				this.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	@Override
	public void setByteBuffer(ByteBuffer buffer) {
		if(buffer==null) throw new IllegalArgumentException("null buffer");
		this.buffer = buffer;
	}
	
	@Override
	public final void onWrite() {
		
		synchronized(this.writeEvents) {
			if(this.writeEvents.isEmpty()) {
				this.markNotWritable();
				return;
			}
		}
		
		final WriteEvent<?> event=this.writeEvents.peek();
		assert event != null;
		
		try {
			
			this.sChannel.write(event.data);
			if(!event.data.hasRemaining()) {
				final Object o = this.writeEvents.poll();
				assert event.equals(o);
				assert event.handler!=null;
				
				SocketImpl.this.reactor.getWorkerPool().execute(event);
			}
			
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			
			final Object o = this.writeEvents.poll();
			assert event.equals(o);
			
			event.th = ioe;
			SocketImpl.this.reactor.getWorkerPool().execute(event);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
	}

	@Override
	public <A> void write(ByteBuffer data, A att, CompletionHandler<ByteBuffer, A> handler) {
		this.writeEvents.add(new WriteEvent<A>(data, att, handler));
		
		synchronized(this.writeEvents) {
			this.markWritable();
		}
	}
}
