package org.gearman.impl.client;

import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobEventType;

public class GearmanJobEventImpl implements GearmanJobEvent {

	public static final GearmanJobEventImpl GEARMAN_EOF = new GearmanJobEventImpl(GearmanJobEventType.GEARMAN_EOF, null);
	public static final GearmanJobEventImpl GEARMAN_SUBMIT_FAIL_CONNECTION_FAILED = new GearmanJobEventImpl(GearmanJobEventType.GEARMAN_SUBMIT_FAIL_CONNECTION_FAILED, null);
	public static final GearmanJobEventImpl GEARMAN_SUBMIT_FAIL_SERVER_NOT_AVAILABLE = new GearmanJobEventImpl(GearmanJobEventType.GEARMAN_SUBMIT_FAIL_SERVER_NOT_AVAILABLE, null);
	public static final GearmanJobEventImpl GEARMAN_JOB_DISCONNECT = new GearmanJobEventImpl(GearmanJobEventType.GEARMAN_JOB_DISCONNECT, null);
	public static final GearmanJobEventImpl GEARMAN_JOB_FAIL = new GearmanJobEventImpl(GearmanJobEventType.GEARMAN_JOB_FAIL, null);
	
	private final GearmanJobEventType type;
	private final byte[] data;
	
	public GearmanJobEventImpl(GearmanJobEventType type, byte[] data) {
		this.type = type;
		this.data = data;
	}
	
	@Override
	public GearmanJobEventType getEventType() {
		return type;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public boolean hasData() {
		return data!=null;
	}

}
