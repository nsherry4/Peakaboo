package org.peakaboo.filter.model;

import java.util.List;
import java.util.Optional;

import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.SavedPlugin;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public interface Filter extends BoltJavaPlugin {


	public static record FilterContext(DataSet dataset, FittingSetView fittings) {};
	
	boolean isEnabled();
	void setEnabled(boolean enabled);
	
	/**
	 * Returns a name for this plugin
	 */
	String getFilterName();

	/**
	 * Returns a short description for this plugin
	 */
	String getFilterDescription();

	/**
	 * Returns a FilterDescriptor detailing the category and action of this filter
	 */
	FilterDescriptor getFilterDescriptor();
	
	/**
	 * Returns a UUID uniquely identifying this kind of filter -- not unique to this instance.
	 */
	String getFilterUUID();
	
	/**
	 * Returns the parameters
	 */
	List<Value<?>> getParameters();

	default Group getParameterGroup() {
		return new Group(getFilterName(), getParameters()); 
	}
	
	/**
	 * This method is called once before the filter is used.  
	 */
	void initialize();
	
	
	/**
	 * Indicates that this filter should not be applied, but that any UI should show
	 * a preview of the filter.
	 */
	default boolean isPreviewOnly() {
		return false;
	}
	

	/**
	 * Returns true if this filter can filter an arbitrarily-sized subset of the current data, false otherwise
	 */
	boolean canFilterSubset();

	
	default FilterContext requireContext(Optional<FilterContext> ctx) {
		if (!ctx.isPresent()) {
			throw new IllegalArgumentException("This filter requires a FilterContext");
		}
		return ctx.get();
	}
	
	SpectrumView filter(SpectrumView data, Optional<FilterContext> ctx);
	
	/**
	 * Convenience method to wrap the {@link FilterContext} in an {@link Optional}
	 */
	default SpectrumView filter(SpectrumView data, FilterContext ctx) {
		return filter(data, Optional.of(ctx));
	}
	
	/**
	 * Filter a spectrum without a filter context. If a plugin needs a filter
	 * context, it should throw an exception here. Unpacking the context or throwing
	 * an exception can be done with the convenience method
	 * {@link Filter#requireContext(Optional)}
	 */
	default SpectrumView filter(SpectrumView data) {
		return filter(data, Optional.empty());
	}
	
	default String pluginUUID() {
		return this.getFilterUUID();
	}
	
	default String pluginName() {
		return getFilterName();
	}

	default String pluginDescription() {
		return getFilterDescription();
	}
	
	
	default SavedPlugin save() {
		return new SavedPlugin(getFilterUUID(), getFilterName(), getParameterGroup().serialize());
	}
	
}
