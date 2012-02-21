/*
 * Copyright (C) 2012 by Isaiah van der Elst <isaiah.v@comcast.net>
 * Use and distribution licensed under the BSD license.  See
 * the COPYING file in the parent directory for full text.
 */

package org.gearman;

import java.io.Serializable;

/**
 * An enumerator describing why a job failed
 * @author isaiah
 */
public enum GearmanJobFailureType implements Serializable{
	/** The job failed due to the server disconnecting */
	DISCONNECT_FAIL,
	/** The job failed due to the worker failing the job */
	WORKER_FAIL;
}
