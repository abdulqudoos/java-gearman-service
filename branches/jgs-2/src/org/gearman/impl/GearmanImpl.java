package org.gearman.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanPersistence;
import org.gearman.GearmanServer;
import org.gearman.GearmanService;
import org.gearman.GearmanWorker;
import org.gearman.core.GearmanConnectionManager;
import org.gearman.impl.server.local.GearmanServerLocal;
import org.gearman.impl.server.remote.GearmanServerRemote;
import org.gearman.impl.util.Scheduler;

/**
 * 
 * @author isaiah
 *
 */
public final class GearmanImpl extends Gearman {
	
	private final GearmanConnectionManager connectionManager;
	private final Scheduler scheduler;
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Set<GearmanService> serviceSet = Collections.synchronizedSet(new HashSet<GearmanService>());
	
	public GearmanImpl() throws IOException {
		this(1);
	}
	
	public GearmanImpl(int coreThreads) throws IOException {
		if(coreThreads<=0)
			throw new IllegalArgumentException("GearmanImpl needs 1 or more threads");
		
		final ThreadPoolExecutor pool = new ThreadPoolExecutor(coreThreads, Integer.MAX_VALUE, GearmanConstants.THREAD_TIMEOUT, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
		pool.allowCoreThreadTimeOut(false);
		pool.prestartCoreThread();
		
		this.scheduler = new Scheduler(pool); 
		this.connectionManager = new GearmanConnectionManager();
	}

	@Override
	public void shutdown() {
		try {
			lock.writeLock().lock();
			
			for(GearmanService service : this.serviceSet) {
				service.shutdown();
			}
			this.connectionManager.shutdown();
			this.scheduler.shutdown();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean isShutdown() {
		return this.connectionManager.isShutdown();
	}

	@Override
	public Gearman getGearman() {
		return this;
	}

	@Override
	public String getVersion() {
		return GearmanConstants.VERSION;
	}

	@Override
	public GearmanServer createGearmanServer(InetSocketAddress address) {
		lock.readLock().lock();
		try {
			if(this.isShutdown()) {
				throw new IllegalStateException("Shutdown Service");
			}
			
			final GearmanServer server = new GearmanServerRemote(this, address);
			this.serviceSet.add(server);
			
			return server;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public GearmanWorker createGearmanWorker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GearmanClient createGearmanClient() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	public final GearmanConnectionManager getGearmanConnectionManager() {
		return this.connectionManager;
	}

	@Override
	public GearmanServer createGearmanServer(int... ports) throws IOException {
		return createGearmanServer((GearmanPersistence)null, ports);
	}

	@Override
	public GearmanServer createGearmanServer(GearmanPersistence persistence, int... ports) throws IOException {
		lock.readLock().lock();
		try {
			if(this.isShutdown()) {
				throw new IllegalStateException("Shutdown Service");
			}
			
			
			final GearmanServer server = new GearmanServerLocal(this, persistence, ports);
			this.serviceSet.add(server);
			
			return server;
		} finally {
			lock.readLock().unlock();
		}
	}
}
