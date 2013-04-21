package org.gearman.spring;

import java.util.List;

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
	private List<GearmanServer> gearmanServers;
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
		
		if(gearmanServers!=null) for(GearmanServer server : gearmanServers)
			client.addServer(server);
		
		if(clientId!=null)
			client.setClientID(clientId);
		if(lostConnectionPolicy!=null)
			client.setLostConnectionPolicy(lostConnectionPolicy);
		
		this.client = client;
	}
	
	/**
	 * Returns <code>GearmanClient.class</code>
	 */
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

	/**
	 * 
	 * @return
	 */
	public List<GearmanServer> getGearmanServers() {
		return gearmanServers;
	}

	public void setGearmanServers(List<GearmanServer> gearmanServers) {
		this.gearmanServers = gearmanServers;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public GearmanLostConnectionPolicy getLostConnectionPolicy() {
		return lostConnectionPolicy;
	}

	public void setLostConnectionPolicy(
			GearmanLostConnectionPolicy lostConnectionPolicy) {
		this.lostConnectionPolicy = lostConnectionPolicy;
	}
}
