package org.gearman.impl;

import java.nio.charset.Charset;
import java.util.logging.Logger;

public class GearmanConstants {
	public static final String LOGGER_NAME = "gearman";
	public static final String CHARSET_NAME = "UTF-8";
	public static final int PORT = 4730;
	public static final String VERSION = "@project.name@ v@project.version@";
	public static final long THREAD_TIMEOUT = 60000L;
	public static final Logger LOGGER;
	public static final Charset CHARSET;
	
	static {
		LOGGER = Logger.getLogger(LOGGER_NAME);
		CHARSET = Charset.forName(CHARSET_NAME);
	}
}
