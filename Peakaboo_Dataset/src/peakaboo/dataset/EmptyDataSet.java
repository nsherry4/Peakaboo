package peakaboo.dataset;

import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.DataSourceMetadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;


public class EmptyDataSet extends AbstractDataSet
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
	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
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
	public void discard()
	{
		
	}

	@Override
	public int channelsPerScan()
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

	@Override
	public String getDataSourcePath()
	{
		return "";
	}

	@Override
	public float energyPerChannel()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataSourceMetadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMetadata() {
		// TODO Auto-generated method stub
		return false;
	}



}
