package org.peakaboo.framework.autodialog.view.swing.layouts;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.autodialog.view.editors.Editor.LabelStyle;
import org.peakaboo.framework.autodialog.view.swing.SwingView;
import org.peakaboo.framework.autodialog.view.swing.editors.SwingEditorFactory;

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
		return false;
	}

	@Override
	public boolean expandHorizontal() {
		return false;
	}

	protected SwingView fromValue(Value<?> value) {
		if (value instanceof Parameter<?> p) {
			return SwingEditorFactory.forParameter(p);
		} else if (value instanceof Group g) {
			return SwingLayoutFactory.forGroup(g);
		} else {
			return null;
		}
	}
	
}
