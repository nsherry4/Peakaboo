package net.sciencestudio.bolt.scripting.languages;

public class RubyLanguage extends Language{

	public RubyLanguage() {
		System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
	}
	
	@Override
	public String getName() {
		return "ruby";
	}

	@Override
	//causes issues with variable bindings when compiled. Is this a bug in JRuby?
	public boolean isCompilable() {
		return false;
	}

}
