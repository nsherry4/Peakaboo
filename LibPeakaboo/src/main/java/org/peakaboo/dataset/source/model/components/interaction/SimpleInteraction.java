package org.peakaboo.dataset.source.model.components.interaction;

public class SimpleInteraction implements Interaction {

	public int scanCount, scansRead;
	public boolean aborted;
	
	@Override
	public void notifyScanCount(int count) {
		this.scanCount = count;
	}

	@Override
	public void notifyScanRead(int count) {
		this.scansRead += count;
		
	}

	@Override
	public boolean checkReadAborted() {
		return aborted;
	}

}
