package org.gearman.impl.data;

import org.gearman.GearmanJob;

public class GearmanJobImpl implements GearmanJob {
	private static final long serialVersionUID = 1L;
	
	private final String functionName;
	private final byte[] data;
	
	public GearmanJobImpl(final String functionName, byte[] data) {
		this.functionName = functionName;
		this.data = data;
	}
	
	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public byte[] getData() {
		return data;
	}

}
