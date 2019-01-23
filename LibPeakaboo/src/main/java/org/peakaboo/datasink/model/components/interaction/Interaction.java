package org.peakaboo.datasink.model.components.interaction;

public interface Interaction {

	void notifyScanWritten(int count);
	boolean isAbortedRequested();
	
}
