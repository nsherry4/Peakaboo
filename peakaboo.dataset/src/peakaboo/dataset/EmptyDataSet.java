package peakaboo.dataset;

import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
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
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMetadata() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPhysicalSize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PhysicalSize getPhysicalSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSize getDataSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanData getScanData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasScanData() {
		// TODO Auto-generated method stub
		return false;
	}




}
