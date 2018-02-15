package bolt.compiler;

import java.util.function.BiFunction;


public class BoltJavaMap2<T1, T2, T3> extends BoltJavaFunction implements BiFunction<T1, T2, T3>{

	private BiFunction<T1, T2, T3> innerFn;

	private String value1;
	private String value2;
	private Class<?> t1, t2, t3;
		

	public BoltJavaMap2(String value1, String value2, Class<?> t1, Class<?> t2, Class<?> t3) {
			
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		
		this.value1 = value1;
		this.value2 = value2;

	}
	

	
	protected String getSourceCode()
	{
		return generateSourceCode(
				"BiFunction", "apply",
				t1.getSimpleName() + ", " + t2.getSimpleName() + ", " + t3.getSimpleName(), 
				t3.getSimpleName(), 
				t1.getSimpleName() + " " + value1 + ", " + t2.getSimpleName() + " " + value2
			);
	}
	
	@SuppressWarnings("unchecked")
	private void compile()
	{
		innerFn = (BiFunction<T1, T2, T3>)getFunctionObject();
	}
	
	
	
	
	@Override
	public T3 apply(T1 v1, T2 v2) {
		if (innerFn == null) compile();
		return innerFn.apply(v1, v2);
	}



	@Override
	protected void sourceCodeChanged() {
		innerFn = null;
	}
	
}