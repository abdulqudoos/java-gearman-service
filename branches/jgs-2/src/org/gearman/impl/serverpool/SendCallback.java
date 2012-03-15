package org.gearman.impl.serverpool;

import org.gearman.impl.core.GearmanCallbackHandler;
import org.gearman.impl.core.GearmanConnection.SendCallbackResult;
import org.gearman.impl.core.GearmanPacket;

class SendCallback implements GearmanCallbackHandler<GearmanPacket, SendCallbackResult> {
	private final GearmanCallbackHandler<GearmanPacket, SendCallbackResult> callback;
	
	SendCallback(GearmanCallbackHandler<GearmanPacket, SendCallbackResult> callback) {
		this.callback = callback;
	}
	
	@Override
	public void onComplete(GearmanPacket data, SendCallbackResult result) {
		if(!result.isSuccessful()) {
			// TODO log warlogger.log(Level.WARNING, "");
		}
		
		if(callback!=null)
			callback.onComplete(data, result);
	}
}
