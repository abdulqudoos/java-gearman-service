/*
 * Copyright (C) 2010 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman.impl.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

interface DispatcherHandler extends Closeable {
	public void onRead();
	public void onWrite();
	public void onComplete(SelectionKey key) throws IOException;
	public SocketChannel getSelectableChannel();
}
