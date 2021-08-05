package org.peakaboo.filter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.plugins.JavaFilterPlugin;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

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

public abstract class AbstractFilter implements Serializable, JavaFilterPlugin {
	
	private List<Value<?>>		parameters;
	public boolean				enabled;
		
	
	//==============================================
	// PLUGIN METHODS
	//==============================================	

	@Override
	public String pluginName() {
		return getFilterName();
	}

	@Override
	public String pluginDescription() {
		return getFilterDescription();
	}
	
	
	public AbstractFilter() {
		this.parameters = new ArrayList<>();
		this.enabled = true;
	}

	@Override
	public final List<Value<?>> getParameters() {
		return this.parameters;
	}
	
	@Override
	public final void setParameters(List<Value<?>> values) {
		parameters = values;
	}
	
	protected void addParameter(Value<?> value) {
		parameters.add(value);
	}
	
	protected void addParameter(Value<?>... values) {
		for (Value<?> value : values) { addParameter(value); }
	}
	
	
	/**
	 * Filter the given {@link Spectrum} and return the modified result
	 * @param data the Spectrum to filter
	 */
	protected abstract ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx);
		
	

	@Override
	public ReadOnlySpectrum filter(ReadOnlySpectrum data, Optional<FilterContext> ctx) {
		
		try{
			ReadOnlySpectrum newdata = filterApplyTo(data, ctx);
			if (newdata != null) return newdata;
			return data;
		}
		catch(Throwable e)
		{
			PeakabooLog.get().log(Level.SEVERE, "Error applying filter " + this.getClass().getSimpleName(), e);
			return data;
		}
		
	}

	public String toString() {
		return this.getFilterName();
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}	
	
}
