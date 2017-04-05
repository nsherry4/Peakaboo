package peakaboo.dataset;



import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
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

public abstract class AbstractDataSet implements DataSet
{



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


	
	
}
