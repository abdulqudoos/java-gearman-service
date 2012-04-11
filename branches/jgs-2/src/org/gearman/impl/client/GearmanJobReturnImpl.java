package org.gearman.impl.client;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobReturn;

public class GearmanJobReturnImpl implements GearmanJobReturn {

	private boolean isEOF = false;
	private final Deque<GearmanJobEvent> eventList = new LinkedList<>();
	
	@Override
	public synchronized GearmanJobEvent poll() throws InterruptedException {
		while(eventList.isEmpty() && !this.isEOF) {
			this.wait();
		}
		
		if(eventList.isEmpty())
			return GearmanJobEventImpl.GEARMAN_EOF;
		
		return eventList.pollFirst();
	}

	@Override
	public synchronized GearmanJobEvent poll(long timeout, TimeUnit unit) throws InterruptedException {
		if(timeout==0) return poll();
			
		timeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
		while(eventList.isEmpty() && !this.isEOF && timeout>0) {
			long startTime = System.currentTimeMillis();
			
			this.wait(timeout);
			timeout = timeout - (startTime - System.currentTimeMillis());
		}
		
		if(eventList.isEmpty() && this.isEOF)
			return GearmanJobEventImpl.GEARMAN_EOF;
		
		return eventList.pollFirst();
	}

	@Override
	public synchronized GearmanJobEvent pollNow() {
		if(this.isEOF && eventList.isEmpty())
			return GearmanJobEventImpl.GEARMAN_EOF;
		
		return this.eventList.pollFirst();
	}
	
	public synchronized void put(GearmanJobEvent event) {
		this.eventList.addLast(event);
		this.notifyAll();
	}
	
	public synchronized void eof() {
		this.isEOF = true;
		this.notifyAll();
	}

}
