package eventful;



public interface IEventful
{

	void addListener(EventfulListener l);


	void removeListener(final EventfulListener l);


	void removeAllListeners();


	void updateListeners();

}