package org.peakaboo.datasource.plugin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasource.model.components.fileformat.FileFormatCompatibility;

public class DataSourceLookup
{

	public static List<DataSourcePlugin> findDataSourcesForFiles(List<Path> paths, List<DataSourcePlugin> dsps)
	{	
		
		List<DataSourcePlugin> maybe_by_filename = new ArrayList<>();
		List<DataSourcePlugin> maybe_by_contents = new ArrayList<>();
		List<DataSourcePlugin> yes_by_contents = new ArrayList<>();
		
		PeakabooLog.get().log(Level.INFO, "Discovering compatible DataSource plugins");

		for (DataSourcePlugin datasource : dsps)
		{
			try {
				FileFormatCompatibility compat = datasource.getFileFormat().compatibility(new ArrayList<>(paths));
				
				PeakabooLog.get().log(Level.INFO, "DataSource plugin '" + datasource.pluginName() + "' (" + datasource.pluginUUID() + ") answers '" + compat.toString() + "'");
				
				if ( compat == FileFormatCompatibility.NO ) continue;
				if ( compat == FileFormatCompatibility.MAYBE_BY_FILENAME) { maybe_by_filename.add(datasource); }
				if ( compat == FileFormatCompatibility.MAYBE_BY_CONTENTS) { maybe_by_contents.add(datasource); }
				if ( compat == FileFormatCompatibility.YES_BY_CONTENTS) { yes_by_contents.add(datasource); }
			} 
			catch (Throwable e) {
				PeakabooLog.get().log(Level.SEVERE, "Error while evaluating data sources", e);
			} 
		}
			
		
		if (yes_by_contents.size() > 0) { return yes_by_contents; }
		if (maybe_by_contents.size() > 0) { return maybe_by_contents; }
		return maybe_by_filename;
		
	}

}
