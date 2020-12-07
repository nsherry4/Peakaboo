package org.peakaboo.framework.autodialog.view.swing.editors;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.peakaboo.framework.autodialog.model.Parameter;

public class LabelEditor extends AbstractSwingEditor<String> {

	private JLabel control;

	public LabelEditor() {
		control = new JLabel();
	}
	
	@Override
	public void initialize(Parameter<String> param) {
		this.param = param;
		
		setFromParameter();
		param.getValueHook().addListener(v -> setFromParameter());
	}

	@Override
	public String getEditorValue() {
		return control.getText();
	}

	@Override
	public void setEditorValue(String value) {
		control.setText(value);
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
	public JComponent getComponent() {
		return control;
	}

	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}

}
