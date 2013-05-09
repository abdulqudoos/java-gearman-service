package org.gearman.spring;

import java.util.concurrent.TimeUnit;

import org.gearman.Gearman;
import org.gearman.GearmanLostConnectionPolicy;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

public class GearmanWorkerFactory implements FactoryBean<GearmanWorker>, InitializingBean {

	private Gearman gearman;
	private GearmanServer[] servers;
	private GearmanFunctionBean[] functions;
	private Integer maximumConcurrency;
	private Long reconnectPeriod;
	private GearmanLostConnectionPolicy lostConnectionPolicy;
	private String clientId;
	
	private GearmanWorker worker;
	
	@Override
	public GearmanWorker getObject() throws Exception {
		return worker;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		GearmanWorker worker = gearman.createGearmanWorker();
		
		if(servers!=null) for(GearmanServer server : servers) {
			worker.addServer(server);
		}
		
		if(functions!=null) for(GearmanFunctionBean function : functions) {
			worker.addFunction(function.getName(), function.getFunction());
		}
		
		if(maximumConcurrency!=null) worker.setMaximumConcurrency(maximumConcurrency);
		if(reconnectPeriod!=null) worker.setReconnectPeriod(reconnectPeriod, TimeUnit.MILLISECONDS);
		if(lostConnectionPolicy!=null) worker.setLostConnectionPolicy(lostConnectionPolicy);
		if(clientId!=null) worker.setClientID(clientId);
		
		this.worker = worker;
	}

	@Override
	public Class<GearmanWorker> getObjectType() {
		return GearmanWorker.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Required
	public void setGearman(Gearman gearman) {
		this.gearman = gearman;
	}

	@Required
	public void setServers(GearmanServer[] servers) {
		this.servers = servers;
	}

	@Required
	public void setFunctions(GearmanFunctionBean[] functions) {
		this.functions = functions;
	}

	public void setMaximumConcurrency(Integer maximumConcurrency) {
		this.maximumConcurrency = maximumConcurrency;
	}

	public void setReconnectPeriod(Long reconnectPeriod) {
		this.reconnectPeriod = reconnectPeriod;
	}

	public void setLostConnectionPolicy(GearmanLostConnectionPolicy lostConnectionPolicy) {
		this.lostConnectionPolicy = lostConnectionPolicy;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
}
