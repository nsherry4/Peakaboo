package net.sciencestudio.bolt.compiler;

import java.util.function.BiFunction;

public class BoltJavaFold<T1, T2> extends BoltJavaFunction implements BiFunction<T1, T2, T2>{


	private BiFunction<T1, T2, T2> innerFn;

	private String base, value;
	private Class<?> t1, t2;
	
	
	public BoltJavaFold(String base, String value, Class<?> t1, Class<?> t2) {
		
		this.t1 = t1;
		this.t2 = t2;
		
		this.base = base;
		this.value = value;

	}
	

	
	protected String getSourceCode()
	{
		return generateSourceCode(
				"BiFunction", "apply",
				t1.getSimpleName() + ", " + t2.getSimpleName() + ", " + t2.getSimpleName(), 
				t2.getSimpleName(), 
				t1.getSimpleName() + " " + value + ", " + t2.getSimpleName() + " " + base
			);
	}
	
	@SuppressWarnings("unchecked")
	private void compile()
	{
		innerFn = (BiFunction<T1, T2, T2>)getFunctionObject();
	}
	
	
	@Override
	public T2 apply(T1 v, T2 b) {
		if (innerFn == null) compile();
		return innerFn.apply(v, b);
	}



	@Override
	protected void sourceCodeChanged() {
		innerFn = null;
	}

	
	
}