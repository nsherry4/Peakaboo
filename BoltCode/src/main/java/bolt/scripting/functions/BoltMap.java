package bolt.scripting.functions;

import java.util.function.Function;

import bolt.scripting.BoltScriptExecutionException;
import bolt.scripting.BoltScripter;
import bolt.scripting.languages.Language;


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
			e.printStackTrace();
			throw new BoltScriptExecutionException("Error executing script\n\n" + e.getMessage() + "\n-----\n" + getStdErr(), e);
		}
	}

}
