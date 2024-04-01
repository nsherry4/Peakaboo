package org.peakaboo.framework.autodialog.model.classinfo;

public class IntegerClassInfo extends SimpleClassInfo<Integer> {

	public IntegerClassInfo() {
		super(Integer.class, Object::toString, Integer::parseInt);
	}
	
}
