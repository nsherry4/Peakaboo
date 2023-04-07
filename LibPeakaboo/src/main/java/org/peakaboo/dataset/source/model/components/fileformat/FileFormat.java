package org.peakaboo.dataset.source.model.components.fileformat;

import java.io.IOException;
import java.util.List;

import org.peakaboo.dataset.source.model.datafile.DataFile;

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
	FileFormatCompatibility compatibility(List<DataFile> filenames) throws IOException;

	/**
	 * Returns a name for this DataSource Plugin
	 */
	String getFormatName();
	
	
	/**
	 * Returns a description for this DataSource Plugin
	 */
	String getFormatDescription();
	
}
