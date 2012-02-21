package org.gearman.impl;

import org.gearman.GearmanProperty;

/**
 * Constant values used by java-gearman-service implementation
 * @author isaiah
 */
class GearmanImplProperty {
	
	/** The current version number. This value is updated a build time */
	public final static GearmanProperty VERSION = new GearmanProperty("gearman.version", "java-gearman-service@version.major@.@version.minor@.@version.build@"); 
	
	/** Logger name */
	public final static GearmanProperty LOGGER_NAME = new GearmanProperty("gearman.loggername", "gearman");
	
	/** The Charset used to encode strings */
	public final static GearmanProperty CHARSET = new GearmanProperty("gearman.charset","UTF-8");
	
	/** The thread timeout period */
	public final static GearmanProperty THREAD_TIMEOUT = new GearmanProperty("gearman.thread_timeout", "60000");
	
	public final static GearmanProperty PERSISTENCE = new GearmanProperty("gearman.persistence", "false");
	
	public final static GearmanProperty PERSISTENCE_CLASSNAME = new GearmanProperty("gearman.persistence.classname", "false");
	
	public final static GearmanProperty PERSISTENCE_JDBC_DRIVER = new GearmanProperty("gearman.persistence.jdbc_driver", "sun.jdbc.odbc.JdbcOdbcDriver");
	
	public final static GearmanProperty PERSISTENCE_TIMEOUT = new GearmanProperty("gearman.persistence.timeout","60000");
	
	public final static GearmanProperty PERSISTENCE_JDBC_URL = new GearmanProperty("gearman.persistence.jdbc_url", "Driver={PostgreSQL};Server=localhost;Port=5432;Database=gearman;");
	
	public final static GearmanProperty PERSISTENCE_JDBC_PROPERTIES = new GearmanProperty("gearman.persistence.jdbc_properties", "Uid=myUsername;Pwd=myPassword;");
}
