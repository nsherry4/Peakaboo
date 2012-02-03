package peakaboo.ui.swing.plotting.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import peakaboo.datasource.plugin.AbstractDSP;

import commonenvironment.IOOperations;

public class DataSourceLookup
{

	public static List<AbstractDSP> findDataSourcesForFiles(List<String> filenames, List<AbstractDSP> dsps)
	{	
		
		List<AbstractDSP> datasources = new ArrayList<AbstractDSP>();
		
		if (filenames.size() == 1)
		{
			String filename = filenames.get(0);
			
			for (AbstractDSP datasource : dsps)
			{
			
				if ( !matchFileExtension(filename, datasource.getFileExtensions()) ) continue;
				if ( !datasource.canRead(filename) ) continue;
				datasources.add(datasource);

				
			}//for datasources
		}
		else
		{
		
			//loop over every datasource
			for (AbstractDSP datasource : dsps)
			{
				
				if ( !matchFileExtensions(filenames, datasource.getFileExtensions()) ) continue;
				if ( !datasource.canRead(filenames) ) continue;
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
