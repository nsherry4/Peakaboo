package org.peakaboo.datasource.model.components.fileformat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.datasource.model.datafile.DataFile;

public interface FileFormat {

	/**
	 * Returns a list of strings representing the file extensions that
	 * this DataSource is capable of reading. This is used only for UI
	 * related reasons (like showing a file selection dialog with 
	 * specific file extensions). For everything else, the 
	 * {@link FileFormat#compatibility(String)} and 
	 * {@link FileFormat#compatibility(List)} methods are used.
	 */
	List<String> getFileExtensions();
	
	
	/**
	 * Determines if this DataSource can read the given files as a whole 
	 * dataset, and returns info on how certain it is
	 */
	@Deprecated(since="5.4", forRemoval=true)
	FileFormatCompatibility compatibility(List<Path> filenames);
	
	// TODO: this is temporary -- with 6.0 plugins should natively accept DataFiles
	// and this method should be renamed 'compatibility'
	default FileFormatCompatibility compatibilityWithDataFile(List<DataFile> datafiles) throws IOException {
		List<Path> paths = new ArrayList<>();
		for (DataFile f : datafiles) {
			paths.add(f.getAndEnsurePath());
		}
		return compatibility(paths);
	}
	
	/**
	 * Returns a name for this DataSource Plugin
	 */
	String getFormatName();
	
	
	/**
	 * Returns a description for this DataSource Plugin
	 */
	String getFormatDescription();
	
}
