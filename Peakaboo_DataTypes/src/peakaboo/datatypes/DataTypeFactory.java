package peakaboo.datatypes;

import java.util.HashMap;
import java.util.List;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import scitypes.Spectrum;


/**
 * 
 *  Single point in programme where data types can be adjusted.
 *
 * @author Nathaniel Sherry, 2009
 */

public class DataTypeFactory {

	/**
	 * Creates a new list of type T
	 * @param <T> type of data this list should hold
	 * @return a new list of type T
	 */
    public static <T> List<T> list() {
    	List<T> list = new ArrayList<T>();
    	return list;
    }

	/**
	 * Creates a new set of type T
	 * @param <T> type of data this set should hold
	 * @return a new set of type T
	 */
    public static <T> AbstractSet<T> set() {
    	AbstractSet<T> set = new HashSet<T>();
    	return set;
    }
    
    /**
     * Creates a new list of type T and initialises it with data from the given list
	 * @param <T> type of data this list should hold
     * @param copyFrom the list of data to initialise this list with.
     * @return a new list of type T containing the same values as copyFrom
     */
    public static <T> List<T> listInit(List<T> copyFrom) {
    	List<T> copy = new ArrayList<T>();
    	if (copyFrom == null) return copy;
    	for (int i = 0; i < copyFrom.size(); i++){
    		copy.add(i, copyFrom.get(i));
    	}
    	return copy;
    }
    
    /**
     * Creates a new list of type T and sets its initial capacity to a given size
	 * @param <T> type of data this list should hold
     * @param size the initial capacity this list should have.
     * @return a new list of type T, with a default capacity of size 
     */
    public static <T> List<T> list(int size) {
    	return new ArrayList<T>(size);
    }
    
    /**
     * Creates a new list of type T and initialises it to a certain size with null values
	 * @param <T> type of data this list should hold
     * @param size the initial size of the list.
     * @return a new list of size 'size' filled with null values
     */
    public static <T> List<T> listInit(int size){
    	List<T> list = list(size);
    	for (int i = 0; i < size; i++){ list.add(null); }
    	return list;
    }
    
    /**
     * Creates a new list of type T and initialises it with a single value of that type
	 * @param <T> type of data this list should hold
     * @param element the element to add to the list.
     * @return a new list of size 'size' filled with null values
     */
    public static <T> List<T> listInit(T element){
    	List<T> list = list();
    	list.add(element);
    	return list;
    }
    
    /**
     * Creates a list of {@link Spectrum}s
     * @return a list which can contain Spectrums
     */
    public static <T> List<List<T>> dataset(){
    	return new ArrayList<List<T>>();
    }
    
    /**
     * Creates a list of {@link Spectrum}s
     * @return a list which can contain Spectrums, initialized with nulls
     */
    public static <T> List<List<T>> datasetInit(int size){
    	List<List<T>> dataset = dataset();
    	for (int i = 0; i < size; i++){
    		dataset.add(null);
    	}
    	return dataset;
    }
    
    /**
     * Creates a list of {@link Spectrum}s
     * @return a new list of {@link Spectrum}s
     */
    public static List<Spectrum> spectrumSet()
    {
    	return DataTypeFactory.<Spectrum>list();
    }
    
    /**
     * Creates a list of {@link Spectrum}s, initialzed to null values
     * @param size the initial size of the list
     * @return a new null-initialized list of {@link Spectrum}s
     */
    public static List<Spectrum> spectrumSetInit(int size)
    {
    	List<Spectrum> dataset = DataTypeFactory.<Spectrum>list();
    	for (int i = 0; i < size; i++){
    		dataset.add(null);
    	}
    	return dataset;
    }
    
    /**
     * Creates a map from values of type T to values of type S
     * @param <T> type of keys
     * @param <S> type of values
     * @return a new map; T => S
     */
    public static <T, S> Map<T, S> map(){
    	return new HashMap<T, S>();
    }


}