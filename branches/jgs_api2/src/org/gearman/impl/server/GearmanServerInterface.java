package org.gearman.impl.server;

import org.gearman.core.GearmanCallbackHandler;
import org.gearman.core.GearmanConnectionHandler;
import org.gearman.core.GearmanConnectionManager.ConnectCallbackResult;

public interface GearmanServerInterface extends org.gearman.GearmanServer {
	public <A> void createGearmanConnection(GearmanConnectionHandler<A> handler, GearmanCallbackHandler<GearmanServerInterface, ConnectCallbackResult> failCallback);
}