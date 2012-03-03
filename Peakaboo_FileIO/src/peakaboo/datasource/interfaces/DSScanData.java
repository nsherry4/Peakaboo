package peakaboo.datasource.interfaces;


import java.util.List;

import scitypes.Spectrum;



public interface DSScanData
{

	/**
	 * Retrieves the values from the scan at the given index
	 * 
	 * @param index
	 *            the scan number to retrieve
	 */
	public Spectrum getScanAtIndex(int index) throws IndexOutOfBoundsException;

	
	
	/**
	 * Returns the number of scans in this data set, including any which have been marked as bad
	 */
	public int getScanCount();

	

	/**
	 * Returns the names of all scans, eg ["Scan 1", "Scan 2", ...]
	 */
	public List<String> getScanNames();


	/**
	 * Returns the maximum energy value for any channel for the scans in this set.
	 */
	public float getMaxEnergy();



	/**
	 * Returns a nice, human readable name for this data set. Depending 
	 * on the data stored in the file, it could be the name of the data
	 * file, the folder the set of files were found it, a name specified 
	 * within the file itself, etc...
	 */
	public String getDatasetName();
	

}
