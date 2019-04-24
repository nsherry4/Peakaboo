package org.peakaboo.framework.autodialog.view.javafx.editors;

import java.math.BigDecimal;
import java.text.NumberFormat;

import javafx.scene.Node;
import jfxtras.labs.scene.control.BigDecimalField;
import org.peakaboo.framework.autodialog.model.Parameter;

public class IntegerEditor extends AbstractEditor<Integer> {

	BigDecimalField node;
	
	public IntegerEditor(){}
	
	@Override
	public void init(Parameter<Integer> value) {
		
		double interval = 1;
		
//		if (value instanceof BoundedValue) {
//			BoundedValue<Integer> boundedValue = (BoundedValue<Integer>) value;
//			interval = boundedValue.getInterval();
//		}
		
		node = new BigDecimalField(new BigDecimal(value.getValue()));
		node.setFormat(NumberFormat.getNumberInstance());
		node.setStepwidth(new BigDecimal(interval));
				
		node.setMaxWidth(100);
		
		node.numberProperty().addListener(change -> {
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
	public Integer getEditorValue() {
		return node.getNumber().intValue();
	}

	@Override
	public void setEditorValue(Integer value) {
		node.setNumber(new BigDecimal(value));
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

	public void validateFailed() {
		setFromParameter();
	}

}
