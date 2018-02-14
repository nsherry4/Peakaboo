package net.sciencestudio.autodialog.view.swing.editors;

import eventful.EventfulType;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.Value;

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
