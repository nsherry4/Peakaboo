package peakaboo.datatypes.eventful;


public interface IEventful
{

	public void addListener(PeakabooSimpleListener l);
	public void removeListener(PeakabooSimpleListener l);
	public void updateListeners();
	public void updateListeners(Object message);

	
}
