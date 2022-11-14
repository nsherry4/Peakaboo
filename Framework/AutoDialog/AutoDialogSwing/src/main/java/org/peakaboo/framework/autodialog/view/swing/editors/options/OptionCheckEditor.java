package org.peakaboo.framework.autodialog.view.swing.editors.options;

import javax.swing.JComponent;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.view.swing.editors.AbstractSwingEditor;
import org.peakaboo.framework.swidget.widgets.options.OptionCheckBox;

public class OptionCheckEditor extends AbstractSwingEditor<Boolean>{
	
	private OptionCheckBox control;

	public OptionCheckEditor() {
		control = new OptionCheckBox();
	}
	
	@Override
	public void initialize(Parameter<Boolean> param) {
		
		this.param = param;
		
		setFromParameter();
		param.getValueHook().addListener(v -> setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
			
		control.withListener(e -> {
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
	public JComponent getComponent() {
		return control;
	}

	@Override
	public boolean expandHorizontal() {
		return true;
	}

	@Override
	public LabelStyle getLabelStyle() {
		return LabelStyle.LABEL_ON_SIDE;
	}


	
	@Override
	public void setEditorValue(Boolean value) {
		control.setSelected(value);
	}

	@Override
	public Boolean getEditorValue() {
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
