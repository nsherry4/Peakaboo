package org.peakaboo.framework.autodialog.view.swing.editors;

import javax.swing.JComponent;
import javax.swing.JSeparator;

import org.peakaboo.framework.autodialog.model.Parameter;

public class SeparatorEditor extends AbstractSwingEditor<Object> {

	private JComponent component;
	

	public SeparatorEditor() {
		this(new JSeparator());
	}
	
	public SeparatorEditor(JComponent component) {
		this.component = component;
	}
	
	@Override
	public void initialize(Parameter<Object> param)
	{
		this.param = param;
	}
	
	@Override
	public boolean expandVertical() {
		return false;
	}

	@Override
	public boolean expandHorizontal() {
		return false;
	}

	@Override
	public LabelStyle getLabelStyle() {
		return LabelStyle.LABEL_HIDDEN;
	}

	@Override
	public void setEditorValue(Object value) {
		//NOOP
	}

	@Override
	public Object getEditorValue() {
		return param.getValue();
	}

	@Override
	public JComponent getComponent() {
		return component;
	}
	
	@Override
	protected void setEnabled(boolean enabled) {
		//NOOP
	}
	
}
