package org.peakaboo.framework.bolt.scripting.functions;

import java.util.function.Supplier;

import org.peakaboo.framework.bolt.scripting.BoltScriptExecutionException;
import org.peakaboo.framework.bolt.scripting.BoltScripter;
import org.peakaboo.framework.bolt.scripting.languages.Language;

public class BoltGet<T1> extends BoltScripter implements Supplier<T1>{

	private String get;
	
	
	public BoltGet(Language language, String get, String script) {
		
		super(language, script);
		
		this.get = get;
	
	}
	

	
	@Override
	public T1 get() {
		
		if (hasSideEffects || !multithreaded) {
			synchronized(this)
			{
				return do_f();
			}
		} else {
			return do_f();
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private T1 do_f() {
		
		if (!hasSideEffects) clear();
		
		try {
			run();
			return (T1) get(get);
			
		} catch (Exception e) {
			throw new BoltScriptExecutionException("Error executing script\n\n" + e.getMessage() + "\n-----\n" + getStdErr(), e);
		}
		
	}

}
