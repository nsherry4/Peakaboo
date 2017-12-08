package peakaboo.ui.swing.plotting.datasource;

import java.util.ArrayList;
import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.fileformat.FileFormatCompatibility;

public class DataSourceLookup
{

	public static List<DataSource> findDataSourcesForFiles(List<String> filenames, List<DataSource> dsps)
	{	
		
		List<DataSource> maybe_by_filename = new ArrayList<DataSource>();
		List<DataSource> maybe_by_contents = new ArrayList<DataSource>();
		List<DataSource> yes_by_contents = new ArrayList<DataSource>();
		
		if (filenames.size() == 1)
		{
			String filename = filenames.get(0);
			for (DataSource datasource : dsps)
			{
				try {
					FileFormatCompatibility compat = datasource.getFileFormat().compatibility(filename);
					if ( compat == FileFormatCompatibility.NO ) continue;
					if ( compat == FileFormatCompatibility.MAYBE_BY_FILENAME) { maybe_by_filename.add(datasource); }
					if ( compat == FileFormatCompatibility.MAYBE_BY_CONTENTS) { maybe_by_contents.add(datasource); }
					if ( compat == FileFormatCompatibility.YES_BY_CONTENTS) { yes_by_contents.add(datasource); }
				} 
				catch (Throwable e) {
					e.printStackTrace();
				} 
			}
		}
		else
		{
			for (DataSource datasource : dsps)
			{
				try {
					FileFormatCompatibility compat = datasource.getFileFormat().compatibility(new ArrayList<String>(filenames));
					if ( compat == FileFormatCompatibility.NO ) continue;
					if ( compat == FileFormatCompatibility.MAYBE_BY_FILENAME) { maybe_by_filename.add(datasource); }
					if ( compat == FileFormatCompatibility.MAYBE_BY_CONTENTS) { maybe_by_contents.add(datasource); }
					if ( compat == FileFormatCompatibility.YES_BY_CONTENTS) { yes_by_contents.add(datasource); }
				} 
				catch (Throwable e) {
					e.printStackTrace();
				} 
			}
			
		}
		if (yes_by_contents.size() > 0) { return yes_by_contents; }
		if (maybe_by_contents.size() > 0) { return maybe_by_contents; }
		return maybe_by_filename;
		
	}

}
