package peakaboo.dataset.provider;



import java.util.List;

import fava.datatypes.Bounds;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.dataset.provider.implementations.EmptyDataSetProvider;
import peakaboo.dataset.provider.implementations.LocalDataSetProvider;
import peakaboo.datatypes.peaktable.PeakTable;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.fileio.xrf.DataSource;
import peakaboo.fileio.xrf.DataSourceDimensions;
import peakaboo.fileio.xrf.DataSourceExtendedInformation;
import peakaboo.filters.FilterSet;
import peakaboo.mapping.results.MapResultSet;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;



/**
 * Abstract class defining the methods needed for loading and working with a set of XRF scans, and for producing maps
 * with them. Subclasses can implement different ways of delivering the data -- {@link LocalDataSetProvider} provides an
 * implementation using the local file system as a source for the data. <br>
 * <br>
 * Another implementation could use a network data source. Some sets of XRF data can be rather large, so this has been
 * designed so that only one scan, or a set of maps needs requested from the DataSetContainer at a time -- any work
 * requiring access to more than one scan is done either in the DataSetContainer (or somewhere else linked with a
 * specific implementation).
 * 
 * @author Nathaniel Sherry, 2009
 */

public abstract class DataSetProvider
{

	protected Spectrum		dsc_average;
	protected Spectrum		dsc_maximum;

	protected float			dsc_maxEnergy;
	protected int			dsc_scanSize;

	protected String		dataSourcePath;

	protected PeakTable		peakTable;

	protected boolean		hasDimensions;

	protected boolean		hasExtendedInformation;


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
	
	public abstract int firstNonNullScanIndex(int start);
	
	public abstract int firstNonNullScanIndex();
	
	public static int firstNonNullScanIndex(DataSource ds, int start)
	{
		for (int i = start; i < ds.getScanCount(); i++)
		{
			if (ds.getScanAtIndex(i) != null)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	
	
	public abstract int lastNonNullScanIndex(int upto);
	
	public abstract int lastNonNullScanIndex();
	
	public static int lastNonNullScanIndex(DataSource ds, int upto)
	{
		upto = Math.min(upto, ds.getScanCount()-1);
		
		for (int i = upto; i >= 0; i--)
		{
			if (ds.getScanAtIndex(i) != null)
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
	public abstract int scanCount();
	
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
	public float energyPerChannel()
	{
		return dsc_maxEnergy / scanSize();
	}


	/**
	 * Returns the size of a single scan
	 * 
	 * @return size of a scan
	 */
	public abstract int scanSize();
	


	/**
	 * Sets the maximum energy value according to the data in the dataset
	 * 
	 * @param maxEnergy
	 */
	protected void setMaxEnergy(float maxEnergy)
	{
		this.dsc_maxEnergy = maxEnergy;
	}


	/**
	 * Gets the string representation of the data source. Could be a file path, a network address, or anything else
	 * implementation specific
	 * 
	 * @return data source string
	 */
	public String getDataSourcePath()
	{
		return dataSourcePath;
	}


	/**
	 * Does this implementation of the DataSetContainer actually contain data? {@link EmptyDataSetProvider} purposefully
	 * doesn't
	 * 
	 * @return true if this dataset has data, false otherwise
	 */
	public abstract boolean hasData();


	/**
	 * Does this implementation of the DataSetContainer contain dimensional information? This will depend on if the
	 * {@link DataSource} implements {@link DataSourceDimensions}
	 * 
	 * @return true if this dataset has dimensional information, false otherwise
	 */
	public abstract boolean hasDimensions();


	/**
	 * Does this implementation of the DataSetContainer contain extended information such as where the scan was taken?
	 * This will depend on if the {@link DataSource} implements {@link DataSourceExtendedInformation}
	 * 
	 * @return true if this dataset has extended information, false otherwise
	 */
	public abstract boolean hasExtendedInformation();


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
	public abstract TaskList<MapResultSet> calculateMap(final FilterSet filters, final FittingSet fittings);


	/**
	 * Gets the name of the data set
	 * 
	 * @return the name of the data set
	 */
	public abstract String getDatasetName();


	
	/**
	 * See {@link DataSourceDimensions}
	 */
	public abstract Coord<Bounds<Number>> getRealDimensions();

	/**
	 * See {@link DataSourceDimensions}
	 */
	public abstract SISize getRealDimensionsUnits();

	/**
	 * See {@link DataSourceDimensions}
	 */
	public abstract Coord<Integer> getDataDimensions();

	/**
	 * See {@link DataSourceDimensions}
	 */
	public abstract List<Coord<Number>> getCoordinateList();


	// SS Namespace
	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getCreationTime();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getCreator();


	// SSModel Namespace
	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getProjectName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getSessionName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getFacilityName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getLaboratoryName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getExperimentName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getInstrumentName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getTechniqueName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getSampleName();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getScanName();


	// Scan Namespace
	
	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getStartTime();

	/**
	 * See {@link DataSourceExtendedInformation}
	 */
	public abstract String getEndTime();

		
	public abstract void discard();
	
}
