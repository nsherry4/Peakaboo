package net.sciencestudio.autodialog.model.classinfo;

public class BooleanClassInfo extends SimpleClassInfo<Boolean> {

	public BooleanClassInfo() {
		super(Boolean.class, v -> v.toString(), Boolean::parseBoolean);
	}
	
}
