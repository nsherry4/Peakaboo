package org.peakaboo.framework.autodialog.view.swing.editors;

import java.awt.Dimension;

import javax.swing.JSlider;

import org.peakaboo.framework.autodialog.model.Parameter;

public class IntegerSliderEditor extends WrappingEditor<Integer, JSlider> {
	
	public IntegerSliderEditor() {
		this(new JSlider());
		component.setPreferredSize(new Dimension(150, 0));
	}
	
	public IntegerSliderEditor(JSlider component) {
		super(component);
	}

	
	@Override
	public void setEditorValue(Integer value) {
		getComponent().setValue(value);
	}
	
	@Override
	public void initialize(Parameter<Integer> param) {
		
		this.param = param;

		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
		
		getComponent().addChangeListener(e -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!param.setValue(getEditorValue())) {
				validateFailed();
			}
		});				
	}
	
	@Override
	public Integer getEditorValue() {
		return getComponent().getValue();
	}
	
	
	@Override
	protected void setEnabled(boolean enabled) {
		component.setEnabled(enabled);
	}
}
