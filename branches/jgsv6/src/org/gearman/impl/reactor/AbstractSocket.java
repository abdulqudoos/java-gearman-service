/*
 * Copyright (C) 2010 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman.impl.reactor;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

abstract class AbstractSocket<X> implements Socket<X> {

	private X att;

	@Override
	public final X getAttachment() {
		return this.att;
	}

	@Override
	public final InetAddress getInetAddress() {
		return this.getUnderlyingSocket().getInetAddress();
	}

	@Override
	public final boolean getKeepAlive() throws SocketException {
		return this.getUnderlyingSocket().getKeepAlive();
	}

	@Override
	public final InetAddress getLocalAddress() {
		return this.getUnderlyingSocket().getLocalAddress();
	}

	@Override
	public final int getLocalPort() {
		return this.getUnderlyingSocket().getLocalPort();
	}

	@Override
	public final SocketAddress getLocalSocketAddress() {
		return this.getUnderlyingSocket().getLocalSocketAddress();
	}

	@Override
	public final int getPort() {
		return this.getUnderlyingSocket().getPort();
	}

	@Override
	public final SocketAddress getRemoteSocketAddress() {
		return this.getUnderlyingSocket().getRemoteSocketAddress();
	}

	@Override
	public final int getSoLinger() throws SocketException {
		return this.getUnderlyingSocket().getSoLinger();
	}

	@Override
	public final boolean getTcpNoDelay() throws SocketException {
		return this.getUnderlyingSocket().getTcpNoDelay();
	}

	/**
	 * Returns the underlying {@link java.net.Socket} implementation
	 * 
	 * @return The underlying {@link java.net.Socket}
	 */
	protected abstract java.net.Socket getUnderlyingSocket();

	@Override
	public final void setAttachment(final X att) {
		this.att = att;
	}

	@Override
	public final void setKeepAlive(final boolean on) throws SocketException {
		this.getUnderlyingSocket().setKeepAlive(on);

	}

	@Override
	public final void setSoLinger(final boolean on, final int linger)
			throws SocketException {
		this.getUnderlyingSocket().setSoLinger(on, linger);
	}

	@Override
	public final void setTcpNoDelay(final boolean on) throws SocketException {
		this.getUnderlyingSocket().setTcpNoDelay(on);
	}
}
