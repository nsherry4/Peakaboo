package peakaboo.datasource;

import java.util.List;

import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.interaction.DataSourceInteraction;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import peakaboo.datasource.components.scandata.ScanData;

public interface DataSource
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
	
	
	
	
	DataSourceDimensions getDimensions();
	
	default boolean hasDimensions() {
		return (getDimensions() != null);		
	}
	

	

	DataSourceFileFormat getFileFormat();
	
	void setInteraction(DataSourceInteraction interaction);
	DataSourceInteraction getInteraction();
	
	default boolean hasInteraction() {
		return (getInteraction() != null);
	}
	
	
	
	
	default boolean hasScanData() {
		return (getScanData() != null);
	}
	ScanData getScanData();
	
	
	
	

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
