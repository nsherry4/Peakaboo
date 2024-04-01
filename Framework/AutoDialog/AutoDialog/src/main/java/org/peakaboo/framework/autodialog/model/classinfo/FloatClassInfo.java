package org.peakaboo.framework.autodialog.model.classinfo;


public class FloatClassInfo extends SimpleClassInfo<Float> {

	public FloatClassInfo() {
		super(Float.class, Object::toString, Float::parseFloat);
	}

}
