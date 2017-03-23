package peakaboo.datasource.components;


import java.util.List;

import scitypes.Spectrum;



public interface DataSourceScanData
{

	/**
	 * Retrieves the values from the scan at the given index
	 * 
	 * @param index
	 *            the scan number to retrieve
	 */
	Spectrum get(int index) throws IndexOutOfBoundsException;

	
	
	/**
	 * Returns the number of scans in this data set.
	 */
	int scanCount();

	

	/**
	 * Returns the names of all scans, eg ["Scan 1", "Scan 2", ...]
	 */
	List<String> scanNames();


	/**
	 * Returns the maximum energy value for any channel for the scans in this set.
	 */
	float maxEnergy();



	/**
	 * Returns a nice, human readable name for this data set. Depending 
	 * on the data stored in the file, it could be the name of the data
	 * file, the folder the set of files were found it, a name specified 
	 * within the file itself, etc...
	 */
	String datasetName();
	

}
