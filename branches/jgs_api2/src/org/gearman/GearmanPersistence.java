package org.gearman;

import java.util.Collection;

/**
 * An application hook telling the framework how jobs will persist. Instances of
 * {@link GearmanPersistence}s will provide information needed to create jdbc connection and
 * provide the logic for reading and writing jobs to a persistent medium.
 * 
 * @author isaiah
 */
public interface GearmanPersistence {
	
	/**
	 * Write the {@link GearmanPersistable} item to a persistent medium.<br>
	 * <br>
	 * When this method returns, it is guaranteed the write operation has completed.
	 * @param item
	 * 		The item to write to a persistent medium
	 * @throws Exception
	 * 		If an exception occurs while writing the item
	 */
	public void write(GearmanPersistable item) throws Exception;
	
	/**
	 * Removes the {@link GearmanPersistable} item from the persistent medium.<br>
	 * <br>
	 * When this method returns, it is guaranteed the remove operation has completed.
	 * @param item
	 * 		The item to remove
	 * @throws Exception
	 * 		If an exception occurs while removing the item
	 */
	public void remove(GearmanPersistable item) throws Exception;
	
	/**
	 * Removes all items from the persistent medium
	 * @throws Exception
	 * 		If an exception occurs while removing the items
	 */
	public void removeAll() throws Exception;
	
	/**
	 * Reads all items from the 
	 * @return
	 * @throws Exception
	 */
	public Collection<GearmanPersistable> readAll() throws Exception;
}