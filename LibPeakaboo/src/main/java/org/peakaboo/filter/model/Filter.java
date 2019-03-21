package org.peakaboo.filter.model;

import java.util.List;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Value;

import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;

public interface Filter {

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
	 * Returns the type of the filter.
	 */
	FilterType getFilterType();

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
	
	
	/**
	 * Indicates that this filter should not be applied, but that any UI should show
	 * a preview of the filter.
	 */
	default boolean isPreviewOnly() {
		return false;
	}
	

//	/**
//	 * Forces a check to ensure the Fitler's Parameters are valid.
//	 * @return true if the new values are valid, false otherwise
//	 */
//	boolean validateParameters();

	/**
	 * Returns true if this filter can filter an arbitrarily-sized subset of the current data, false otherwise
	 */
	boolean canFilterSubset();

	/**
	 * Call's the subclass's {@link AbstractFilter#filterApplyTo(Spectrum, boolean)} method,
	 * catching exceptions that occur so as to prevent a filter from crashing the program.
	 * @param data the data to process
	 * @param cache whether or not this data should be cached for the purposes of drawing on the spectrum
	 * @return the result of applying the filter to data
	 */
	ReadOnlySpectrum filter(ReadOnlySpectrum data);
	
	

}