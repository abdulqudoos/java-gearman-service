package org.gearman.spring;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gearman.Gearman;
import org.gearman.GearmanLostConnectionPolicy;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

public class GearmanWorkerFactory implements FactoryBean, InitializingBean {

	private Gearman gearman;
	private List<GearmanServer> gearmanServers;
	private List<GearmanFunctionBean> gearmanFunctions;
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
		
		if(gearmanServers!=null) for(GearmanServer server : gearmanServers) {
			worker.addServer(server);
		}
		
		if(gearmanFunctions!=null) for(GearmanFunctionBean function : gearmanFunctions) {
			worker.addFunction(function.getFunctionName(), function.getFunction());
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

	public Gearman getGearman() {
		return gearman;
	}

	@Required
	public void setGearman(Gearman gearman) {
		this.gearman = gearman;
	}

	public List<GearmanServer> getGearmanServers() {
		return gearmanServers;
	}

	public void setGearmanServers(List<GearmanServer> gearmanServers) {
		this.gearmanServers = gearmanServers;
	}

	public List<GearmanFunctionBean> getGearmanFunctions() {
		return gearmanFunctions;
	}

	public void setGearmanFunctions(List<GearmanFunctionBean> gearmanFunctions) {
		this.gearmanFunctions = gearmanFunctions;
	}

	public Integer getMaximumConcurrency() {
		return maximumConcurrency;
	}

	public void setMaximumConcurrency(Integer maximumConcurrency) {
		this.maximumConcurrency = maximumConcurrency;
	}

	public Long getReconnectPeriod() {
		return reconnectPeriod;
	}

	public void setReconnectPeriod(Long reconnectPeriod) {
		this.reconnectPeriod = reconnectPeriod;
	}

	public GearmanLostConnectionPolicy getLostConnectionPolicy() {
		return lostConnectionPolicy;
	}

	public void setLostConnectionPolicy(GearmanLostConnectionPolicy lostConnectionPolicy) {
		this.lostConnectionPolicy = lostConnectionPolicy;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
