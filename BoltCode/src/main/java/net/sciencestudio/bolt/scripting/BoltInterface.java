package net.sciencestudio.bolt.scripting;

import java.util.logging.Level;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.scripting.languages.JavascriptLanguage;
import net.sciencestudio.bolt.scripting.languages.Language;

public class BoltInterface extends BoltScripter {

	ScriptEngine engine;
	
	public BoltInterface(Language language, String script) {
		super(language, script);
	}
	
	public void initialize() {
		try {
			engine = this.language.getEngine();
			engine.eval(this.script);
		} catch (Exception e) {
			throw new BoltScriptExecutionException("Error executing script\n\n" + e.getMessage() + "\n-----\n" + getStdErr(), e);
		}
	}
	
	public Object get(String variable) {
		return engine.getBindings(ScriptContext.ENGINE_SCOPE).get(variable);
	}
	
	public Object call(String function, Object... args) throws NoSuchMethodException, ScriptException {
		Invocable inv = (Invocable) engine;
		Object result = inv.invokeFunction(function, args);
		return result;
	}
	
	public static void main(String[] args) throws NoSuchMethodException, ScriptException {
		String script = "var fun1 = function(name) { return 'Hello ' + name; }";
		BoltInterface iface = new BoltInterface(new JavascriptLanguage(), script);
		iface.initialize();
		iface.call("fun1", "Me");
	}
	
}
