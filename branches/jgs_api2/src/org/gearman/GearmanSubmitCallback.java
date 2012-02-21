/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.IOException;

/**
 * Asynchronous callback handlers notify the user when an asynchronous operation completes,
 * successful or not.
 * 
 * @author isaiah
 * 
 * @param <A>
 * 		attachment type
 * @param <D>
 * 		return type
 */
public interface GearmanSubmitCallback {
	
	/**
	 * Called when the asynchronous operation completes successfully
	 * @param attachment
	 * 		The attachment object set by the user
	 * @param data
	 * 		The operations return value
	 */
	public void onSuccess(GearmanJob job, GearmanSubmittal submittal);
	
	/**
	 * Called when the asynchronous operation fails
	 * @param attachment
	 * 		The attachment object set by the user
	 * @param failureType
	 * 		An object describing why the operation failed
	 */
	public void onFailure(GearmanJob job, IOException ioe);
}
