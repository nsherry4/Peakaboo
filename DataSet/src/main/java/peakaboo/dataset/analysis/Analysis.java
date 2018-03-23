package peakaboo.dataset.analysis;

import java.util.List;
import java.util.function.Consumer;

import scitypes.ReadOnlySpectrum;

public interface Analysis {

	
	
	void process(int index, ReadOnlySpectrum spectrum);
	

	
	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @return average scan
	 */
	ReadOnlySpectrum averagePlot();

	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @param excludedIndices
	 *            is a list of indices to exclude from the average
	 * @return average scan
	 */
	ReadOnlySpectrum averagePlot(List<Integer> excludedIndices);

	/**
	 * Produces a single scan/list containing the most intense values for each channel
	 * 
	 * @return the top signal-per-channel scan
	 */
	ReadOnlySpectrum maximumPlot();

	


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
