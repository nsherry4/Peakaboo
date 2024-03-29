package org.peakaboo.framework.autodialog.model.classinfo;

public class EnumClassInfo<T extends Enum<T>> extends SimpleClassInfo<T> {

	public EnumClassInfo(Class<T> cls) {
		super(cls, Enum::name, s -> Enum.valueOf(cls, s));
	}

}
