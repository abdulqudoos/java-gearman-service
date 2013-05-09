package org.gearman.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.gearman.Gearman;
import org.gearman.GearmanPersistence;
import org.gearman.GearmanServer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

public class GearmanServerArrayFactory implements FactoryBean<GearmanServer[]>, InitializingBean {
	private GearmanServer[] serverArray;
	
	private Gearman gearman;
	private String servers;
	private String ports;
	private GearmanPersistence persistence;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Collection<GearmanServer> values = new HashSet<>();
		
		createGearmanServers(this.servers, values);
		startGearmanServers(ports, persistence, values);
		
		this.serverArray = values.toArray(new GearmanServer[values.size()]);
	}
	
	@Override
	public GearmanServer[] getObject() throws Exception {
		return serverArray;
	}
	
	@Override
	public Class<GearmanServer[]> getObjectType() {
		return GearmanServer[].class;
	}
	
	@Override
	public boolean isSingleton() {
		return false;
	}
	
	@Required
	public void setGearman(Gearman gearman) {
		this.gearman = gearman;
	}
	
	public void setServers(String servers) {
		this.servers = servers;
	}
	
	public void setPorts(String ports) {
		this.ports = ports;
	}
	
	public void setPersistence(GearmanPersistence persistence) {
		this.persistence = persistence;
	}
	
	private void createGearmanServers(String servers, Collection<GearmanServer> values) {
		if(servers==null || servers.isEmpty()) return;
		
		Collection<String> checker = new HashSet<>();
		String[] serversArray = servers.split(";");
		
		for(String serverUnit : serversArray) {
			serverUnit = serverUnit.trim();
			
			if(!checker.add(serverUnit)) continue;
			
			int index = serverUnit.indexOf(':');
			
			String host = serverUnit.substring(0, index);
			String portstr = serverUnit.substring(index+1, serverUnit.length());
			
			int port = Integer.parseInt(portstr);
			
			values.add(gearman.createGearmanServer(host, port));
		}
	}
	
	private void startGearmanServers(String ports, GearmanPersistence persistence,Collection<GearmanServer> values) throws IOException {
		if(ports==null || (ports=ports.trim()).isEmpty()) return;
		
		Collection<Integer> checker = new HashSet<>();
		String[] portArray = ports.split(";");
		
		for(String portUnit : portArray) {
			int port = Integer.parseInt(portUnit);
			
			if(!checker.add(port)) continue;
			
			if(persistence==null)
				values.add(gearman.startGearmanServer(port));
			else
				values.add(gearman.startGearmanServer(port, persistence));
		}
	}
}
