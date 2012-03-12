/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.IOException;

/**
 * The gearman client is used to submit jobs to the job server.
 * @author isaiah
 */
public interface GearmanClient extends GearmanServerPool {
	
	/**
	 * A factory method for creating {@link GearmanJob} objects.
	 * @param function
	 * 		The function name
	 * @param data
	 * 		The function data
	 * @return
	 * 		A new {@link GearmanJob} instance
	 */
	public GearmanJob createJob(String function, byte[] data);
	
	/**
	 * A factory method for creating {@link GearmanJobSubmittal} objects
	 * @param serverID
	 * 		The server ID
	 * @param jobHandle
	 * 		The job handle
	 * @return
	 * 		A new {@link GearmanJobSubmittal} object
	 */
	public GearmanJobSubmittal createJobSubmittal(String serverID, byte[] jobHandle);
	
	/**
	 * Polls for the job status. This is a blocking operation. The current thread may block and wait
	 * for the operation to complete
	 * @param submittal
	 * 		The {@link GearmanJobSubmittal} containing the information needed to query the correct
	 * 		server for the correct job
	 * @return
	 * 		The job status of the job in question.
	 * @throws IOException
	 * 		If an I/O exception occurs while performing this operation 
	 */
	public GearmanJobStatus getStatus(GearmanJobSubmittal submittal) throws IOException;
	
	/**
	 * Polls for the job status. This is a non-blocking operation. The current thread will <i>not</i>
	 * wait for the operation to complete
	 * @param submittal
	 * 		The {@link GearmanJobSubmittal} containing the information needed to query the correct
	 * 		server for the correct job
	 * @param callback
	 * 		The callback object used to notify the user of operations result  
	 */
	public void getStatus(GearmanJobSubmittal submittal, GearmanJobStatusCallback callback);
	
	/**
	 * Submits a background job. Background jobs are queued in the job server but are disjoint from
	 * the client. The result is not needed on the client side.<br>
	 * <br>
	 * This is a blocking operation. The current thread may block and
	 * wait for the operation to complete.
	 * @param job
	 * 		The job to submit.
	 * @return
	 * 		A {@link GearmanJobSubmittal} used to identify the job
	 * @throws IOException
	 * 		If an I/O exception occurs while performing this operation
	 */
	public GearmanJobSubmittal submitBackgroundJob(GearmanJob job) throws IOException;
	
	/**
	 * Submits a background job. Background jobs are queued in the job server but are disjoint from
	 * the client. The result is not needed on the client side.<br>
	 * <br>
	 * This is a blocking operation. The current thread may block and wait for the operation to complete.
	 * @param job
	 * 		The job to submit
	 * @param priority
	 * 		The priority of the job submitted
	 * @return
	 * 		A {@link GearmanJobSubmittal} used to identify the job
	 * @throws IOException
	 * 		If an I/O exception occurs while performing this operation
	 */
	public GearmanJobSubmittal submitBackgroundJob(GearmanJob job, GearmanJobPriority priority) throws IOException;
	
	/**
	 * Submits a background job. Background jobs are queued in the job server but are disjoint from
	 * the client. The result is not needed on the client side.<br>
	 * <br>
	 * This is a non-blocking operation. The current thread will <i>not</i> wait for the operation
	 * to complete.
	 * @param job
	 * 		The job to submit
	 * @param callback
	 * 		The callback object used to notify the user of operations result
	 */
	public void submitBackgroundJob(GearmanJob job, GearmanSubmitCallback callback);
	
	/**
	 * Submits a background job. Background jobs are queued in the job server but are disjoint from
	 * the client. The result is not needed on the client side.<br>
	 * <br>
	 * This is a non-blocking operation. The current thread will <i>not</i> wait for the operation
	 * to complete.
	 * @param job
	 * 		The job to submit
	 * @param callback
	 * 		The callback object used to notify the user of operations result
	 * @param priority
	 * 		The job's priority
	 */
	public void submitBackgroundJob(GearmanJob job, GearmanSubmitCallback callback, GearmanJobPriority priority);
	
	/**
	 * Submits a job. Non-background jobs are attached to a client. The result and any intermediate
	 * data will received by the client.<br>
	 * <br>
	 * This is a blocking operation. The current thread may block and wait for the operation to complete.
	 * @param job
	 * 		The job to submit
	 * @param handler
	 * 		A callback object for receiving data from the worker
	 * @return
	 * 		A {@link GearmanJobSubmittal} used to identify the job
	 * @throws IOException
	 * 		If an I/O exception occurs while performing this operation
	 */
	public GearmanJobSubmittal submitJob(GearmanJob job, GearmanJobHandler handler) throws IOException;
	
	/**
	 * Submits a job. Non-background jobs are attached to a client. The result and any intermediate
	 * data will received by the client.<br>
	 * <br>
	 * This is a blocking operation. The current thread may block and wait for the operation to complete.
	 * @param job
	 * 		The job to submit
	 * @param handler
	 * 		A callback object for receiving data from the worker
	 * @param priority
	 * 		The job's priority
	 * @return
	 * 		A {@link GearmanJobSubmittal} used to identify the job
	 * @throws IOException
	 * 		If an I/O exception occurs while performing this operation
	 */
	public GearmanJobSubmittal submitJob(GearmanJob job, GearmanJobHandler handler, GearmanJobPriority priority) throws IOException;
	
	/**
	 * Submits a job. Non-background jobs are attached to a client. The result and any intermediate
	 * data will received by the client.<br>
	 * <br>
	 * This is a non-blocking operation. The current thread will <i>not</i> wait for the operation
	 * @param job
	 * 		The job to submit
	 * @param handler
	 * 		A callback object for receiving data from the worker
	 * @param callback
	 * 		The callback object used to notify the user of operations result
	 */
	public void submitJob(GearmanJob job, GearmanJobHandler handler, GearmanSubmitCallback callback);
	
	/**
	 * Submits a job. Non-background jobs are attached to a client. The result and any intermediate
	 * data will received by the client.<br>
	 * <br>
	 * This is a non-blocking operation. The current thread will <i>not</i> wait for the operation
	 * @param job
	 * 		The job to submit
	 * @param handler
	 * 		A callback object for receiving data from the worker
	 * @param callback
	 * 		The callback object used to notify the user of operations result
	 * @param priority
	 * 		The job's priority
	 */
	public void submitJob(GearmanJob job, GearmanJobHandler handler, GearmanSubmitCallback callback, GearmanJobPriority priority);
}