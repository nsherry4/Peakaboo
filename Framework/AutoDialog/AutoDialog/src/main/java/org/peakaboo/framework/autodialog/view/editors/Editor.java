package org.peakaboo.framework.autodialog.view.editors;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.view.View;
import org.peakaboo.framework.eventful.EventfulType;

public interface Editor<T> extends View
{
	
		
	public enum LabelStyle {
		LABEL_ON_TOP,
		LABEL_ON_SIDE,
		LABEL_HIDDEN
	}
	

	void initialize(Parameter<T> param);


	
	/**
	 * Restores the graphical interface component to the value of the Parameter it is derived
	 * from. Only sets the value if it differes from the current value in the editor. This is
	 * useful for callback wiring.
	 */
	void setFromParameter();
	
	
	/**
	 * Returns the current value of the graphical interface component.
	 * @return
	 */
	T getEditorValue();
	
	/**
	 * Sets the value of the editor
	 * @param value the value to set
	 */
	void setEditorValue(T value);
	
	/**
	 * Returns a hook which can be used to listen for changes to the editor's value
	 * @return
	 */
	EventfulType<T> getEditorValueHook();
	

	
	
	
}
