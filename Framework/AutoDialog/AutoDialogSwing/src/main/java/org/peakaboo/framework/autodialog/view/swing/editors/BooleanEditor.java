package org.peakaboo.framework.autodialog.view.swing.editors;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.peakaboo.framework.autodialog.model.Parameter;


public class BooleanEditor extends AbstractSwingEditor<Boolean>
{
	
	private JCheckBox control;

	public BooleanEditor() {
		control = new JCheckBox();
	}
	
	@Override
	public void initialize(Parameter<Boolean> param) {
		
		this.param = param;
		
		setFromParameter();
		param.getValueHook().addListener(v -> setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
		
		control.setAlignmentX(Component.LEFT_ALIGNMENT);
		control.setOpaque(false);
		
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
	public JComponent getComponent()
	{
		return control;
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
	public void setEditorValue(Boolean value) {
		control.setSelected(value);
	}

	@Override
	public Boolean getEditorValue()
	{
		return control.isSelected();
	}
	

	
	public void validateFailed() {
		setFromParameter();
	}

	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}

	
}
