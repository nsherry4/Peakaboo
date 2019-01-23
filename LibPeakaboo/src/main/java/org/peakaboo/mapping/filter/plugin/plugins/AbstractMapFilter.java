package org.peakaboo.mapping.filter.plugin.plugins;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;

import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.Value;

public abstract class AbstractMapFilter implements JavaMapFilterPlugin {

	private boolean enabled;
	private List<Value<?>> parameters;
	
	public AbstractMapFilter() {
		this.parameters = new ArrayList<>();
		this.enabled = true;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	
	
	@Override
	public List<Value<?>> getParameters() {
		return parameters;
	}

	@Override
	public void setParameters(List<Value<?>> params) {
		this.parameters = params;
	}
	
	protected void addParameter(Parameter<?> param) {
		parameters.add(param);
	}
	
	protected void addParameter(Parameter<?>... params)	{
		for (Parameter<?> param : params) { addParameter(param); }
	}
	

	
	@Override
	public String pluginName() {
		return getFilterName();
	}

	@Override
	public String pluginDescription() {
		return getFilterDescription();
	}


}
