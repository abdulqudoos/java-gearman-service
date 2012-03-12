package org.gearman.util;

import org.gearman.core.GearmanConnection;

public class Util
{
	public static final String toString(GearmanConnection<?> conn) {
		return "["+conn.getHostAddress() + ":" + conn.getPort() +"]";
	}
}
