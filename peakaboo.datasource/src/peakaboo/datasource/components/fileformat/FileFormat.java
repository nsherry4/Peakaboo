package peakaboo.datasource.components.fileformat;

import java.util.List;

public interface FileFormat {

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
	 * Returns a name for this DataSource Plugin
	 */
	String getFormatName();
	
	
	/**
	 * Returns a description for this DataSource Plugin
	 */
	String getFormatDescription();
	
}
