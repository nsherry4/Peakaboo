package bolt.scripting.functions;

import java.util.function.BiFunction;

import bolt.scripting.BoltScriptExecutionException;
import bolt.scripting.BoltScripter;
import bolt.scripting.languages.Language;

public class BoltMap2<T1, T2, T3> extends BoltScripter implements BiFunction<T1, T2, T3>{

	private String input1, input2, output;

	
	
	public BoltMap2(Language language, String input1, String input2, String output, String script) {
		super(language, script);
		
		this.input1 = input1;
		this.input2 = input2;
		this.output = output;
		
	}
	

	
	@Override
	public T3 apply(T1 v1, T2 v2) {
		
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
	private T3 do_f(T1 v1, T2 v2) {
		
		if (!hasSideEffects) clear();
		set(input1, v1);
		set(input2, v2);
				
		try {
			
			run();
			
			return (T3)get(output);
			
		} catch (Exception e) {
			throw new BoltScriptExecutionException("Error executing script\n\n" + e.getMessage() + "\n-----\n" + getStdErr(), e);
		}
		
	}

}