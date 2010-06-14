package peakaboo.dataset;



import java.util.List;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.SISize;
import peakaboo.datatypes.ranges.ROI;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.filters.FilterSet;
import peakaboo.mapping.MapResultSet;



/**
 * This class is used to run Peakaboo against a network backend so that large data sets don't need to be shipped to the
 * client, and so that processing is not limited by the client's hardware (eg netbooks)
 * 
 * @author Omid Mola, Nathaniel Sherry 2010
 */

public class NetworkDataSetProvider extends DataSetProvider
{

	@Override
	public ScanContainer averagePlot()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanContainer averagePlot(List<Integer> excludedIndices)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TaskList<MapResultSet> calculateMap(FilterSet filters, FittingSet fittings)
	{
		// TODO Auto-generated method stub
		return null;
	}


	/*@Override
	public List<Double> calculateSumInRegion(ROI region)
	{
		// TODO Auto-generated method stub
		return null;
	}*/


	@Override
	public List<Coord<Number>> getCoordinateList()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getCreationTime()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getCreator()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Coord<Integer> getDataDimensions()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getDatasetName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getEndTime()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getExperimentName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getFacilityName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getInstrumentName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getLaboratoryName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getProjectName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Coord<Range<Number>> getRealDimensions()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SISize getRealDimensionsUnits()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getSampleName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScanContainer getScan(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getScanName(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getScanName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getSessionName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getStartTime()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getTechniqueName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean hasData()
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean hasDimensions()
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean hasExtendedInformation()
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void invalidateFilteredData()
	{
		// TODO Auto-generated method stub

	}


	@Override
	public float maximumIntensity()
	{
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public ScanContainer maximumPlot()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int scanCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void discard()
	{
		// TODO Auto-generated method stub
		//close any network connections, delete any cached data, etc
		
	}


	@Override
	public int scanSize()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
