package org.peakaboo.framework.autodialog.view.swing.editors;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.peakaboo.framework.autodialog.model.Parameter;


public class IntegerEditor extends AbstractSwingEditor<Integer> {

	private JSpinner control;
	
	public IntegerEditor() {
		control = new JSpinner();
	}
	
	@Override
	public void initialize(Parameter<Integer> param) {	
		this.param = param;
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
		
		control.getEditor().setPreferredSize(new Dimension(70, control.getEditor().getPreferredSize().height));
		control.setValue(param.getValue());

		control.addChangeListener(e -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!param.setValue(getEditorValue())) {
				validateFailed();
			}
		});
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
		return LabelStyle.LABEL_ON_SIDE;
	}

	@Override
	public JComponent getComponent() {
		return control;
	}

	@Override
	public void setEditorValue(Integer value) {
		control.setValue(value);
	}

	@Override
	public Integer getEditorValue() {
		return (Integer)control.getValue();
	}
	

	public void validateFailed() {
		setFromParameter();
	}
	
	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}
	
}
