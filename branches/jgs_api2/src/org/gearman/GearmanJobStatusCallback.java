/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.IOException;

/**
 * A callback hander for the operation, {@link GearmanClient#getStatus(GearmanSubmittal, GearmanJobStatusCallback)}
 * @author isaiah
 */
public interface GearmanJobStatusCallback {
	
	/**
	 * Called on a successful operation.
	 * @param submittal
	 * 		The job submittal the operation was called with
	 * @param status
	 * 		The returned job status
	 */
	public void onSuccess(GearmanSubmittal submittal, GearmanJobStatus status);
	
	/**
	 * Called on a failed operation.
	 * @param submittal
	 * 		The job submittal the operation was called with
	 * @param ioe
	 * 		An exception describing why the operation failed 
	 */
	public void onFailure(GearmanSubmittal submittal, IOException ioe);
}
