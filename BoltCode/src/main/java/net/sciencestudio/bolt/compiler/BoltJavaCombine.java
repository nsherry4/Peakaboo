package net.sciencestudio.bolt.compiler;

import java.util.function.BiFunction;


public class BoltJavaCombine<T1, T2> extends BoltJavaFunction implements BiFunction<T1, T1, T2>{

	private BiFunction<T1, T1, T2> innerFn;

	private String value1, value2;
	private Class<?> t1, t2;
		

	public BoltJavaCombine(String value1, String value2, Class<?> t1, Class<?> t2) {
			
		this.t1 = t1;
		this.t2 = t2;
		
		this.value1 = value1;
		this.value2 = value2;

	}
	

	
	protected String getSourceCode()
	{
		return generateSourceCode(
				"BiFunction", "apply",
				t1.getSimpleName() + ", " + t1.getSimpleName() + ", " + t2.getSimpleName(), 
				t2.getSimpleName(), 
				t1.getSimpleName() + " " + value1 + ", " + t1.getSimpleName() + " " + value2
			);
	}
	
	@SuppressWarnings("unchecked")
	private void compile()
	{
		innerFn = (BiFunction<T1, T1, T2>)getFunctionObject();	
	}
	
	
	
	
	@Override
	public T2 apply(T1 v1, T1 v2) {
		if (innerFn == null) compile();
		return innerFn.apply(v1, v2);
	}



	@Override
	protected void sourceCodeChanged() {
		innerFn = null;
	}
	
}