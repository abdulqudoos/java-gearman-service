package org.gearman.impl;

import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.gearman.GearmanProperty;

public class GearmanImplConstants {
	public static final int PORT;
	public static final String VERSION;
	public static final Logger LOGGER;
	public static final Charset CHARSET;
	public static final long THREAD_TIMEOUT;
	
	static {
		VERSION = getVersion();
		LOGGER = getLogger();
		PORT = getPort();
		CHARSET = getCharset();
		THREAD_TIMEOUT = getThreadTimeout();
	}
	
	private final static String getVersion() {
		return GearmanProperty.getPropertyValue(GearmanImplProperty.VERSION);
	}
	
	private final static Logger getLogger() {
		return Logger.getLogger(GearmanProperty.getPropertyValue(GearmanImplProperty.LOGGER_NAME));
	}
	
	private final static int getPort() {
		return Integer.parseInt(GearmanProperty.getPropertyValue(GearmanProperty.PORT));
	}
	
	public final static Charset getCharset() {
		return Charset.forName(GearmanProperty.getPropertyValue(GearmanImplProperty.CHARSET));
	}
	
	public final static long getThreadTimeout() {
		return Long.parseLong(GearmanProperty.getPropertyValue(GearmanImplProperty.THREAD_TIMEOUT));
	}
}
