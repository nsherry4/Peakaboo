package peakaboo.datasource.components.scandata;


import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;



public interface ScanData
{

	/**
	 * Retrieves the values from the scan at the given index
	 * 
	 * @param index
	 *            the scan number to retrieve
	 */
	ReadOnlySpectrum get(int index) throws IndexOutOfBoundsException;

	
	
	/**
	 * Returns the number of scans in this data set.
	 */
	int scanCount();

	

	/**
	 * Returns the names of all scans, eg ["Scan 1", "Scan 2", ...]
	 */
	String scanName(int index);


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
