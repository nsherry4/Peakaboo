package net.sciencestudio.autodialog.model.classinfo;

public class EnumClassInfo<T extends Enum<T>> extends SimpleClassInfo<T> {

	public EnumClassInfo(Class<T> cls) {
		super(cls, e -> e.name(), s -> Enum.valueOf(cls, s));
	}

}
