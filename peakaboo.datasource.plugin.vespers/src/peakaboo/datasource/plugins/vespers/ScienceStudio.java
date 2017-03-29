package peakaboo.datasource.plugins.vespers;

import java.util.Arrays;

import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;

/**
 * @author maxweld
 *
 */
public class ScienceStudio extends ConverterFactoryDelegatingDSP
{

	@Override
	public String getFormatName() {
		return "CLS Data Acquisition Format";
	}

	@Override
	public String getFormatDescription()
	{
		return "Data format used by the Canadian Light Source for XRF collection on the VESPERS beamline";
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}
	
	
	@Override
	public DataSourceFileFormat getFileFormat() {
		return new SimpleFileFormat(
				true, 
				"Vespers XRF", 
				"Data format used by the Canadian Light Source for XRF collection on the VESPERS beamline", 
				Arrays.asList("dat"));
	}
	
}
