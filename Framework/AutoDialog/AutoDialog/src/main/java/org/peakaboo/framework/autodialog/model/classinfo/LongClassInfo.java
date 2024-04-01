package org.peakaboo.framework.autodialog.model.classinfo;

public class LongClassInfo extends SimpleClassInfo<Long> {

	public LongClassInfo() {
		super(Long.class, Object::toString, Long::parseLong);
	}
	
}
