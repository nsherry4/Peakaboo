package peakaboo.fileio;


import java.util.List;
import scitypes.Spectrum;



public interface DSScanData
{

	/**
	 * Retrieves the values from the scan at the given index
	 * 
	 * @param index
	 *            the scan number to retrieve
	 * @return the values from the requested scan
	 */
	public Spectrum getScanAtIndex(int index);

	
	/**
	 * Returns the number of scans in this data set, including any which have been marked as bad
	 * 
	 * @return the total number of scans
	 */
	public int getScanCount();

	

	/**
	 * Returns the names of all scans, eg ["Scan 1", "Scan 2", ...]
	 * 
	 * @return a list of scan names for non-bad scans
	 */
	public List<String> getScanNames();


	/**
	 * Returns the maximum energy value for any channel for the scans in this set.
	 * 
	 * @return the maximum energy
	 */
	public float getMaxEnergy();



	/**
	 * Returns a nice, human readable name for this data set. Eg. the 
	 * name of the file, the folder the files were found it, etc...
	 * 
	 * @return the data set name
	 */
	public String getDatasetName();
	

}
