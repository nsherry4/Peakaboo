package peakaboo.datasource.plugins.sciencestudio;

/**
 * @author maxweld
 *
 */
public class ScienceStudio extends ConverterFactoryDelegatingDSP
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
