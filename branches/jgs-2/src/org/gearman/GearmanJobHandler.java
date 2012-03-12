/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

/**
 * An interface implemented by the user to handle incoming result data. This is the client-side
 * equivalent for the {@link GearmanFunctionCallback}.
 *    
 * @author isaiah.v
 */
public interface GearmanJobHandler {
	
	/**
	 * The warning callback channel. This is just like the data callback channel,
	 * but its purpose is to send warning information.<br>
	 * <br>
	 * When the {@link GearmanFunction} receives a GearmanJob, this method can
	 * used to send information to the client while the job is executing<br>
	 * <br>
	 * Client Side: Override this method to define how information received on
	 * this callback channel should be handled. If this method is not
	 * overwritten, the information will be dropped.<br>
	 * <br>
	 * Worker Side: This method will throw a IllegalStateException if the job
	 * has completed. Only while the job is executing can the callback methods
	 * be used.<br>
	 * 
	 * @param submittal
	 * 		
	 * @param data
	 *            Information to send on the data callback channel
	 */
	public void callbackWarning(GearmanJobSubmittal submittal, byte[] warning);
	
	/**
	 * The data callback channel.<br>
	 * <br>
	 * When the {@link GearmanFunction} receives a GearmanJob, this method can
	 * used to send information to the client while the job is executing<br>
	 * <br>
	 * Client Side: Override this method to define how information received on
	 * this callback channel should be handled. If this method is not
	 * overwritten, the information will be dropped.<br>
	 * <br>
	 * Worker Side: This method will throw a IllegalStateException if the job
	 * has completed. Only while the job is executing can the callback methods
	 * be used.<br>
	 * 
	 * @param data
	 *            Information to send on the data callback channel
	 */
	public abstract void callbackData(GearmanJobSubmittal submittal, byte[] data);
	
	/**
	 * Updates the client of the job progress.<br>
	 * <br>
	 * When the {@link GearmanFunction} receives a GearmanJob, this method can
	 * used to send information about the jobs progress back to the client.<br>
	 * <br>
	 * Client Side: Override this method to define how information received on
	 * this callback channel should be handled. If this method is not
	 * overwritten, the information will be dropped.<br>
	 * <br>
	 * Worker Side: This method will throw a IllegalStateException if the job
	 * has completed. Only while the job is executing can the callback methods
	 * be used.<br>
	 * @param numerator
	 * 		A number typically specifying the numerator in the fraction work that's
	 * 		completed 
	 * @param denominator
	 * 		A number typically specifying the denominator in the fraction work that's
	 * 		completed
	 */
	public abstract void callbackStatus(GearmanJobSubmittal submittal, long numerator, long denominator);
	
	/**
	 * Called when the asynchronous operation completes successfully
	 * @param attachment
	 * 		The attachment object set by the user
	 * @param data
	 * 		The operations return value
	 */
	public void onSuccess(GearmanJobSubmittal job, byte[] data);
	
	/**
	 * Called when the asynchronous operation fails
	 * @param attachment
	 * 		The attachment object set by the user
	 * @param failureType
	 * 		An object describing why the operation failed
	 */
	public void onFailure(GearmanJobSubmittal job, GearmanJobFailureType failureType);
}
