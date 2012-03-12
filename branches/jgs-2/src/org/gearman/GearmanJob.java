/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.Serializable;

/**
 * An object representing a gearman job
 * @author isaiah
 */
public interface GearmanJob extends Serializable {
	/**
	 * Returns the gearman function name
	 * @return
	 * 		The gearman function name
	 */
	public String getFunctionName();
	
	/**
	 * Returns the job's payload
	 * @return
	 * 		the payload
	 */
	public byte[] getData();
}
