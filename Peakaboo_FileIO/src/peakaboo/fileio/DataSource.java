package peakaboo.fileio;

public interface DataSource extends DSScanData, DSRealDimensions, DSMetadata
{
	
	
	/**
	 * Returns true if this data source supports metadata
	 * @return
	 */
	public boolean hasMetadata();
	
	
	/**
	 * Returns true if this data source supports information on real dimensions
	 * @return
	 */
	public boolean hasRealDimensions();
}
