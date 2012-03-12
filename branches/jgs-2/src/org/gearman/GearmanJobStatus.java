/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.Serializable;

/**
 * An immutable object specifying a job's status at a 
 * @author isaiah
 */
public interface GearmanJobStatus extends Serializable {
	
	/**
	 * Returns the job handle assigned by the server when the job was created
	 * @return
	 * 		The job handle assigned by the server when the job was created
	 */
	public byte[] getJobHandle();
	
	/**
	 * Returns the time of day that the job status response was received (client side)
	 * @return
	 * 		The time of day the job status response was received
	 */
	public long getResponseTime();
	
	/**
	 * Tests if the server knew the status of the job in question.
	 * 
	 * Most job servers will return unknown if it never received the job or
	 * if the job has already been completed.
	 * 
	 * If the status is known but not running, then a worker has not yet
	 * polled the job
	 * 
	 * @return
	 * 		<code>true</code> if the server knows the status of the job in question
	 */
	public boolean isKnown();
	
	/**
	 * Tests if the job is currently running.
	 * 
	 * If the status is unknown, this value will always be false.
	 *  
	 * @return
	 * 		<code>true</code> if the job is currently being worked on. <code>
	 * 		false</code> otherwise.
	 */
	public boolean isRunning();
	
	/**
	 * The percent complete numerator.
	 * @return
	 * 		the percent complete numerator.
	 */
	public long getNumerator();
	
	/**
	 * The percent complete denominator.
	 * @return
	 * 		the percent complete denominator.
	 */
	public long getDenominator();
}