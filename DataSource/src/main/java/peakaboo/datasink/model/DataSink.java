package peakaboo.datasink.model;

import java.io.IOException;
import java.nio.file.Path;

import peakaboo.datasource.model.DataSource;

public interface DataSink
{
	
	void write(DataSource source, Path destination) throws IOException;
	
	
	
	String getFormatExtension();
	
	/**
	 * Returns a name for this DataSource Plugin
	 */
	String getFormatName();
	
	
	/**
	 * Returns a description for this DataSource Plugin
	 */
	String getFormatDescription();
	
}
