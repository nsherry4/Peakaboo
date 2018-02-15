package bolt.compiler;

import java.util.function.Predicate;

public class BoltJavaCondition<T1> extends BoltJavaFunction implements Predicate<T1>
{


	private Predicate<T1> innerFn;

	private String value;
	private Class<?> t1;
		

	public BoltJavaCondition(String value, Class<? extends T1> t1) {
			
		this.t1 = t1;
		
		this.value = value;

	}
	

	
	protected String getSourceCode()
	{
		return generateSourceCode(
				"Predicate", "test",
				t1.getSimpleName(), 
				"Boolean", 
				t1.getSimpleName() + " " + value
			);
	}
	
	@SuppressWarnings("unchecked")
	private void compile()
	{
		innerFn = (Predicate<T1>)getFunctionObject();
	}
	
	
	
	
	@Override
	public boolean test(T1 v) {
		if (innerFn == null) compile();
		return innerFn.test(v);
	}



	@Override
	protected void sourceCodeChanged() {
		innerFn = null;
	}

}
