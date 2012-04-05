package org.gearman;

/**
 * An asynchronous callback class used to receive incoming job data from the job server.
 * @author isaiah
 *
 * @param <A> attachment data type
 */
public interface GearmanJobEventCallback<A> {
	
	/**
	 * Called when an event is received from the job server.<br>
	 * <br>
	 * Note that if an event is received while this method is executing, the next method invocation will
	 * not occur until this method returns.
	 * @param attachment
	 * 		An attachment defined by the user to identify the job
	 * @param type
	 * 		The job event type
	 * @param data
	 * 		The job data associated with the event. This value may be <code>null</code>, depending of the event type
	 */
	public void onGearmanJobEvent(A attachment, GearmanJobEventType type, byte[] data);
}
