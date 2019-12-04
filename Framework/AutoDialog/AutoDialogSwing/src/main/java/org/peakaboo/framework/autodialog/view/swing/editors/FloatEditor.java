package org.peakaboo.framework.autodialog.view.swing.editors;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.peakaboo.framework.autodialog.model.Parameter;


public class FloatEditor extends AbstractSwingEditor<Float> {

	private JSpinner control;
	
	public FloatEditor() {
		control = new JSpinner();
	}
	
	@Override
	public void initialize(Parameter<Float> param) {
		this.param = param;
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
		
		
		control.setModel(new SpinnerNumberModel((Float)param.getValue(), null, null, 0.1f));
		control.getEditor().setPreferredSize(new Dimension(70, control.getEditor().getPreferredSize().height));
		control.setValue((Float)param.getValue());
		
		
		control.addChangeListener(e -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!param.setValue(getEditorValue())) {
				validateFailed();
			}
		});

	}
	
	@Override
	public boolean expandVertical()
	{
		return false;
	}

	@Override
	public boolean expandHorizontal()
	{
		return false;
	}

	@Override
	public LabelStyle getLabelStyle()
	{
		return LabelStyle.LABEL_ON_SIDE;
	}

	@Override
	public JComponent getComponent()
	{
		return control;
	}

	@Override
	public void setEditorValue(Float value)
	{
		control.setValue(value);
	}

	@Override
	public Float getEditorValue()
	{
		return ((Number)control.getValue()).floatValue();
	}

	public void validateFailed() {
		setFromParameter();
	}

	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}

}
