package org.peakaboo.framework.autodialog.model.classinfo;

public class StringClassInfo extends SimpleClassInfo<String> {

	public StringClassInfo() {
		super(String.class, v -> v, s -> s);
	}
	
}
