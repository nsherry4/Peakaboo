package org.peakaboo.framework.autodialog.view.swing.layouts;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.editors.Editor.LabelStyle;

public abstract class AbstractSwingLayout implements SwingLayout {

	protected Group group;
	protected JPanel root = new JPanel();
	

	public void initialize(Group group) {
		this.group = group;
		layout();
	}
	

	
	@Override
	public String getTitle() {
		return group.getName();
	}
	
	@Override
	public Group getValue() {
		return group;
	}

	@Override
	public JComponent getComponent() {
		return root;
	}
	
	@Override
	public LabelStyle getLabelStyle() {
		return LabelStyle.LABEL_HIDDEN;
	}
	
	
	@Override
	public boolean expandVertical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean expandHorizontal() {
		// TODO Auto-generated method stub
		return false;
	}


	
}
