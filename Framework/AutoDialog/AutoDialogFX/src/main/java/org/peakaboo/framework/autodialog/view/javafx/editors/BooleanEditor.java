package org.peakaboo.framework.autodialog.view.javafx.editors;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.peakaboo.framework.autodialog.model.Parameter;

public class BooleanEditor extends AbstractEditor<Boolean> {

	CheckBox node;
	
	public BooleanEditor() {}
	
	@Override
	public void init(Parameter<Boolean> value) {	
		node = new CheckBox("");

		node.selectedProperty().addListener(change -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!parameter.setValue(getEditorValue())) {
				validateFailed();
			}
		});
	}
	

	@Override
	public Boolean getEditorValue() {
		return node.isSelected();
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
	public void setEditorValue(Boolean value) {
		node.setSelected(value);
	}

	public void validateFailed() {
		setFromParameter();
	}

	@Override
	public Node getComponent() {
		return node;
	}


}
