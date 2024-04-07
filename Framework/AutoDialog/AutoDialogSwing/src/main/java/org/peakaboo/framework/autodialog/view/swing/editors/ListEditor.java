package org.peakaboo.framework.autodialog.view.swing.editors;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;


public class ListEditor<T> extends AbstractSwingEditor<T> {

	private JComboBox<T> control;
	

	public ListEditor() {
		control = new JComboBox<>();
	}
	
	
	@Override
	public void initialize(Parameter<T> p) {
		this.param = p;
		SelectionParameter<T> selparam = (SelectionParameter<T>) p;

		control.setModel(new DefaultComboBoxModel(selparam.getPossibleValues().toArray()));
		control.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
		
		
		control.addActionListener(e -> {
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
	public void setEditorValue(T value) {
		control.setSelectedItem(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getEditorValue() {
		return (T)control.getSelectedItem();
	}
	

	public void validateFailed() {
		setFromParameter();
	}
	
	
	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}
	
}
