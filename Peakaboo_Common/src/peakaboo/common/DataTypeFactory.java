package peakaboo.common;


import java.util.List;

import fava.functionable.FList;

import scitypes.Spectrum;


/**
 * 
 *  Single point in programme where data types can be adjusted.
 *
 * @author Nathaniel Sherry, 2009
 */

public class DataTypeFactory {



    
    /**
     * Creates a new list of type T and initialises it to a certain size with null values
	 * @param <T> type of data this list should hold
     * @param size the initial size of the list.
     * @return a new list of size 'size' filled with null values
     */
    public static <T> FList<T> listInit(int size){
    	FList<T> list = new FList<T>(size);
    	for (int i = 0; i < size; i++){ list.add(null); }
    	return list;
    }
    
    /**
     * Creates a new list of type T and initialises it with a single value of that type
	 * @param <T> type of data this list should hold
     * @param element the element to add to the list.
     * @return a new list of size 'size' filled with null values
     */
    public static <T> FList<T> listInit(T element){
    	FList<T> list = new FList<T>();
    	list.add(element);
    	return list;
    }
    
    /**
     * Creates a list of {@link Spectrum}s
     * @return a list which can contain Spectrums
     */
    public static <T> FList<List<T>> dataset(){
    	return new FList<List<T>>();
    }
    
    /**
     * Creates a list of {@link Spectrum}s
     * @return a list which can contain Spectrums, initialized with nulls
     */
    public static <T> FList<List<T>> datasetInit(int size){
    	FList<List<T>> dataset = dataset();
    	for (int i = 0; i < size; i++){
    		dataset.add(null);
    	}
    	return dataset;
    }
    
    /**
     * Creates a list of {@link Spectrum}s
     * @return a new list of {@link Spectrum}s
     */
    public static FList<Spectrum> spectrumSet()
    {
    	return new FList<Spectrum>();
    }
    
    /**
     * Creates a list of {@link Spectrum}s, initialzed to null values
     * @param size the initial size of the list
     * @return a new null-initialized list of {@link Spectrum}s
     */
    public static FList<Spectrum> spectrumSetInit(int size)
    {
    	FList<Spectrum> dataset = new FList<Spectrum>();
    	for (int i = 0; i < size; i++){
    		dataset.add(null);
    	}
    	return dataset;
    }
    



}