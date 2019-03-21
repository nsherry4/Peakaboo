package net.sciencestudio.bolt.scripting.languages;

public class PythonLanguage extends Language{

	@Override
	public String getName() {
		return "python";
	}

	@Override
	public boolean isCompilable() {
		return true;
	}
	
}
