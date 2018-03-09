package peakaboo.dataset;

import java.io.File;
import java.util.List;

import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.DummyScanData;
import peakaboo.datasource.model.components.scandata.ScanData;
import scitypes.Spectrum;


public class EmptyDataSet implements DataSet
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
	public boolean hasData(){
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
	public File getDataSourcePath()
	{
		return new File("");
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
		return new DummyScanData();
	}

	@Override
	public boolean hasGenuineDataSize() {
		// TODO Auto-generated method stub
		return false;
	}




}
