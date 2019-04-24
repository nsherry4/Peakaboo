package org.peakaboo.framework.autodialog.view.swing.editors;

import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;

public abstract class AbstractSwingEditor<T> implements SwingEditor<T> {

	protected Parameter<T> param;
	
	private EventfulType<T> editorValueHook = new EventfulType<>();

	public EventfulType<T> getEditorValueHook() {
		return editorValueHook;
	}
	
	public String getTitle() {
		return param.getName();
	}
	
	public Value<T> getValue() {
		return param;
	}
	
	@Override
	public final void setFromParameter()
	{
		boolean equiv = false;
		if (param.getValue() == null && getEditorValue() == null) {
			equiv = true;
		} else if (param.getValue() == null) {
			equiv = false;
		} else if (param.getValue().equals(getEditorValue())) {
			equiv = true;
		}
		
		if (! equiv) {
			setEditorValue(param.getValue());
		}
		
		setEnabled(param.isEnabled());
		
	}
	
	protected abstract void setEnabled(boolean enabled);
	
}
