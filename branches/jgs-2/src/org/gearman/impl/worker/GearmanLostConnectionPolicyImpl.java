package org.gearman.impl.worker;

import org.gearman.GearmanLostConnectionAction;
import org.gearman.GearmanLostConnectionGrounds;
import org.gearman.GearmanLostConnectionPolicy;
import org.gearman.GearmanServer;

class GearmanLostConnectionPolicyImpl implements GearmanLostConnectionPolicy {

	@Override
	public GearmanLostConnectionAction lostRemoteServer(GearmanServer server, GearmanLostConnectionGrounds grounds) {
		return GearmanLostConnectionAction.RECONNECT;
	}
	
	@Override
	public void lostLocalServer(GearmanServer server, GearmanLostConnectionGrounds grounds) {
	}
}
