package org.peakaboo.datasource.model.components.interaction;

public interface Interaction {

	void notifyScanCount(int count);
	void notifyScanRead(int count);
	boolean checkReadAborted();

}
