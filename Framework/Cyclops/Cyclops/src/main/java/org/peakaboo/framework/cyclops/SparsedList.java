package org.peakaboo.framework.cyclops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Allows a non-sparse list to act as a sparse list by allowing add/set methods
 * to operate beyond the current length of the list. Intermediate entries will
 * be populated with nulls. This implementation uses a given list as a backer,
 * and will modify it in it's operation. Note that this is not a "native" sparse
 * list implementation, and as such, performance will be much more like a
 * non-sparse list.
 * 
 * @author NAS 2017
 *
 * @param <T>
 */
public class SparsedList<T> implements List<T> {

	private List<T> backing;

	public SparsedList(List<T> backing) {
		this.backing = backing;
	}

	public List<T> getBackingList() {
		return backing;
	}

	public void forEach(Consumer<? super T> action) {
		backing.forEach(action);
	}

	public int size() {
		return backing.size();
	}

	public boolean isEmpty() {
		return backing.isEmpty();
	}

	public boolean contains(Object o) {
		return backing.contains(o);
	}

	public Iterator<T> iterator() {
		return backing.iterator();
	}

	public Object[] toArray() {
		return backing.toArray();
	}

	public <S> S[] toArray(S[] a) {
		return backing.toArray(a);
	}

	public boolean add(T e) {
		return backing.add(e);
	}

	public boolean remove(Object o) {
		return backing.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return backing.containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c) {
		return backing.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return backing.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return backing.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return backing.retainAll(c);
	}

	public void replaceAll(UnaryOperator<T> operator) {
		backing.replaceAll(operator);
	}

	public boolean removeIf(Predicate<? super T> filter) {
		return backing.removeIf(filter);
	}

	public void sort(Comparator<? super T> c) {
		backing.sort(c);
	}

	public void clear() {
		backing.clear();
	}

	public boolean equals(Object o) {
		return backing.equals(o);
	}

	public int hashCode() {
		return backing.hashCode();
	}

	public T get(int index) {
		return backing.get(index);
	}

	public T set(int index, T element) {
		// populate the list up to and including the index we will set to be `element`
		while (index > this.size() - 1) {
			this.add(null);
		}
		return backing.set(index, element);
	}

	public void add(int index, T element) {
		// populate the list up to but not including the index we place `element` in
		while (index > this.size() - 2) {
			this.add(null);
		}
		backing.add(index, element);
	}

	public Stream<T> stream() {
		return backing.stream();
	}

	public T remove(int index) {
		return backing.remove(index);
	}

	public Stream<T> parallelStream() {
		return backing.parallelStream();
	}

	public int indexOf(Object o) {
		return backing.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return backing.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return backing.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return backing.listIterator(index);
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return new SparsedList<>(backing.subList(fromIndex, toIndex));
	}

	public Spliterator<T> spliterator() {
		return backing.spliterator();
	}



}
