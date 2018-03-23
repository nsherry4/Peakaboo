package peakaboo.dataset;

import java.io.File;
import java.util.List;
import java.util.Optional;

import peakaboo.dataset.analysis.Analysis;
import peakaboo.dataset.analysis.DummyAnalysis;
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
	public boolean hasGenuineData(){
		return false;
	}



	@Override
	public void discard()
	{
		
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
	public Optional<Metadata> getMetadata() {
		return Optional.empty();
	}


	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return Optional.empty();
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



	@Override
	public Analysis getAnalysis() {
		// TODO Auto-generated method stub
		return new DummyAnalysis();
	}




}
