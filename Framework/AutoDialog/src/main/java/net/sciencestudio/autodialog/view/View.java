package net.sciencestudio.autodialog.view;

import net.sciencestudio.autodialog.model.Value;
import net.sciencestudio.autodialog.view.editors.Editor.LabelStyle;

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
