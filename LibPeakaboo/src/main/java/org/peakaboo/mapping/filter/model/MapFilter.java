package org.peakaboo.mapping.filter.model;

import java.util.List;

import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.model.Value;

public interface MapFilter {

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
	 * Returns a short title-cased string (ideally 1 word) describing the kind of
	 * thing that this filter does in the past tense (e.g. Enlarged, Smoothed)
	 */
	MapFilterDescriptor getFilterDescriptor();
	
	/**
	 * Returns the parameters
	 */
	List<Value<?>> getParameters();

	/**
	 * Sets the parameters
	 */
	void setParameters(List<Value<?>> params);


	default Group getParameterGroup() {
		return new Group(getFilterName(), getParameters()); 
	}
	
	/**
	 * This method is called once before the filter is used.  
	 */
	void initialize();

	
	AreaMap filter(AreaMap map);
	
	/**
	 * Returns true if and only if this MapFilter does not change the relationship between map pixels and the originating set of spectra;
	 */
	boolean isReplottable();	
	
	
}
