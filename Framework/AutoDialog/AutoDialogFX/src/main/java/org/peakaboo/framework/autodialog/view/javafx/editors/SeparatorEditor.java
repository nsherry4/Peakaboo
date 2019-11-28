package org.peakaboo.framework.autodialog.view.javafx.editors;

import org.peakaboo.framework.autodialog.model.Parameter;

import javafx.scene.Node;
import javafx.scene.control.Separator;

public class SeparatorEditor extends AbstractEditor<Object>{

	private Separator node;
	
	
	
	public SeparatorEditor() {
		node = new Separator();
	}
	
	@Override
	public Double getEditorValue() {
		return null;
	}

	@Override
	public Node getComponent() {
		return node;
	}

	@Override
	public void setEditorValue(Object value) {
		
	}

	@Override
	public void init(Parameter<Object> value) {
		
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
		return LabelStyle.LABEL_HIDDEN;
	}


}
