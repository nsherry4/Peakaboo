package peakaboo.ui.swing.plotting.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import commonenvironment.IOOperations;
import peakaboo.datasource.DataSource;

public class DataSourceLookup
{

	public static List<DataSource> findDataSourcesForFiles(List<String> filenames, List<DataSource> dsps)
	{	
		
		List<DataSource> datasources = new ArrayList<DataSource>();
		
		if (filenames.size() == 1)
		{
			String filename = filenames.get(0);
			
			for (DataSource datasource : dsps)
			{
			
				if ( !matchFileExtension(filename, datasource.getFileFormat().getFileExtensions()) ) continue;
				if ( !datasource.getFileFormat().canRead(filename) ) continue;
				datasources.add(datasource);

				
			}//for datasources
		}
		else
		{
		
			//loop over every datasource
			for (DataSource datasource : dsps)
			{
				if ( !matchFileExtensions(filenames, datasource.getFileFormat().getFileExtensions()) ) continue;
				if ( !datasource.getFileFormat().canRead(new ArrayList<String>(filenames)) ) continue;
				datasources.add(datasource);
				
			}
			
		}
		
		return datasources;
		
	}
	
	private static boolean matchFileExtension(String filename, Collection<String> dsexts)
	{
		for (String dsext : dsexts)
		{
			
			if (IOOperations.getFileExt(filename).compareToIgnoreCase(dsext) == 0) return true;
		}
		return false;
	}
	
	private static boolean matchFileExtensions(Collection<String> filenames, Collection<String> dsexts)
	{
		for (String filename : filenames)
		{
			if (!matchFileExtension(filename, dsexts)) return false;
		}
		return true;
	}
}
