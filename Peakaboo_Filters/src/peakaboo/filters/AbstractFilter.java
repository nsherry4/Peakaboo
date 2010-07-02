package peakaboo.filters;


import java.io.Serializable;
import java.util.List;

import peakaboo.calculations.Background;
import peakaboo.calculations.Calculations;
import peakaboo.calculations.Noise;
import peakaboo.datatypes.DataTypeFactory;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

/**
 * 
 * This abstract class defines a filter for a {@link Spectrum} of data. Also contains messy logic for enumerating
 * all classes in this package which inherit from this class, and are thus considered filters. This is
 * primarily intended for use in UIs, as it focuses on classifying the type and name of the filter, and the
 * types of parameters. This provides a way for UIs to display the filter and let the user change the
 * settings. When applying filters programmatically, it would probably be more desirable to simple use the
 * functions in the {@link Calculations}, {@link SpectrumCalculations}, {@link Background}, and {@link Noise}
 * classes which are the backend for these filters.
 * 
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class AbstractFilter implements Serializable
{

	public static enum FilterType
	{
		BACKGROUND {

			@Override
			public String toString()
			{
				return "Background Fit";
			}
		},
		NOISE {

			@Override
			public String toString()
			{
				return "Noise Filter";
			}
		},
		MATHEMATICAL {

			@Override
			public String toString()
			{
				return "Mathematical Filter";
			}
		},
	}
	
	protected List<Parameter<?>>	parameters;
	public boolean					enabled;


	public AbstractFilter()
	{
		this.parameters = DataTypeFactory.<Parameter<?>> list();
		this.enabled = true;
	}


	public abstract String getFilterName();


	public abstract String getFilterDescription();


	public abstract FilterType getFilterType();


	public final List<Parameter<?>> getParameters()
	{
		return this.parameters;
	}

	protected Spectrum	previewCache;
	protected Spectrum	calculatedData;


	protected final void setPreviewCache(Spectrum data)
	{
		this.previewCache = new Spectrum(data);
	}


	public abstract PlotPainter getPainter();


	public abstract boolean validateParameters();
	
	
	public abstract Spectrum filterApplyTo(Spectrum data, boolean cache);


	protected <T1> T1 getParameterValue(int number)
	{
		return ((Parameter<T1>)parameters.get(number)).getValue();
	}
	
	protected <T1> void setParameterValue(int number, T1 value)
	{
		((Parameter<T1>)parameters.get(number)).setValue(value);
	}
	
	public abstract boolean showFilter();

}
