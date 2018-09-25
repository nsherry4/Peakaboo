package cyclops.util;

public class Mutable<T> {

	private T value;

	public Mutable(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
	
	public synchronized T testAndSet(T value) {
		T old = get();
		set(value);
		return old;
	}
	
	
	
}
