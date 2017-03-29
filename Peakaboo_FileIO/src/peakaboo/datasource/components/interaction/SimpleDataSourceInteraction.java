package peakaboo.datasource.components.interaction;

public class SimpleDataSourceInteraction implements DataSourceInteraction {

	public int scanCount, scansRead;
	public boolean aborted;
	
	@Override
	public void notifyScanCount(int count) {
		this.scanCount = count;
	}

	@Override
	public void notifyScanRead(int count) {
		this.scansRead = count;
		
	}

	@Override
	public boolean checkReadAborted() {
		return aborted;
	}

}
