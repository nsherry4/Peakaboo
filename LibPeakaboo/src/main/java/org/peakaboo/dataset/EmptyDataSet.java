package org.peakaboo.dataset;

import java.util.Optional;

import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.DummyScanData;
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.components.scandata.analysis.DummyAnalysis;


public class EmptyDataSet implements DataSet {

	@Override
	public boolean hasGenuineScanData(){
		return false;
	}



	@Override
	public void discard() {
		//NOOP
	}



	@Override
	public DataSource getDataSource() {
		return null;
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
		return null;
	}

	@Override
	public ScanData getScanData() {
		return new DummyScanData();
	}

	@Override
	public boolean hasGenuineDataSize() {
		return false;
	}

	@Override
	public Analysis getAnalysis() {
		return new DummyAnalysis();
	}


}
