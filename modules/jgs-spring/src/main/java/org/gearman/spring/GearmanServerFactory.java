package org.gearman.spring;

import org.gearman.Gearman;
import org.gearman.GearmanServer;
import org.gearman.impl.util.GearmanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * A factory bean that creates {@link GearmanServer} objects
 * @author isaiah
 */
public class GearmanServerFactory implements FactoryBean, InitializingBean {

	private Gearman gearman;
	private String host = "localhost";
	private Integer port = GearmanUtils.getPort();
	
	private GearmanServer server;
	
	@Override
	public GearmanServer getObject() throws Exception {
		return server;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.server = gearman.createGearmanServer(host, port);
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
