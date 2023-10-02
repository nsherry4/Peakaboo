package org.peakaboo.dataset.source.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormatCompatibility;
import org.peakaboo.dataset.source.model.datafile.DataFile;

public class DataSourceLookup {

	public static List<DataSourcePlugin> findDataSourcesForFiles(List<DataFile> datafiles, List<DataSourcePlugin> dsps) {	
		
		List<DataSourcePlugin> maybeByFilename = new ArrayList<>();
		List<DataSourcePlugin> maybeByContents = new ArrayList<>();
		List<DataSourcePlugin> yesByContents = new ArrayList<>();
		
		PeakabooLog.get().log(Level.INFO, "Discovering compatible DataSource plugins");

		for (DataSourcePlugin datasource : dsps) {
			try {
				FileFormatCompatibility compat = datasource.getFileFormat().compatibility(new ArrayList<>(datafiles));
				
				PeakabooLog.get().log(Level.INFO, "DataSource plugin '" + datasource.pluginName() + "' (" + datasource.pluginUUID() + ") answers '" + compat.toString() + "'");
				
				if ( compat == FileFormatCompatibility.NO ) continue;
				if ( compat == FileFormatCompatibility.MAYBE_BY_FILENAME) { maybeByFilename.add(datasource); }
				if ( compat == FileFormatCompatibility.MAYBE_BY_CONTENTS) { maybeByContents.add(datasource); }
				if ( compat == FileFormatCompatibility.YES_BY_CONTENTS) { yesByContents.add(datasource); }
			} catch (Throwable e) {
				PeakabooLog.get().log(Level.WARNING, "Error while evaluating data source " + datasource.pluginName(), e);
			} 
		}
			
		
		if (!yesByContents.isEmpty()) { return yesByContents; }
		if (!maybeByContents.isEmpty()) { return maybeByContents; }
		return maybeByFilename;
		
	}

}
