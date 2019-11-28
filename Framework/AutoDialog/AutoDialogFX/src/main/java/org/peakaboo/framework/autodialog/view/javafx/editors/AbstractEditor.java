package org.peakaboo.framework.autodialog.view.javafx.editors;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.eventful.EventfulType;


public abstract class AbstractEditor<T> implements FXEditor<T> {

	protected Parameter<T> parameter;
	private EventfulType<T> editorValueHook = new EventfulType<>();
	
	AbstractEditor() {}

	@Override
	public final void initialize(Parameter<T> parameter) {
		
		this.parameter = parameter;
		
		parameter.getValueHook().addListener(v -> setFromParameter());
		parameter.getEnabledHook().addListener(e -> onEnabledChange());
		
		init(parameter);
		setFromParameter();

	}

	
	
	public EventfulType<T> getEditorValueHook() {
		return editorValueHook;
	}
	
	@Override
	public Value<T> getValue() {
		return parameter;
	}
	
	
	protected void onEnabledChange() {
		getComponent().setDisable(!parameter.isEnabled());
	}

	@Override
	public final void setFromParameter()
	{
	
		boolean equiv = false;
		if (parameter.getValue() == null && getEditorValue() == null) {
			equiv = true;
		} else if (parameter.getValue() == null) {
			equiv = false;
		} else if (parameter.getValue().equals(getEditorValue())) {
			equiv = true;
		}
		
		if (! equiv) {
			setEditorValue(parameter.getValue());
		}
	}
	
	@Override
	public String getTitle() {
		return parameter.getName();
	}

	
	public abstract void init(Parameter<T> value);
	
	
}
