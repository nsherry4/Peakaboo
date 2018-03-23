package peakaboo.dataset;

import java.io.File;
import java.util.Optional;

import peakaboo.dataset.analysis.Analysis;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;


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


	/**
	 * Gets the {@link File} representation of the data source. Could be a file path, a network address, or anything else
	 * implementation specific
	 * 
	 * @return data source string
	 */
	File getDataSourcePath();

	
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
	 * that, otherwise, create a dummy one.
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