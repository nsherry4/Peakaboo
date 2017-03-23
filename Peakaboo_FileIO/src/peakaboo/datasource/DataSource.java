package peakaboo.datasource;

import java.util.List;

import peakaboo.datasource.components.DataSourceDimensions;
import peakaboo.datasource.components.DataSourceMetadata;
import peakaboo.datasource.components.DataSourceScanData;

public interface DataSource extends DataSourceScanData, DataSourceDimensions
{
	
	
	/**
	 * Returns a DataSourceMetadata, or null
	 */
	DataSourceMetadata getMetadata();
	
	/**
	 * Tests if a DataSource has metadata defined in {@link DataSourceMetadata}
	 * @return true if the data is provided, false otherwise
	 */
	default boolean hasMetadata() {
		return (getMetadata() != null);
	}
	
	
	/**
	 * Returns true if this data source supports information on dimensions
	 */
	boolean hasScanDimensions();
	
	
	/**
	 * Returns a list of strings representing the file extensions that
	 * this DataSource is capable of reading
	 */
	List<String> getFileExtensions();
	
	
	/**
	 * Returns true if this DataSource can read the given file as a whole 
	 * dataset, false otherwise.
	 */
	boolean canRead(String filename);

	/**
	 * Returns true if this DataSource can read the given files as a whole 
	 * dataset, false otherwise
	 */
	boolean canRead(List<String> filenames);
	

	/**
	 * Reads the given file as a whole dataset. This method, collectively with 
	 * {@link DataSource#read(List)}, will be called either 0 or 1 times 
	 * throughout the lifetime of this DataSource object.
	 * @throws Exception
	 */
	void read(String filename) throws Exception;
	
	/**
	 * Reads the given files as a whole dataset. This method, collectively with 
	 * {@link DataSource#read(String)}, will be called either 0 or 1 times 
	 * throughout the lifetime of this DataSource object.
	 * @throws Exception
	 */
	void read(List<String> filenames) throws Exception;
}
