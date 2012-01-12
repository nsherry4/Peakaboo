import bolt.plugin.Plugin;
import peakaboo.filter.AbstractFilter;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

@Plugin
public class MyCustomFilter extends AbstractFilter
{

	@Override
	public void initialize()
	{
		
	}
	
	@Override
	public String getPluginDescription()
	{
		return "This filter calculates the reciporical value of each " +
				"channel, and normalizes the data so that the strongest" +
				"channel in the resultant spectrum is the same intensity" +
				"as the strongest channel in the input spectrum";
	}

	@Override
	public String getPluginName()
	{
		return "Reciporical";
	}

	@Override
	public boolean pluginEnabled()
	{
		return true;
	}

	@Override
	public boolean canFilterSubset()
	{
		return true;
	}

	@Override
	public FilterType getFilterType()
	{
		return FilterType.MATHEMATICAL;
	}

	@Override
	public boolean validateParameters()
	{
		//no parameters to validate
		return true;
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		Spectrum result = new Spectrum(data.size());
		float maxIn = data.get(0);
		float maxOut = 0;
		
		float value;
		for (int i = 0; i < data.size(); i++)
		{
			
			value = data.get(i);
			if (value == 0)
			{
				result.set(i, 0f);
			}
			else
			{
				result.set(i, 1f / value);
			}
			
			maxIn = Math.max(data.get(i), maxIn);
			maxOut = Math.max(result.get(i), maxOut);
		}
		if (maxOut == 0) maxOut = 1; 

		System.out.println(maxIn);
		System.out.println(maxOut);
		
		float multiplier = maxIn / maxOut;
		for (int i = 0; i < data.size(); i++)
		{
			result.set(i, result.get(i) * multiplier);
		}
		
		return result;
	}

	@Override
	public PlotPainter getPainter()
	{
		return null;
	}

}
