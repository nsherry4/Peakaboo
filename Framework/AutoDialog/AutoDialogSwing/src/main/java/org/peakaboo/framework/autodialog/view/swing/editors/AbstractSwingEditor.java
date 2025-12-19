package org.peakaboo.framework.autodialog.view.swing.editors;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.eventful.EventfulType;

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
	public final void setFromParameter() {
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

	/**
	 * Notifies the parameter and listeners of a value change from the editor.
	 * This method validates the new value before notifying listeners, ensuring
	 * that only successfully validated values are propagated.
	 * <p>
	 * Call this method from UI event handlers (e.g., change listeners, action listeners)
	 * when the user modifies the editor value.
	 * </p>
	 */
	protected void notifyParameterChanged() {
		T newValue = getEditorValue();
		if (param.setValue(newValue)) {
			getEditorValueHook().updateListeners(newValue);
		} else {
			validateFailed();
		}
	}

	/**
	 * Called when parameter validation fails. Default implementation reverts
	 * the editor to the parameter's current value. Subclasses may override
	 * to customize behavior (e.g., use SwingUtilities.invokeLater).
	 */
	protected void validateFailed() {
		setFromParameter();
	}

	protected abstract void setEnabled(boolean enabled);

}
