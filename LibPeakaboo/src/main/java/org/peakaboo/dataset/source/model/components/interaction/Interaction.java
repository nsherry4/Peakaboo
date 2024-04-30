package org.peakaboo.dataset.source.model.components.interaction;

public interface Interaction {

	/** 
	 * Notifies the observer of the number of scans in this data set
	 */
	void notifyScanCount(int count);
	
	/**
	 * Notify the observer that you have read an <i>additional</i> <tt>count</tt> scans
	 * @param count
	 */
	void notifyScanRead(int count);
	
	/**
	 * Allows the worker to check if the observer has requested this action be aborted
	 */
	boolean checkReadAborted();

}
