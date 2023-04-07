package org.peakaboo.dataset.sink.model.components.interaction;

public class SimpleInteraction implements Interaction {

	private int scansWritten = 0;
	private boolean abortRequested = false;
	
	@Override
	public void notifyScanWritten(int count) {
		scansWritten += count;
	}

	@Override
	public boolean isAbortedRequested() {
		return abortRequested;
	}

	public int getScansWritten() {
		return scansWritten;
	}

	public void setAbortRequested(boolean abortRequested) {
		this.abortRequested = abortRequested;
	}

	
	
}
