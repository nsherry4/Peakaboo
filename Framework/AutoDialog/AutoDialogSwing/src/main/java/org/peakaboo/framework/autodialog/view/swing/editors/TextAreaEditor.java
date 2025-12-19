package org.peakaboo.framework.autodialog.view.swing.editors;


import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.peakaboo.framework.autodialog.model.Parameter;

public class TextAreaEditor extends WrappingEditor<String, JTextArea> {


	public TextAreaEditor() {
		this(new JTextArea());
		component.setRows(5);
		component.setColumns(20);
	}
	
	public TextAreaEditor(JTextArea textarea) {
		super(textarea, true, true, LabelStyle.LABEL_ON_TOP);
	}

	@Override
	public void initialize(Parameter<String> param) {
		this.param = param;

		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
		
		component.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}
			
			private void update() {
				notifyParameterChanged();
			}
		});
	}

	@Override
	public void setEditorValue(String value) {
		component.setText(value);
	}

	@Override
	public String getEditorValue() {
		return component.getText();
	}
	
	@Override
	protected void setEnabled(boolean enabled) {
		component.setEnabled(enabled);
	}
	

}
