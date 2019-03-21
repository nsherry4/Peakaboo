package net.sciencestudio.bolt.scripting.functions;

import java.util.function.BiFunction;

import net.sciencestudio.bolt.scripting.BoltScriptExecutionException;
import net.sciencestudio.bolt.scripting.BoltScripter;
import net.sciencestudio.bolt.scripting.languages.Language;


public class BoltCombine<T1, T2> extends BoltScripter implements BiFunction<T1, T1, T2> {

	private String input1, input2, output;

	
	
	public BoltCombine(Language language, String input1, String input2, String output, String script) {
		super(language, script);
		
		this.input1 = input1;
		this.input2 = input2;
		this.output = output;
		
	}
	

	
	
	@Override
	public T2 apply(T1 v1, T1 v2) {
		
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
	private T2 do_f(T1 v1, T1 v2) {

		if (!hasSideEffects) clear();
		set(input1, v1);
		set(input2, v2);
				
		try {
			run();
			return (T2)get(output);
			
		} catch (Exception e) {
			throw new BoltScriptExecutionException("Error executing script\n\n" + e.getMessage() + "\n-----\n" + getStdErr(), e);
		}
				
	}

}
