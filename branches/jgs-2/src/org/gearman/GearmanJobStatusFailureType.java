package org.gearman;

public enum GearmanJobStatusFailureType {
	SERVER_NOT_AVAILABLE,
	SERVER_DROPPED,
	CONNECTION_FAILED,
	SERVER_DISCONNECTED,
	SEND_FAILED;
}
