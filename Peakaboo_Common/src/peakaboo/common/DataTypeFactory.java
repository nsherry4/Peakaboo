package peakaboo.common;


import java.util.List;
import java.util.ArrayList;

import scitypes.Spectrum;


/**
 * 
 *  Single point in programme where data types can be adjusted.
 *
 * @author Nathaniel Sherry, 2009
 */

public class DataTypeFactory {


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
     * Creates a new list of type T and initialises it to a certain size with null values
	 * @param <T> type of data this list should hold
     * @param size the initial size of the list.
     * @return a new list of size 'size' filled with null values
     */
    public static <T> List<T> listInit(int size){
    	List<T> list = new ArrayList<T>(size);
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
    	List<T> list = new ArrayList<T>();
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
    	return new ArrayList<Spectrum>();
    }
    
    /**
     * Creates a list of {@link Spectrum}s, initialzed to null values
     * @param size the initial size of the list
     * @return a new null-initialized list of {@link Spectrum}s
     */
    public static List<Spectrum> spectrumSetInit(int size)
    {
    	List<Spectrum> dataset = new ArrayList<Spectrum>();
    	for (int i = 0; i < size; i++){
    		dataset.add(null);
    	}
    	return dataset;
    }
    



}