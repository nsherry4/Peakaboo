package org.peakaboo.framework.bolt.compiler;

import java.util.function.Function;

public class BoltJavaMap<T1, T2> extends BoltJavaFunction implements Function<T1, T2>{

	private Function<T1, T2> innerFn;

	private String value;
	private Class<?> t1, t2;
		

	public BoltJavaMap(String value, Class<?> t1, Class<?> t2) {
			
		this.t1 = t1;
		this.t2 = t2;
		
		this.value = value;

	}
	

	
	protected String getSourceCode()
	{
		return generateSourceCode(
				"java.util.function.Function", "apply",
				t1.getSimpleName() + ", " + t2.getSimpleName(), 
				t2.getSimpleName(), 
				t1.getSimpleName() + " " + value
			);
	}
	
	@SuppressWarnings("unchecked")
	private void compile()
	{
		innerFn = (Function<T1, T2>)getFunctionObject();
	}
	
	
	
	
	@Override
	public T2 apply(T1 v) {
		if (innerFn == null) compile();
		return innerFn.apply(v);
	}



	@Override
	protected void sourceCodeChanged() {
		innerFn = null;
	}
	
}
