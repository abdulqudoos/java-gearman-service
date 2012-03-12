/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Both {@link GearmanClient}s and {@link GearmanWorker}s are <code>GearmanServerPool</code>s.
 * A gearman server pool allows the user manage the servers within a particular service 
 * @author isaiah
 */
public interface GearmanServerPool extends GearmanService {
	/**
	 * Adds a {@link GearmanServer} to the service.<br>
	 * <br>
	 * Note: connections are not made to the server at this time. A connection is only established when needed
	 * @param server
	 * 		The gearman server to add
	 * @return
	 * 		<code>true</code> if the server was added to the service
	 */
	public boolean addServer(GearmanServer server);
	
	/**
	 * Returns the default reconnect period
	 * @param unit
	 * 		The time unit
	 * @return
	 * 		The about of time before the service attempts to reconnect to a disconnected server
	 */
	public long getReconnectPeriod(TimeUnit unit);
	
	/**
	 * Returns the number of servers managed by this service
	 * @return
	 * 		The number of servers managed by this service
	 */
	public int getServerCount();
	
	/**
	 * Removes all servers from this service
	 */
	public void removeAllServers();
	
	/**
	 * Attempts to remove a server from this service
	 * @param serviceId
	 * 		The service ID of the server to remove
	 * @return
	 * 		<code>true</code> if the server was removed. <code>false</code> if the given service id was not found
	 */
	public boolean removeServer(String serverID);
	
	public boolean removeServer(GearmanServer server);
	public void setClientID(String id);
	public String getClientID();
	public boolean hasServer(GearmanServer server);
	
	/**
	 * A synchronizing method used to block the current thread until at least one server
	 * is connectable.<br>
	 * <br>
	 * This method will only block if all available servers are waiting due to a lost connection.
	 * The method will unblock if:<br>
	 * 		1) A server's reconnect period has elapsed and can<br>
	 * 		2) A server is added to the service<br>
	 * 		3) All servers are removed from the service<br>
	 * 
	 * @see GearmanServerPool#setReconnectPeriod(long, TimeUnit)
	 * @see GearmanServerPool#setLostConnectionPolicy(GearmanLostConnectionPolicy)
	 * @throws InterruptedException
	 * 		If the thread is interrupted
	 */
	public void waitReconnect() throws InterruptedException;
	
	/**
	 * A synchronizing method used to block the current thread until at least one server
	 * is connectable.<br>
	 * <br>
	 * This method will only block if all available servers are waiting due to a lost connection.
	 * The method will unblock if:<br>
	 * 		1) A server's reconnect period has elapsed<br>
	 * 		2) A server is added to the service<br>
	 * 		3) All servers are removed from the service<br>
	 * 
	 * 		If the thread is interrupted
	 * @param timeout
	 * 		
	 * @see GearmanServerPool#setReconnectPeriod(long, TimeUnit)
	 * @see GearmanServerPool#setLostConnectionPolicy(GearmanLostConnectionPolicy)
	 * @throws InterruptedException
	 */
	public void waitReconnect(long timeout) throws InterruptedException;
	
	/**
	 * Returns the collection of servers this service is managing
	 * @return
	 * 		The collection of servers this service is managing
	 */
	public Collection<GearmanServer> getServers();
	
	/**
	 * Returns the set of server IDs used by this service
	 * @return
	 * 		The set of server IDs used by this service
	 */
	public Collection<String> getServerIDs();
	
	/**
	 * Sets the {@link GearmanLostConnectionPolicy}. The lost connection policy describes
	 * what should be done in the event that the server unexpectedly disconnects
	 * @param policy
	 * 		The policy for handling unexpected disconnects
	 */
	public void setLostConnectionPolicy(GearmanLostConnectionPolicy policy);
	
	/**
	 * Sets the default reconnect period. When a connection is unexpectedly disconnected, the
	 * will wait a period of time before attempting to reconnect unless otherwise specified
	 * by the {@link GearmanLostConnectionPolicy}
	 * @param time
	 * 		The amount of time before a reconnect is attempted unless otherwise specified
	 * 		by the {@link GearmanLostConnectionPolicy}
	 * @param unit
	 * 		The time unit
	 */
	public void setReconnectPeriod(long time, TimeUnit unit);
	
	
	/**
	 * Tests if a server with the given ServerID is held within this service
	 * @param serverID
	 * 		The server ID of the {@link GearmanServer} we're looking for
	 * @return
	 * 		<code>true</code> if this service contains the {@link GearmanServer} 
	 */
	public boolean hasServer(String serverID);
}
