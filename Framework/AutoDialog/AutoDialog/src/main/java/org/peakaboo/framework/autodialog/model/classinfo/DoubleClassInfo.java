package org.peakaboo.framework.autodialog.model.classinfo;

public class DoubleClassInfo extends SimpleClassInfo<Double> {

	public DoubleClassInfo() {
		super(Double.class, Object::toString, Double::parseDouble);
	}
	
}
