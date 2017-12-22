package peakaboo.datasink.model;

import java.io.IOException;
import java.io.OutputStream;

import peakaboo.datasource.model.DataSource;

public interface DataSink
{
	
	void write(DataSource source, OutputStream destination) throws IOException;
	
	
	
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
