package peakaboo.dataset.provider.implementations;

import java.util.List;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.fileio.DataSource;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.workers.PluralSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;


public class EmptyDataSetProvider extends DataSetProvider
{

	@Override
	public Spectrum averagePlot()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Spectrum averagePlot(List<Integer> excludedIndices)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Spectrum getScan(int index)
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
	public float maximumIntensity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Spectrum maximumPlot()
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
	public PluralSet<MapResultSet> calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type)
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
	public Coord<Bounds<Number>> getRealDimensions()
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
	public String getSampleName()
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
	public void discard()
	{
		
	}

	@Override
	public int scanSize()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int expectedScanCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int firstNonNullScanIndex()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int firstNonNullScanIndex(int start)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastNonNullScanIndex(int upto)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastNonNullScanIndex()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataSource getDataSource()
	{
		// TODO Auto-generated method stub
		return null;
	}



}
