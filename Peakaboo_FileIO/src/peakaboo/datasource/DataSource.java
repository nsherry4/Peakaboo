package peakaboo.datasource;

import java.util.List;

public interface DataSource extends DSScanData, DSRealDimensions, DSMetadata
{
	
	
	/**
	 * Returns true if this data source supports metadata
	 */
	public boolean hasMetadata();
	
	
	/**
	 * Returns true if this data source supports information on real dimensions
	 */
	public boolean hasRealDimensions();
	
	
	/**
	 * Returns a list of strings representing the file extensions that
	 * this DataSource is capable of reading
	 */
	public abstract List<String> getFileExtensions();
	
	
	/**
	 * Returns true if this DataSource can read the given file as a whole 
	 * dataset, false otherwise.
	 */
	public abstract boolean canRead(String filename);

	/**
	 * Returns true if this DataSource can read the given files as a whole 
	 * dataset, false otherwise
	 */
	public abstract boolean canRead(List<String> filenames);
	

	/**
	 * Reads the given file as a whole dataset. This method, collectively with 
	 * {@link DataSource#read(List)}, will be called either 0 or 1 times 
	 * throughout the lifetime of this DataSource object.
	 * @throws Exception
	 */
	public abstract void read(String filename) throws Exception;
	
	/**
	 * Reads the given files as a whole dataset. This method, collectively with 
	 * {@link DataSource#read(String)}, will be called either 0 or 1 times 
	 * throughout the lifetime of this DataSource object.
	 * @throws Exception
	 */
	public abstract void read(List<String> filenames) throws Exception;
}
