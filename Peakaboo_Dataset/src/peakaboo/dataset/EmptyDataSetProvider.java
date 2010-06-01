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


public class EmptyDataSetProvider extends DataSetProvider
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
	public List<Double> calculateSumInRegion(ROI region)
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
	public void invalidateFilteredData()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public double maximumIntensity()
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
	public TaskList<MapResultSet> calculateMap(FilterSet filters, FittingSet fittings)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasData(){
		return false;
	}

	@Override
	public String getDatasetName()
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getScanName(int index)
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public Coord<Integer> getDataDimensions()
	{
		// TODO Auto-generated method stub
		return null;
	}


	/*@Override
	public Corners getRealDimensions()
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

	public String getCreationTime()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getEndTime()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getExperimentName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getFacilityName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getInstrumentName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLaboratoryName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getProjectName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getSampleName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getScanName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getSessionName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getStartTime()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTechniqueName()
	{
		// TODO Auto-generated method stub
		return null;
	}



}
