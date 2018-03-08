package net.sciencestudio.bolt.scripting.functions;

import java.util.function.Function;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.scripting.BoltScriptExecutionException;
import net.sciencestudio.bolt.scripting.BoltScripter;
import net.sciencestudio.bolt.scripting.languages.Language;


public class BoltMap<T1, T2> extends BoltScripter implements Function<T1, T2>{

	private String inputName, outputName;
	
	
	
	public BoltMap(Language language, String inputName, String outputName, String script) {
		super(language, script);
		
		this.inputName = inputName;
		this.outputName = outputName;
		
	}
	



	@Override
	public T2 apply(T1 v) {
		
		if (hasSideEffects || !multithreaded) {
			synchronized(this)
			{
				return do_f(v);
			}
		} else {
			return do_f(v);
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private T2 do_f(T1 v)
	{
		if (!hasSideEffects) clear();
		set(inputName, v);
				
		try {
			
			run();
			
			return (T2)get(outputName);
			
		} catch (Exception e) {
			Bolt.logger().log(Level.WARNING, "Error executing script", e);
			throw new BoltScriptExecutionException("Error executing script\n\n" + e.getMessage() + "\n-----\n" + getStdErr(), e);
		}
	}

}
