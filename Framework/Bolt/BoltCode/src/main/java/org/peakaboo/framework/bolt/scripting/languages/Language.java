package org.peakaboo.framework.bolt.scripting.languages;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public abstract class Language {

	//needed in cases where the language support files are loaded by a specific class-loader
	private ClassLoader classloader = Thread.currentThread().getContextClassLoader();
	
	public ClassLoader getClassLoader(){
		return classloader;
	}
	
	public void setClassLoader(ClassLoader classloader) {
		this.classloader = classloader;
	}
	
	
	public abstract String getName();
	public abstract boolean isCompilable();

	public ScriptEngine getEngine() {
		return new ScriptEngineManager(this.getClassLoader()).getEngineByName(this.getName());
	}
	
}
