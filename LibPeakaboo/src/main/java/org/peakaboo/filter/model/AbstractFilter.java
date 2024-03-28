package org.peakaboo.filter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

/**
 * 
 * This abstract class defines a filter for a {@link Spectrum} of data. A large part of this abstract
 * class is for use in UIs, as it focuses on classifying the type and name of the filter, and the
 * types of parameters. This provides a way for UIs to display the filter and let the user change the
 * settings.
 * 
 * 
 * @author Nathaniel Sherry
 * 
 */

public abstract class AbstractFilter implements Serializable, Filter {
	
	private List<Value<?>>		parameters;
	public boolean				enabled;
		
	
	//==============================================
	// PLUGIN METHODS
	//==============================================	
	
	protected AbstractFilter() {
		this.parameters = new ArrayList<>();
		this.enabled = true;
	}

	@Override
	public final List<Value<?>> getParameters() {
		return this.parameters;
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
	protected abstract SpectrumView filterApplyTo(SpectrumView data, Optional<FilterContext> ctx);
		
	

	@Override
	public SpectrumView filter(SpectrumView data, Optional<FilterContext> ctx) {
		
		try{
			SpectrumView newdata = filterApplyTo(data, ctx);
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
