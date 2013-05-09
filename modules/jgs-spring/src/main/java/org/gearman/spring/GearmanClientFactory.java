package org.gearman.spring;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanLostConnectionPolicy;
import org.gearman.GearmanServer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * A factory bean that creates {@link GearmanClient} objects.
 * @author isaiah
 */
public class GearmanClientFactory implements FactoryBean, InitializingBean {
	
	private Gearman gearman;
	private GearmanServer[] servers;
	private String clientId;
	private GearmanLostConnectionPolicy lostConnectionPolicy;
	
	private GearmanClient client;

	/**
	 * Called by the spring framework to poll the gearman client. This factory bean is a
	 * singleton and will always return the same instance. 
	 */
	@Override
	public GearmanClient getObject() {
		return client;
	}
	
	/**
	 * Called by the spring framework after the properties have been set. This method
	 * initializes the gearman client instance.
	 */
	@Override
	public void afterPropertiesSet() {
		GearmanClient client = gearman.createGearmanClient();
		
		if(servers!=null) for(GearmanServer server : servers)
			client.addServer(server);
		
		if(clientId!=null)
			client.setClientID(clientId);
		if(lostConnectionPolicy!=null)
			client.setLostConnectionPolicy(lostConnectionPolicy);
		
		this.client = client;
	}
	
	@Override
	public Class<GearmanClient> getObjectType() {
		return GearmanClient.class;
	}

	/**
	 * Called by the spring framework to determine of this is a singleton. This
	 * is a singleton and will return <code>true</code>
	 * @return
	 * 		<code>true</code>
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Polls the {@link Gearman} property
	 * @return
	 * 		the {@link Gearman} property
	 */
	public Gearman getGearman() {
		return gearman;
	}
	
	/**
	 * Sets the {@link Gearman} property.
	 * @param gearman
	 * 		the {@link Gearman} property
	 */
	@Required
	public void setGearman(Gearman gearman) {
		this.gearman = gearman;
	}


	@Required
	public void setServers(GearmanServer[] servers) {
		this.servers = servers;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setLostConnectionPolicy(GearmanLostConnectionPolicy lostConnectionPolicy) {
		this.lostConnectionPolicy = lostConnectionPolicy;
	}
}
