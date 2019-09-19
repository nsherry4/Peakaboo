package org.peakaboo.datasource.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.datafile.DataFile;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Value;

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
	@Deprecated(since="5.4", forRemoval=true)
	Optional<Group> getParameters(List<Path> paths);
	//TODO: this is temporary, with 6.0 data source plugins should accept DataFiles directly
	default Optional<Group> getParametersForDataFile(List<DataFile> datafiles) {
		List<Path> paths = new ArrayList<>();
		try {
			for (DataFile f : datafiles) {
				paths.add(f.getAndEnsurePath());
			}
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to get DataSource parameters", e);
			return Optional.empty();
		}
		return getParameters(paths);
	}
	
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
	@Deprecated(since="5.4", forRemoval=true)
	void read(List<Path> paths) throws Exception;

	default void readDataFiles(List<DataFile> datafiles) throws Exception {
		List<Path> paths = new ArrayList<>();
		for (DataFile f : datafiles) {
			paths.add(f.getAndEnsurePath());
			getInteraction().notifyScanOpened(1);
		}
		this.read(paths);
	}
}
