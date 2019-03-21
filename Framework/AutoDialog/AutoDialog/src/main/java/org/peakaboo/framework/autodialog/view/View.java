package org.peakaboo.framework.autodialog.view;

import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.autodialog.view.editors.Editor.LabelStyle;

public interface View {

	String getTitle();
	
	boolean expandVertical();
	boolean expandHorizontal();
	
	LabelStyle getLabelStyle();
	
	/**
	 * Returns the graphical interface component for this editor.
	 * @return
	 */
	Object getComponent();
	
	
	
	Value<?> getValue();
	
}
