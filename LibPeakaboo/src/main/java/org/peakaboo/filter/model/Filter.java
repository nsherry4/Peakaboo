package org.peakaboo.filter.model;

import java.util.List;

import org.peakaboo.dataset.DataSet;
import org.peakaboo.dataset.EmptyDataSet;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;

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
	 * Applies the filter to the given spectrum.
	 * 
	 * @param data    the data to process
	 * @param dataset the DataSet to which this spectrum belongs
	 * @return the result of applying the filter to data
	 */
	ReadOnlySpectrum filter(ReadOnlySpectrum data, DataSet dataset);
	
	/**
	 * Applies the filter to the given spectrum, supplying an empty {@link DataSet}
	 * @param data the data to process
	 * @return the result of applying the filter to data
	 */
	@Deprecated(forRemoval=true, since="5.4")
	default ReadOnlySpectrum filter(ReadOnlySpectrum data) {
		return filter(data, new EmptyDataSet());
	}
	

}