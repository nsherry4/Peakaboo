package bolt;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import bolt.compiler.BoltJavaMap;
import bolt.scripting.BoltScripter;
import bolt.scripting.functions.BoltMap;
import bolt.scripting.languages.JavascriptLanguage;
import bolt.scripting.languages.Language;
import bolt.scripting.languages.PythonLanguage;
import bolt.scripting.languages.RubyLanguage;


public class Test {

	private static final int rangeSize = 1000000;
	
	public static void main(String args[])
	{
		
		//listScriptingEngines();
		testing();
		compiling();
		script();
		
		//System.out.println(Integer.class.getSimpleName());
		
	}
	
	public static void testing() {
		Language js = new JavascriptLanguage();
		System.out.println(js.getEngine());
		
		Language python = new PythonLanguage();
		System.out.println(python.getEngine());
		
		Language ruby = new RubyLanguage();
		System.out.println(ruby.getEngine());
		
	}
	
	public static void compiling()
	{

		List<Integer> ints = new ArrayList<>();
		for (int i = 0; i < rangeSize; i++) {
			ints.add(i);
		}
		
		BoltJavaMap<Integer, Integer> inc = new BoltJavaMap<Integer, Integer>("i", Integer.class, Integer.class);
		inc.setFunctionText("return i+1;");
		
		try {
			inc.apply(1);
		} catch (Exception e) {

			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		
		testMap("Bolt Java", inc, ints);
		
		
	}
	
	public static void script()
	{
		
		List<Integer> ints = new ArrayList<>();
		for (int i = 0; i < rangeSize; i++) {
			ints.add(i);
		}

		Function<Integer, Integer> jinc = v -> v+1;
		
		testMap("Java", jinc, ints);

		
		testMap("Python", new BoltMap<Integer, Integer>(new PythonLanguage(), "i", "j", "j = i+1;"), ints);
		testMap("Javascript", new BoltMap<Integer, Integer>(new JavascriptLanguage(), "i", "j", "j = i+1"), ints);
		testMap("Ruby", new BoltMap<Integer, Integer>(new RubyLanguage(), "i", "j", "$j = $i+1"), ints);
		
	}
	
	public static void testMap(String language, Function<Integer, Integer> inc, Iterable<Integer> ints)
	{
		
		long startTime = System.currentTimeMillis();
		System.out.println("\n" + language + ":");
		
		for (Integer i : ints)
		{
			inc.apply(i);
		}

		System.out.println((System.currentTimeMillis() - startTime) + "ms");	
	}
	
	public static List<ScriptEngineFactory> getAvailableLanguages()
	{
		Set<ScriptEngineFactory> factories = new HashSet<ScriptEngineFactory>();
		
        //ScriptEngineManager mgr = new ScriptEngineManager();
        
        for (ScriptEngineFactory factory : new ScriptEngineManager().getEngineFactories()) {
        	
        	try {
        		System.out.print(factory.getEngineName() + ": ");        		
        		
        		new BoltScripter(BoltScripter.customLanguage(factory.getLanguageName(), false), "");
        		        		
        		System.out.println("PASS");
        		factories.add(factory);
        	} catch (NoClassDefFoundError e) {
        		System.out.println("x FAIL");
        	} catch (Exception e){
        		System.out.println("x FAIL");
        	}
        	
        	
        }
        
        return new ArrayList<>(factories);
	}
	
    public static void listScriptingEngines() {
    	
    	
    	
        for (ScriptEngineFactory factory : getAvailableLanguages()) {
            
        	System.out.println(factory.getLanguageName());
        	//System.out.printf("\tScript Engine: %s (%s)\n", factory.getEngineName(), factory.getEngineVersion());
            //System.out.printf("\tLanguage: %s (%s)\n", factory.getLanguageName(), factory.getLanguageVersion());
            //for (String name : factory.getNames()) {
            //    System.out.printf("\tEngine Alias: %s\n", name);
            //}
        }
    }
	
}
