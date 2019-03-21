package net.sciencestudio.autodialog.view.javafx.editors;

import javafx.scene.Node;
import javafx.scene.control.Separator;
import net.sciencestudio.autodialog.model.Parameter;

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
