package peakaboo.dataset;

import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
import scitypes.Spectrum;


/**
 * Given a DataSource, a DataSet provides the average and max spectra,
 * along with a few other values. This is intended to layer 
 * features on top of a DataSource in order to keep the DataSource 
 * implementation simple. It is not intended to store user 
 * settings/overrides.
 * 
 * @author Nathaniel Sherry, 2009-2017
 */
public interface DataSet {

	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @return average scan
	 */
	Spectrum averagePlot();

	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @param excludedIndices
	 *            is a list of indices to exclude from the average
	 * @return average scan
	 */
	Spectrum averagePlot(List<Integer> excludedIndices);

	/**
	 * Produces a single scan/list containing the most intense values for each channel
	 * 
	 * @return the top signal-per-channel scan
	 */
	Spectrum maximumPlot();

	


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
	 * Calculates the maximum intensity across all scans
	 * 
	 * @return the maximum intensity
	 */
	float maximumIntensity();


	/**
	 * Gets the energy per channel value according to the data in the dataset
	 * 
	 * @return the energy per channel
	 */
	float energyPerChannel();

	/**
	 * Returns the size of a single scan
	 * 
	 * @return size of a scan
	 */
	int channelsPerScan();

	/**
	 * Gets the string representation of the data source. Could be a file path, a network address, or anything else
	 * implementation specific
	 * 
	 * @return data source string
	 */
	String getDataSourcePath();

	/**
	 * Does this implementation of the DataSetContainer actually contain data? {@link EmptyDataSet} purposefully
	 * doesn't
	 * 
	 * @return true if this dataset has data, false otherwise
	 */
	//Note: This is different than a hasScanData method for a DataSource, since a DataSet will 
	//always have ScanData, even if it's a DummyScanData object.
	boolean hasData();




	/**
	 * This appears to be a workaround to a garbage collection issue where <i>something</i> is 
	 * maintaining a reference to the DataSet. By discarding the underlying DataSource, we can
	 * allow memory to be freed even with the reference. This should really be tested to see if
	 * it is still an issue. 
	 */
	//TODO: Test this to see if the memory issue still exists
	void discard();
	
	Metadata getMetadata();
	boolean hasMetadata();
	
	//boolean hasScanData();
	ScanData getScanData();

	DataSource getDataSource();

	boolean hasPhysicalSize();
	PhysicalSize getPhysicalSize();
	DataSize getDataSize();



}