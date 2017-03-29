package peakaboo.datasource.components.interaction;

public interface DataSourceInteraction {

	void notifyScanCount(int count);
	void notifyScanRead(int count);
	boolean checkReadAborted();

}
