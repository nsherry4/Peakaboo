package org.peakaboo.framework.bolt.compiler;

import java.util.function.Supplier;

public class BoltJavaGet<T1> extends BoltJavaFunction implements Supplier<T1>{

	private Supplier<T1> innerFn;

	private Class<?> t1;
		

	public BoltJavaGet(Class<?> t1) {
			
		this.t1 = t1;


	}
	

	
	protected String getSourceCode()
	{
		return generateSourceCode(
				"Supplier", "get",
				t1.getSimpleName(), 
				t1.getSimpleName(), 
				""
			);
	}
	
	@SuppressWarnings("unchecked")
	private void compile()
	{
		innerFn = (Supplier<T1>)getFunctionObject();
	}
	
	
	
	
	@Override
	public T1 get() {
		if (innerFn == null) compile();
		return innerFn.get();
	}



	@Override
	protected void sourceCodeChanged() {
		innerFn = null;
	}
	
}
