package net.sciencestudio.autodialog.model.classinfo;


public class FloatClassInfo extends SimpleClassInfo<Float> {

	public FloatClassInfo() {
		super(Float.class, f -> f.toString(), Float::parseFloat);
	}

}
