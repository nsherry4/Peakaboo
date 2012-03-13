package peakaboo.datasource.plugin.plugins;

import peakaboo.datasource.plugin.plugins.sciencestudio.ConverterFactoryDelegatingDSP;

/**
 * @author maxweld
 *
 */
public class ScienceStudioDSP extends ConverterFactoryDelegatingDSP
{

	@Override
	public String getDataFormat() {
		return "CLS Data Acquisition Format";
	}

	@Override
	public String getDataFormatDescription()
	{
		return "Data format used by the Canadian Light Source for XRF collection on the VESPERS beamline";
	}
}
