package org.peakaboo.datasource.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.datasource.model.components.fileformat.FileFormatCompatibility;
import org.peakaboo.datasource.model.datafile.DataFile;

public class DataSourceLookup {

	public static List<JavaDataSourcePlugin> findDataSourcesForFiles(List<DataFile> datafiles, List<JavaDataSourcePlugin> dsps) {	
		
		List<JavaDataSourcePlugin> maybeByFilename = new ArrayList<>();
		List<JavaDataSourcePlugin> maybeByContents = new ArrayList<>();
		List<JavaDataSourcePlugin> yesByContents = new ArrayList<>();
		
		PeakabooLog.get().log(Level.INFO, "Discovering compatible DataSource plugins");

		for (JavaDataSourcePlugin datasource : dsps) {
			try {
				FileFormatCompatibility compat = datasource.getFileFormat().compatibilityWithDataFile(new ArrayList<>(datafiles));
				
				PeakabooLog.get().log(Level.INFO, "DataSource plugin '" + datasource.pluginName() + "' (" + datasource.pluginUUID() + ") answers '" + compat.toString() + "'");
				
				if ( compat == FileFormatCompatibility.NO ) continue;
				if ( compat == FileFormatCompatibility.MAYBE_BY_FILENAME) { maybeByFilename.add(datasource); }
				if ( compat == FileFormatCompatibility.MAYBE_BY_CONTENTS) { maybeByContents.add(datasource); }
				if ( compat == FileFormatCompatibility.YES_BY_CONTENTS) { yesByContents.add(datasource); }
			} catch (Throwable e) {
				PeakabooLog.get().log(Level.SEVERE, "Error while evaluating data sources", e);
			} 
		}
			
		
		if (!yesByContents.isEmpty()) { return yesByContents; }
		if (!maybeByContents.isEmpty()) { return maybeByContents; }
		return maybeByFilename;
		
	}

}
