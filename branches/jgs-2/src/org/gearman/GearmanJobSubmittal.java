/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.Serializable;

/**
 * An object representing a job submission
 * @author isaiah
 */
public interface GearmanJobSubmittal extends Serializable {
	/**
	 * The ID to the {@link GearmanServer} that the job was sent to
	 * @return
	 * 		The server ID
	 */
	public String getServerID();
	
	/**
	 * The job handle assigned by the {@link GearmanServer}
	 * @return
	 * 		The job handle
	 */
	public byte[] getJobHandle();
}
