/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

/**
 * Asynchronous callback handlers notify the user when an asynchronous operation completes,
 * successful or not.
 * 
 * @author isaiah
 */
public interface GearmanSubmitCallback {
	
	/**
	 * Called when the asynchronous operation completes successfully
	 * @param attachment
	 * 		The attachment object set by the user
	 * @param data
	 * 		The operations return value
	 */
	public void onSuccess(GearmanJob job, GearmanJobSubmittal submittal);
	
	/**
	 * Called when the asynchronous operation fails
	 * @param attachment
	 * 		The attachment object set by the user
	 * @param failureType
	 * 		An object describing why the operation failed
	 */
	public void onFailure(GearmanJob job, GearmanSubmitFailureType failureType);
}
