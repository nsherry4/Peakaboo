package net.sciencestudio.autodialog.view.javafx.editors;

import java.math.BigDecimal;
import java.text.NumberFormat;

import javafx.scene.Node;
import jfxtras.labs.scene.control.BigDecimalField;
import net.sciencestudio.autodialog.model.Parameter;

public class RealEditor extends AbstractEditor<Float> {

	BigDecimalField node;
	
	public RealEditor(){}
	
	@Override
	public void init(Parameter<Float> value) {
		
		double interval = 1;
		
//		if (value instanceof BoundedValue) {
//			BoundedValue<Double> boundedValue = (BoundedValue<Double>) value;
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
	public Float getEditorValue() {
		return node.getNumber().floatValue();
	}

	@Override
	public void setEditorValue(Float value) {
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
