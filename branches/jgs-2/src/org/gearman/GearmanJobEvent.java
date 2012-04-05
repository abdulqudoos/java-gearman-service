package org.gearman;

/**
 * An object representing job update
 * @author isaiah
 */
public interface GearmanJobEvent {
	
	/**
	 * The job event type. This defines the type of job update received. 
	 * @return
	 * 		The job event type
	 * @see GearmanJobEventType
	 */
	public GearmanJobEventType getEventType();
	
	/**
	 * The result's data
	 * @return
	 * 		The data defined by the result type. If data is not returned with the given result type,
	 * 		<code>null</code> is return.
	 * @see #hasData() 
	 */
	public byte[] getData();
	
	/**
	 * Tests if this job result has data to poll
	 * @return
	 * 		<code>true</code> if the {@link GearmanJobEventType} attached to this job result
	 * 		has data 
	 */
	public boolean hasData();
}
