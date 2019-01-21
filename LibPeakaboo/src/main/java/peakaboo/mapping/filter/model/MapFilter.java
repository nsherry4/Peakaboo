package peakaboo.mapping.filter.model;

import java.util.List;

import cyclops.Coord;
import cyclops.Spectrum;
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
	
	
}
