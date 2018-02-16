package scratch.list;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ScratchListAdapter<T> extends AbstractList<T>{

	private ScratchList<T> backing;
	
	
	public ScratchListAdapter(ScratchList<T> backing) {
		this.backing = backing;
	}
	
	///////////////////////////////////////////////////
	// Core AbstractList Overrides
	///////////////////////////////////////////////////
	
	@Override
	public T get(int index) {
		return backing.get(index);
	}
	
	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public T set(int index, T element) {
		T t = backing.get(index);
		backing.set(index, element);
		return t;
	}
	

	@Override
	public void add(int index, T element) {
		backing.add(index, element);
	}
	
	@Override
	public T remove(int index) {
		T t = backing.get(index);
		backing.remove(index);
		return t;
	}
	
	
	
	
	
	
	///////////////////////////////////////////////////
	// Performance-Related Overrides
	///////////////////////////////////////////////////

	@Override
	public boolean contains(Object o) {
		return backing.contains((T) o);
	}

	@Override
	public Iterator<T> iterator() {
		return backing.iterator();
	}

	@Override
	public boolean add(T e) {
		backing.add(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return backing.remove((T) o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backing.containsAll((Collection<? extends T>) c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		backing.addAll(c);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		backing.addAll(index, c);
		return true;
	}


	@Override
	public void clear() {
		backing.clear();
	}
	
}

