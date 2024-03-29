package org.peakaboo.framework.autodialog.model.classinfo;

public class BooleanClassInfo extends SimpleClassInfo<Boolean> {

	public BooleanClassInfo() {
		super(Boolean.class, Object::toString, Boolean::parseBoolean);
	}
	
}
