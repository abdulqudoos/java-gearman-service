/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.IOException;

/**
 * A <code>Gearman</code> object defines a gearman systems and creates gearman
 * services. These services include {@link GearmanWorker}s,
 * {@link GearmanClient}s, and {@link GearmanServer}s. All services created by
 * the same <code>Gearman</code> object are said to be in the same system, and
 * all services in the same system share system wide thread resources.
 * 
 * @author isaiah.v
 */
public abstract class Gearman implements GearmanService{
	
	private static final String GEARMAN_CLASSNAME = "org.gearman.impl.GearmanImpl";
	
	/**
	 * Creates a new {@link Gearman} instance
	 * @return
	 * 		A new {@link Gearman} instance  
	 */
	public static Gearman createGearman() {
		
		try {
			final Class<?> implClass = Class.forName(GEARMAN_CLASSNAME);
			final Object o = implClass.newInstance();
			
			return (Gearman)o;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			
			/*
			 * If an exception occurs, there is something wrong with the class name or gearman
			 * implementation. In both cases the issue is unrecoverable. An error must be thrown 
			 */
			
			throw new Error("failed to initialize gearman",e);
		}
	}
	
	/**
	 * Returns the current java-gearman-service version
	 * @return
	 * 		The current version
	 */
	public abstract String getVersion();
	
	/**
	 * Creates a new local gearman job server running in the current address space. 
	 * @param ports
	 * 		The port numbers this server should listen on. If no port number is given, the
	 * 		default will be used
	 * @return
	 * 		A new gearman server instance
	 * @throws IOException
	 * 		If an IO exception occurs while attempting to open any of the given ports
	 */
	public abstract GearmanServer createGearmanServer(int port) throws IOException;
	
	/**
	 * Creates a new local gearman job server running in the current address space. 
	 * @param ports
	 * 		The port numbers this server should listen on. If no port number is given, the
	 * 		default will be used
	 * @param persistence
	 * 		An object providing the logic   
	 * @return
	 * 		A new gearman server instance
	 * @throws IOException
	 * 		If an IO exception occurs while attempting to open any of the given ports
	 */
	public abstract GearmanServer createGearmanServer(int port, GearmanPersistence persistence) throws IOException;
	
	/**
	 * Creates an object representing a remote gearman job server
	 * @param address
	 * 		The address of the remote server
	 * @return
	 * 		An object representing a remote gearman job server
	 */
	public abstract GearmanServer createGearmanServer(String host, int port);
	
	/**
	 * Creates a new gearman worker
	 * @return
	 * 		A new gearman worker
	 */
	public abstract GearmanWorker createGearmanWorker();
	
	/**
	 * Creates a new {@link GearmanClient}
	 * @return
	 * 		a new gearman client
	 */
	public abstract GearmanClient createGearmanClient();
}
