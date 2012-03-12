/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.util.Set;

/**
 * A Gearman Worker is responsible for executing jobs it receives from the Job
 * Server. A Worker registers with the Job Server the types of jobs that it may
 * execute, the server will use this information, along with other attributes,
 * to determine which Worker will execute a particular job request. As data is
 * generated or as a job's state changes, the worker passes this information
 * back to the Job Server.
 */
public interface GearmanWorker extends GearmanServerPool {

    /**
     * Sets the maximum number of jobs that can execute at a given time
     * @param maxConcurrentJobs
     * 		The maximum number of jobs that can execute at a given time
     */
    public void setMaximumConcurrency(int maxConcurrentJobs);
    
    /**
     * The maximum number of jobs that may execute at a given time 
     * @return
     * 		The number of jobs that that may execute concurrently 
     */
    public int getMaximumConcurrency();
    
    /**
     * Registers a particular {@link GearmanFunction} with the worker. Once a
     * function has been registered with a worker, the worker is capable of
     * executing any {@link org.gearman.GearmanJob} that matches the
     * registered function. Upon registering a function, the Worker notifies all
     * Gearman Job Servers that is can accept any job that contains the
     * applicable function.
     *
     * @param name
     * 		The gearman function name
     * @param function
     * 		The function being registered with the worker.
     * @return
     * 		The gearman function who was previously assigned to the given function name
     */
    public GearmanFunction addFunction(String name, GearmanFunction function);
    
    /**
     * Returns the gearman function associated with the given function name
     * @param name
     * 		The function name
     * @return
     * 		The gearman function registered with the given function name
     */
    public GearmanFunction getFunction(String name);
    
    /**
     * Retrieve the names of all functions that have been registered with this
     * worker. If no functions have been registered, any empty set should be
     * returned.
     *
     * @return The name of all registered functions.
     */
    public Set<String> getRegisteredFunctions();
    
    /**
     * Unregisters a particular {@link GearmanFunction} from the worker. Once
     * a function has been unregistered from the Worker, a Worker will no longer
     * accept jobs which require the execution of the unregistered function.
     *
     * @param functionName
     * 		The name of the function to unregister
     */
    public boolean removeFunction(String functionName);

    /**
     * Unregisters all{@link GearmanFunction} from the worker. The effect of
     * which is that the worker will not execute any new jobs.
     */
    public void removeAll();
}
