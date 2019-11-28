package org.peakaboo.framework.autodialog.view.javafx.editors;

import org.peakaboo.framework.autodialog.model.Parameter;

import javafx.scene.Node;
import javafx.scene.control.TextField;

public class TextFieldEditor extends AbstractEditor<String> {

	TextField node;
	
	public TextFieldEditor(){}
	
	@Override
	public void init(Parameter<String> value) {
		
		node = new TextField(value.getValue());
		node.textProperty().addListener(change -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!parameter.setValue(getEditorValue())) {
				validateFailed();
			}
		});
	}

	@Override
	public Node getComponent() {
		return node;
	}

	@Override
	public String getEditorValue() {
		return node.getText();
	}

	@Override
	public void setEditorValue(String value) {
		node.setText(value);
	}

	@Override
	public boolean expandVertical() {
		return false;
	}

	@Override
	public boolean expandHorizontal() {
		return true;
	}

	@Override
	public LabelStyle getLabelStyle() {
		return LabelStyle.LABEL_ON_SIDE;
	}

	public void validateFailed() {
		setFromParameter();
	}


}
