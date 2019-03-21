package net.sciencestudio.autodialog.model.classinfo;

public class ClassInfoDefaults {

	
	public static ClassInfo<?> guess(Class<?> cls) {
		
		if (Boolean.class.isAssignableFrom(cls)) {
			return new BooleanClassInfo();
		}
		
		if (Float.class.isAssignableFrom(cls)) {
			return new FloatClassInfo();
		}
		
		if (Double.class.isAssignableFrom(cls)) {
			return new DoubleClassInfo();
		}
		
		if (Integer.class.isAssignableFrom(cls)) {
			return new IntegerClassInfo();
		}
		
		if (Long.class.isAssignableFrom(cls)) {
			return new LongClassInfo();
		}
		
		if (String.class.isAssignableFrom(cls)) {
			return new StringClassInfo();
		}
		
		return null;
		
	}
	
	
	
}
