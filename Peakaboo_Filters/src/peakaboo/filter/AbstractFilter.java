package peakaboo.filter;


import java.io.Serializable;
import java.util.Map;

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
				return "Background Removal";
			}
		},
		NOISE {

			@Override
			public String toString()
			{
				return "Noise Removal";
			}
		},
		ARITHMETIC {

			@Override
			public String toString()
			{
				return "Arithmetic";
			}
		},
		ADVANCED {

			@Override
			public String toString()
			{
				return "Advanced";
			}	
		}
	}
	
	public Map<Object, Parameter>		parameters;
	public boolean						enabled;
	
	protected Spectrum	previewCache;
	protected Spectrum	calculatedData;

	public AbstractFilter()
	{
		this.parameters = DataTypeFactory.<Object, Parameter> map();
		this.enabled = true;
	}


	public abstract String getFilterName();


	public abstract String getFilterDescription();


	public abstract FilterType getFilterType();


	public final Map<Object, Parameter> getParameters()
	{
		return this.parameters;
	}
	public final void setParameters(Map<Object, Parameter> params)
	{
		parameters = params;
	}

	public final Parameter getParameter(Object key)
	{
		return parameters.get(key);
	}

	protected final void setPreviewCache(Spectrum data)
	{
		this.previewCache = new Spectrum(data);
	}


	public abstract PlotPainter getPainter();


	public abstract boolean validateParameters();
	
	
	protected abstract Spectrum filterApplyTo(Spectrum data, boolean cache);

	
	public Spectrum filter(Spectrum data, boolean cache)
	{
		try{
			return filterApplyTo(data, cache);
		}
		catch(Exception e)
		{
			System.err.println(getFilterName() + " Failed");
			return data;
		}
	}
	
	public abstract boolean showFilter();

}
