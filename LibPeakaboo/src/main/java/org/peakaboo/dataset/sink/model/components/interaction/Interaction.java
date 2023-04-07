package org.peakaboo.dataset.sink.model.components.interaction;

public interface Interaction {

	void notifyScanWritten(int count);
	boolean isAbortedRequested();
	
}
