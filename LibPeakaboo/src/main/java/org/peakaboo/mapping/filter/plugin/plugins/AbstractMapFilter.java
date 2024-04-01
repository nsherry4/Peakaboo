package org.peakaboo.mapping.filter.plugin.plugins;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;

public abstract class AbstractMapFilter implements MapFilterPlugin {

	private boolean enabled;
	private List<Value<?>> parameters;
	
	protected AbstractMapFilter() {
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
		getParameters().add(param);
	}
	
	protected void addParameter(Parameter<?>... params)	{
		for (Parameter<?> param : params) { addParameter(param); }
	}
	

	



}
