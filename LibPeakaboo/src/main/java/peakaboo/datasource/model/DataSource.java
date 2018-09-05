package peakaboo.datasource.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.model.Value;
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
	 * Called before {@link DataSource#read(List)} with the same {@link List} of
	 * {@link Path}s. This gives the DataSource a chance to request more information
	 * about how the selected files shoudl be read.
	 * 
	 * @param paths the list of files which will be read
	 * @return the {@link Value} {@link Group} to be responded to, or
	 *         {@link Optional#empty()} if there are no parameters.
	 */
	Optional<Group> getParameters(List<Path> paths);
	Optional<Metadata> getMetadata();
	Optional<DataSize> getDataSize();
	Optional<PhysicalSize> getPhysicalSize();
	FileFormat getFileFormat();
	ScanData getScanData();
	
	Interaction getInteraction();
	void setInteraction(Interaction interaction);
	

		
	/**
	 * Reads the given files as a whole dataset. This method will be called either 0
	 * or 1 times throughout the lifetime of this DataSource object.
	 * 
	 * @throws Exception
	 */
	void read(List<Path> paths) throws Exception;
}
