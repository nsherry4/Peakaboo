package peakaboo.fileio.xrf;


import java.util.List;

import scitypes.Spectrum;



public interface DataSource
{

	/**
	 * The types of files that Peakaboo is capable of reading
	 * 
	 * @author Nathaniel Sherry, 2009
	 */
	public enum FileType
	{
		CLSXML, ZIP, CDFML
	}
	
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
	 * Returns the number of scans expected from this data set
	 * @return the expected scan count
	 */
	public int getExpectedScanCount();
	
	/**
	 * Returns the names of all scans not marked as bad
	 * 
	 * @return a list of scan names for non-bad scans
	 */
	public List<String> getScanNames();


	/**
	 * Returns the maximum energy range of the scans in this set.
	 * 
	 * @return the maximum energy
	 */
	public float getMaxEnergy();


	/**
	 * Mark this scan as bad. This generally means that there is something wrong with this scan/file/record
	 * such that it is not intended to be a part of this data set
	 * 
	 * @param index
	 *            the index of the scan to mark as bad
	 */
	public void markScanAsBad(int index);


	/**
	 * Returns a nice, human readable name for this data set. Eg. the name of the file, the folder the files
	 * were found it, etc...
	 * 
	 * @return the data set name
	 */
	public String getDatasetName();


	public int estimateDataSourceSize();
	

}
