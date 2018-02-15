package bolt.scripting.functions;

import java.util.function.BiFunction;

import bolt.scripting.BoltScriptExecutionException;
import bolt.scripting.BoltScripter;
import bolt.scripting.languages.Language;


public class BoltFold<T1, T2> extends BoltScripter implements BiFunction<T1, T2, T2>{

	private String base, value, result;


	
	public BoltFold(Language language, String base, String value, String result, String script) {
		
		super(language, script);
		
		this.base = base;
		this.value = value;
		this.result = result;
		
	}
	
	

	@Override
	public T2 apply(T1 v1, T2 v2) {
		
		if (hasSideEffects || !multithreaded) {
			synchronized(this)
			{
				return do_f(v1, v2);
			}
		} else {
			return do_f(v1, v2);
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private T2 do_f(T1 v, T2 b) {
		
		if (!hasSideEffects) clear();
		set(base, b);
		set(value, v);
				
		try {
			
			run();
			return (T2)get(result);
			
		} catch (Exception e) {
			throw new BoltScriptExecutionException("Error executing script\n\n" + e.getMessage() + "\n-----\n" + getStdErr(), e);
		}
				
	}

}
