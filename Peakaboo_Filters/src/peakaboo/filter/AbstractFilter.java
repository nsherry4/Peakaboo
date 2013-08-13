package peakaboo.filter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import autodialog.model.Parameter;
import bolt.plugin.BoltPlugin;
import peakaboo.common.Version;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

/**
 * 
 * This abstract class defines a filter for a {@link Spectrum} of data. A large part of this abstract
 * class is for use in UIs, as it focuses on classifying the type and name of the filter, and the
 * types of parameters. This provides a way for UIs to display the filter and let the user change the
 * settings.
 * 
 * 
 * @author Nathaniel Sherry, 2009-2012
 * 
 */

public abstract class AbstractFilter implements BoltPlugin, Serializable
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
		MATHEMATICAL {

			@Override
			public String toString()
			{
				return "Mathematical";
			}

		},
		ADVANCED {

			@Override
			public String toString()
			{
				return "Advanced";
			}

		},
		PROGRAMMING {
		
			@Override
			public String toString()
			{
				return "Programming";
			}
						
		};
		
		public String getSubPackage()
		{
			return "filters." + name().toLowerCase();
		}
		
		public String getFilterTypeDescription()
		{
			return toString() + " Filters";
		}
	}
	
	private Map<Integer, Parameter<?>>			parameters;
	public boolean							enabled;
	
	protected Spectrum	previewCache;
	protected Spectrum	calculatedData;

	private int nextParameterIndex = 0;
	
	
	public AbstractFilter()
	{
		this.parameters = new LinkedHashMap<>();
		this.enabled = true;
	}

	/**
	 * Returns a name for this plugin
	 */
	public abstract String getFilterName();
	
	/**
	 * Returns a short description for this plugin
	 */
	public abstract String getFilterDescription();
	

	/**
	 * Returns the type of the filter.
	 */
	public abstract FilterType getFilterType();


	/**
	 * Returns the parameters
	 */
	public final Map<Integer, Parameter<?>> getParameters()
	{
		return this.parameters;
	}
	
	/**
	 * Sets the parameters
	 */
	public final void setParameters(Map<Integer, Parameter<?>> params)
	{
		parameters = params;
	}
	
	protected void addParameter(Parameter<?> param)
	{
		int key = getNextParameterIndex();
		parameters.put(key, param);
	}
	
	protected void addParameter(Parameter<?>... params)
	{
		for (Parameter<?> param : params) { addParameter(param); }
	}
	

	/**
	 * Retrieves the parameter with the assocuated index
	 */
	public final Parameter<?> getParameter(Integer key)
	{
		return parameters.get(key);
	}

	protected final void setPreviewCache(Spectrum data)
	{
		this.previewCache = new Spectrum(data);
	}


	/**
	 * This method is called once before the filter is used.  
	 */
	public abstract void initialize();
	
	public abstract PlotPainter getPainter();
	
	/**
	 * Called whenever a parameter value is changed.
	 * @return true if the new values are valid, false otherwise
	 */
	public abstract boolean validateParameters();
	protected abstract Spectrum filterApplyTo(Spectrum data, boolean cache);
	
	/**
	 * Returns true if this filter can filter an arbitrarily-sized subset of the current data, false otherwise
	 */
	public abstract boolean canFilterSubset();
	
	
	/**
	 * Call's the subclass's {@link AbstractFilter#filterApplyTo(Spectrum, boolean)} method,
	 * catching exceptions that occur so as to prevent a filter from crashing the program.
	 * @param data the data to process
	 * @param cache whether or not this data should be cached for the purposes of drawing on the spectrum
	 * @return the result of applying the filter to data
	 */
	public Spectrum filter(Spectrum data, boolean cache)
	{
		
		try{
			Spectrum newdata = filterApplyTo(data, cache);
			if (newdata != null) return newdata;
			return data;
		}
		catch(Throwable e)
		{
			System.out.println(getFilterName() + " Filter Failed");
			if (!Version.release) e.printStackTrace();
			return data;
		}
		
	}
	
		
	

	private int getNextParameterIndex()
	{
		return nextParameterIndex++;
	}
	
	public String toString()
	{
		return this.getFilterName();
	}
	
	
}
