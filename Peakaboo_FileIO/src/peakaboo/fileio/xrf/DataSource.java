package peakaboo.fileio.xrf;


import java.util.List;


public interface DataSource
{

	/**
	 * Retrieves the values from the scan at the given index
	 * 
	 * @param index
	 *            the scan number to retrieve
	 * @return the values from the requested scan
	 */
	public List<Double> getScanAtIndex(int index);


	/**
	 * Returns the number of scans in this data set, including any which have been marked as bad
	 * 
	 * @return the total number of scans
	 */
	public int getScanCount();


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
	public double getMaxEnergy();


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




}
