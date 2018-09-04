package peakaboo.dataset.analysis;

import scitypes.ReadOnlySpectrum;

/**
 * Peakaboo derives a number of measurements from a Data Set which are not
 * directly provided by the DataSource. This includes things like the average
 * scan. This is separated from the DataSet for composability and simplicity 
 * @author NAS
 *
 */
public interface Analysis {

	
	/**
	 * Accepts a new scan and it's index (position in the data). Calculates any 
	 * incremental metrics it may wish to do. 
	 * @param index
	 * @param spectrum
	 */
	void process(int index, ReadOnlySpectrum spectrum);
	
	
	

	
	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @return average scan
	 */
	ReadOnlySpectrum averagePlot();


	/**
	 * Produces a single scan/list containing the most intense values for each channel
	 * 
	 * @return the top signal-per-channel scan
	 */
	ReadOnlySpectrum maximumPlot();


	/**
	 * Returns the size of a single scan
	 * 
	 * @return size of a scan
	 */
	int channelsPerScan();
	


	/**
	 * Finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @param start the index from which to start searching
	 * @return the index of the first non-null scan
	 */
	int firstNonNullScanIndex(int start);

	/**
	 * Finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @return the index of the first non-null scan, or -1 if no such scans exist
	 */
	int firstNonNullScanIndex();

	/**
	 * Finds the last non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 1-45 where 50 scans are expected
	 * @param upto the maximum index to consider
	 * @return the index of the last non-null scan
	 */
	int lastNonNullScanIndex(int upto);

	/**
	 * Finds the last non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 1-45 where 50 scans are expected
	 * @return the index of the last non-null scan, or -1 if no such scans exist
	 */
	int lastNonNullScanIndex();


	/**
	 * Calculates the maximum single-channel intensity across all scans
	 * 
	 * @return the maximum intensity
	 */
	float maximumIntensity();
	
}
