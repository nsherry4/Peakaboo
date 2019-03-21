package net.sciencestudio.autodialog.model.classinfo;

public class LongClassInfo extends SimpleClassInfo<Long> {

	public LongClassInfo() {
		super(Long.class, v -> v.toString(), Long::parseLong);
	}
	
}
