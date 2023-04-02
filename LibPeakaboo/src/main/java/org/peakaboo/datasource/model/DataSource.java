package org.peakaboo.datasource.model;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.datafile.DataFile;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Value;

public interface DataSource
{
	
	
	public class DataSourceReadException extends Exception {
		
		public DataSourceReadException(String message) {
			super(message);
		}
		
		public DataSourceReadException(String message, Throwable cause) {
			super(message, cause);
		}
	}
	
	/**
	 * Reads the given files as a whole dataset. This method will be called either 0
	 * or 1 times throughout the lifetime of this DataSource object.
	 * 
	 * @throws DataSourceReadException
	 * @throws IOException
	 */
	void read(List<DataFile> datafiles) throws DataSourceReadException, IOException, InterruptedException;
	
	
	default boolean isRectangular() {
		return true;
	}

	/**
	 * Called before {@link DataSource#read(List)} with the same {@link List} of
	 * {@link DataFile}s. This gives the DataSource a chance to request more information
	 * about how the selected files shoudl be read.
	 * 
	 * @param datafiles the list of files which will be read
	 * @return the {@link Value} {@link Group} to be responded to, or
	 *         {@link Optional#empty()} if there are no parameters.
	 */
	Optional<Group> getParameters(List<DataFile> datafiles) throws DataSourceReadException, IOException;

	
	Optional<Metadata> getMetadata();
	Optional<DataSize> getDataSize();
	Optional<PhysicalSize> getPhysicalSize();
	FileFormat getFileFormat();
	ScanData getScanData();
	
	Interaction getInteraction();
	void setInteraction(Interaction interaction);
	

		

	
}
