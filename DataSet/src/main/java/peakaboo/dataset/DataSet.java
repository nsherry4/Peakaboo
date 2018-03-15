package peakaboo.dataset;

import java.io.File;
import java.util.List;

import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;
import scitypes.ReadOnlySpectrum;


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


	/**
	 * Returns the size of a single scan
	 * 
	 * @return size of a scan
	 */
	int channelsPerScan();

	/**
	 * Gets the {@link File} representation of the data source. Could be a file path, a network address, or anything else
	 * implementation specific
	 * 
	 * @return data source string
	 */
	File getDataSourcePath();

	/**
	 * Does this implementation of the DataSetContainer actually contain data? {@link EmptyDataSet} purposefully
	 * doesn't. This is different than the hasScanData method for a DataSource, since hasData() will return false 
	 * if a ScanData object exists, but has 0 scans.
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
	
	/**
	 * Return a data size. If the underlying {@link DataSource} has a data size, return 
	 * that, otherwise, create a dummy one.
	 */
	DataSize getDataSize();
	boolean hasGenuineDataSize();
	
	

	/**
	 * Given a {@link DataSource} finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @param ds the {@link DataSource} to check
	 * @param start the index from which to start searching
	 * @return the index of the first non-null scan, or -1 if no such scans exist
	 */
	//This method is here, rather than in DataSet because DataSet uses these methods while initializing itself from a DataSource, therefore the DataSource must be passed explicitly
	public static int firstNonNullScanIndex(DataSource ds, int start)
	{
		for (int i = start; i < ds.getScanData().scanCount(); i++)
		{
			if (ds.getScanData().get(i) != null)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	


	
	/**
	 * Given a {@link DataSource} finds the last non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 1-45 where 50 scans are expected
	 * @param ds the {@link DataSource} to check
	 * @param upto the maximum index to consider
	 * @return the index of the last non-null scan, or -1 if no such scans exist
	 */
	public static int lastNonNullScanIndex(DataSource ds, int upto)
	{
		upto = Math.min(upto, ds.getScanData().scanCount()-1);
		
		for (int i = upto; i >= 0; i--)
		{
			if (ds.getScanData().get(i) != null)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	
	




}