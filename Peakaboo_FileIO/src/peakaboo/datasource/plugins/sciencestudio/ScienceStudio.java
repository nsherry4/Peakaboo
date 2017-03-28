package peakaboo.datasource.plugins.sciencestudio;

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
}
