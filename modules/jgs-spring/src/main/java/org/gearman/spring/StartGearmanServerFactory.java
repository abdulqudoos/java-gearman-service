package org.gearman.spring;

import org.gearman.Gearman;
import org.gearman.GearmanPersistence;
import org.gearman.GearmanServer;
import org.gearman.impl.util.GearmanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

public class StartGearmanServerFactory implements FactoryBean, InitializingBean {

	private Gearman gearman;
	private int port = GearmanUtils.getPort();
	private GearmanPersistence gearmanPersistence;
	
	private GearmanServer server;

	@Override
	public GearmanServer getObject() throws Exception {
		return server;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(gearmanPersistence!=null)
			this.server = gearman.startGearmanServer(port, gearmanPersistence);
		else
			this.server = gearman.startGearmanServer(port);
	}

	@Override
	public Class<GearmanServer> getObjectType() {
		return GearmanServer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public Gearman getGearman() {
		return gearman;
	}

	@Required
	public void setGearman(Gearman gearman) {
		this.gearman = gearman;
	}

	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
}
