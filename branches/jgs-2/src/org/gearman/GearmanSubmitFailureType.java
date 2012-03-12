package org.gearman;

public enum GearmanSubmitFailureType {
	
	/**
	 * Failed to send the job due to there being no job servers to
	 * submit to.
	 */
	FAILED_NO_SERVER,
	
	/**
	 * Failed to connect to any of the available servers
	 */
	FAILED_TO_CONNECT,
	
	/**
	 * The {@link GearmanClient} is shutdown
	 */
	FAILED_SERVICE_SHUTDOWN;
}
