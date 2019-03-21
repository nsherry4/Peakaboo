package eventful;



public interface IEventfulEnum<T extends Enum<T>>
{

	void addListener(EventfulEnumListener<T> l);


	void removeListener(final EventfulEnumListener<T> l);


	void removeAllListeners();


	void updateListeners(final T message);

}