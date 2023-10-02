package org.peakaboo.dataset;

import java.util.Optional;

import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;


/**
 * Given a DataSource, a DataSet  is intended to layer features on top of a 
 * DataSource in order to keep the DataSource implementation simple. It is 
 * also intended to present a more programmer-friendly interface to the rest 
 * of Peakaboo, whereas a DataSource is intended to be more user-friendly to 
 * the DataSource implementer. It is not intended to store user settings or 
 * overrides.
 * 
 * @author Nathaniel Sherry, 2009-2018
 */
public interface DataSet {


	Analysis getAnalysis();

	
	ScanData getScanData();
	
	/**
	 * Does this implementation of the DataSetContainer actually contain data? {@link EmptyDataSet} purposefully
	 * doesn't. This is different than getScanData() == null for a DataSource, since hasGenuineData() 
	 * will return false if a ScanData object exists, but has 0 scans.
	 * 
	 * @return true if this dataset has data, false otherwise
	 */
	//Note: This is different than a hasScanData method for a DataSource, since a DataSet will 
	//always have ScanData, even if it's a DummyScanData object.
	boolean hasGenuineScanData();


	Optional<Metadata> getMetadata();

	DataSource getDataSource();

	Optional<PhysicalSize> getPhysicalSize();
	
	/**
	 * Return a data size. If the underlying {@link DataSource} has a data size, return 
	 * that, otherwise, create a dummy one, since it's easier than having a bunch of 
	 * <code> if (hasDataSize) { ... } else { ... } </code> statements everywhere.
	 */
	DataSize getDataSize();
	boolean hasGenuineDataSize();
	
	
	/**
	 * This appears to be a workaround to a garbage collection issue where <i>something</i> is 
	 * maintaining a reference to the DataSet. By discarding the underlying DataSource, we can
	 * allow memory to be freed even with the reference. This should really be tested to see if
	 * it is still an issue. 
	 */
	//TODO: Test this to see if the memory issue still exists
	void discard();


}