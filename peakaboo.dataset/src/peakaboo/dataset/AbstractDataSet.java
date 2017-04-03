package peakaboo.dataset;



import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.metadata.Metadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;



/**
 * Abstract class defining the methods needed for loading and working with a set of
 * XRF scans. This class, or its subclasses, wrap a DataSource object and provide a
 * somewhat richer interface to the data without requiring each DataSource 
 * implementation to handle this logic manually. 
 * 
 * @author Nathaniel Sherry, 2009-2012
 */

public abstract class AbstractDataSet
{


	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @return average scan
	 */
	public abstract Spectrum averagePlot();


	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @param excludedIndices
	 *            is a list of indices to exclude from the average
	 * @return average scan
	 */
	public abstract Spectrum averagePlot(List<Integer> excludedIndices);


	/**
	 * Produces a single scan/list containing the most intense values for each channel
	 * 
	 * @return the top signal-per-channel scan
	 */
	public abstract Spectrum maximumPlot();


	/**
	 * Retrieves a single scan from the data set
	 * 
	 * @param index
	 * @return a single scan
	 */
	public abstract Spectrum getScan(int index);
	
	/**
	 * Finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @param start the index from which to start searching
	 * @return the index of the first non-null scan
	 */
	public abstract int firstNonNullScanIndex(int start);
	
	/**
	 * Finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @return the index of the first non-null scan, or -1 if no such scans exist
	 */
	public abstract int firstNonNullScanIndex();
	
	
	/**
	 * Given a {@link DataSource} finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @param ds the {@link DataSource} to check
	 * @param start the index from which to start searching
	 * @return the index of the first non-null scan, or -1 if no such scans exist
	 */
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
	 * Finds the last non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 1-45 where 50 scans are expected
	 * @param upto the maximum index to consider
	 * @return the index of the last non-null scan
	 */
	public abstract int lastNonNullScanIndex(int upto);
	
	
	/**
	 * Finds the last non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 1-45 where 50 scans are expected
	 * @return the index of the last non-null scan, or -1 if no such scans exist
	 */
	public abstract int lastNonNullScanIndex();
	
	
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
	
	/**
	 * Gets the name of a single scan. This could be a file name, a scan number, or something else inplementation
	 * specific
	 * 
	 * @param index
	 * @return the name of a scan
	 */
	public abstract String getScanName(int index);


	/**
	 * Calculates the maximum intensity across all scans
	 * 
	 * @return the maximum intensity
	 */
	public abstract float maximumIntensity();


	/**
	 * Gets the number of scans in the dataset
	 * 
	 * @return the number of scans
	 */
	public abstract int size();
	
	/**
	 * Gets the expected number of scans in the dataset
	 * 
	 * @return the number of scans
	 */
	public abstract int expectedScanCount();

	/**
	 * Gets the energy per channel value according to the data in the dataset
	 * 
	 * @return the energy per channel
	 */
	public abstract float energyPerChannel();


	/**
	 * Returns the size of a single scan
	 * 
	 * @return size of a scan
	 */
	public abstract int channelsPerScan();
	


	/**
	 * Gets the string representation of the data source. Could be a file path, a network address, or anything else
	 * implementation specific
	 * 
	 * @return data source string
	 */
	public abstract String getDataSourcePath();


	/**
	 * Does this implementation of the DataSetContainer actually contain data? {@link EmptyDataSet} purposefully
	 * doesn't
	 * 
	 * @return true if this dataset has data, false otherwise
	 */
	public abstract boolean hasData();


	/**
	 * Does this implementation of the DataSetContainer contain dimensional information? This will depend on if the
	 * {@link DataSource} implements {@link DataSize}
	 * 
	 * @return true if this dataset has dimensional information, false otherwise
	 */
	public abstract boolean hasDimensions();

	public abstract DataSize getDimensions();
	

	/**
	 * Invalidates any filters which have been applied to the dataset
	 */
	public abstract void invalidateFilteredData();


	/**
	 * returns a map based on the sum over a given region for each scan in the dataset
	 * 
	 * @param region
	 * @return a map based on the given region of interest
	 */
	//public abstract List<Double> calculateSumInRegion(ROI region);

	/**
	 * Creates a map based on a given FilterSet, a given FittingSet, and the data in the dataset.
	 * 
	 * @param filters
	 *            filters to be used on this data
	 * @param fittings
	 *            fittings to be used on this data
	 * @return a {@link Task} which will calculate the map
	 */
	//public abstract ExecutorSet<MapResultSet> calculateMap(final FilterSet filters, final FittingSet fittings, FittingTransform type);


	/**
	 * Gets the name of the data set
	 * 
	 * @return the name of the data set
	 */
	public abstract String getDatasetName();


	
	public abstract Metadata getMetadata();
	public abstract boolean hasMetadata();
	
	
	public abstract void discard();
	
	
	public abstract DataSource getDataSource();
	
	
}
