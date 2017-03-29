package peakaboo.controller.mapper.mapset;

public class MapSetPreferences
{

	public MapSetPreferences(MapSetPreferences copy)
	{
		super();
		this.drawCoordinates = copy.drawCoordinates;
		this.drawSpectrum = copy.drawSpectrum;
		this.drawTitle = copy.drawTitle;
		this.showDataSetTitle = copy.showDataSetTitle;
		this.spectrumSteps = copy.spectrumSteps;
		this.contour = copy.contour;
		this.interpolation = copy.interpolation;
		this.monochrome = copy.monochrome;
	}
	
	
	public MapSetPreferences()
	{
	
	}
	
	public boolean	drawCoordinates		= true;
	public boolean	drawSpectrum		= true;
	public boolean	drawTitle			= true;
	
	public boolean	showDataSetTitle	= false;
	public int		spectrumSteps		= 15;
	public boolean	contour				= false;
	public int		interpolation		= 0;
	public boolean	monochrome			= false;

}