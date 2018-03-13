package peakaboo.datasource.model;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.interaction.Interaction;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;

public interface DataSource
{
	
	
	default boolean isContiguous() {
		return true;
	}
	
	/**
	 * Returns a DataSourceMetadata, or null
	 */
	Metadata getMetadata();
	
	/**
	 * Tests if a DataSource has metadata defined in {@link Metadata}
	 * @return true if the data is provided, false otherwise
	 */
	default boolean hasMetadata() {
		return (getMetadata() != null);
	}
	
	
	
	
	DataSize getDataSize();
	
	default boolean hasDataSize() {
		return (getDataSize() != null);		
	}
	
	
	PhysicalSize getPhysicalSize();
	
	default boolean hasPhysicalSize() {
		return (getPhysicalSize() != null);		
	}
	

	

	FileFormat getFileFormat();
	
	
	
	void setInteraction(Interaction interaction);
	Interaction getInteraction();
	
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
	void read(Path path) throws Exception;
	
	/**
	 * Reads the given files as a whole dataset. This method, collectively with 
	 * {@link DataSource#read(Path)}, will be called either 0 or 1 times 
	 * throughout the lifetime of this DataSource object.
	 * @throws Exception
	 */
	void read(List<Path> paths) throws Exception;
}
