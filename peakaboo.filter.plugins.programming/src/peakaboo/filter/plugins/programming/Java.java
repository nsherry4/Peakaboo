package peakaboo.filter.plugins.programming;


import autodialog.model.Parameter;
import bolt.compiler.BoltJavaMap;
import de.sciss.syntaxpane.lexers.JavaLexer;
import de.sciss.syntaxpane.syntaxkits.JavaSyntaxKit;
import peakaboo.filter.editors.CodeEditor;
import peakaboo.filter.model.AbstractSimpleFilter;
import scitypes.Spectrum;


public class Java extends AbstractSimpleFilter {

	BoltJavaMap<float[], float[]> boltJavaMap;
	
	private Parameter<String> code;
	private CodeEditor editor;
	
	private static final String defaultBoltFunction = "" + 
		"return JavaFilter.filter(spectrumIn);";
	
	private static final String defaultCode = "" + 
	"class JavaFilter { \n" +
	"	public static float[] filter(float[] spectrum){ \n" +
	"		return spectrum; \n" +
	"	} \n" +
	"} \n";

	
	public Java() {
		boltJavaMap = new BoltJavaMap<float[], float[]>("spectrumIn", float[].class, float[].class);
	}
	
	@Override
	public void initialize() {
		
		editor = new CodeEditor("java", new JavaSyntaxKit());
		code = new Parameter<>("Java Code", editor, defaultCode);
		addParameter(code);
	}
	
	@Override
	public FilterType getFilterType() {
		return FilterType.PROGRAMMING;
	}


	@Override
	public boolean validateParameters() {
		
		try {
						
			boltJavaMap.setFunctionText(defaultBoltFunction);
			boltJavaMap.setOtherText("");
			boltJavaMap.setIncludeText(code.getValue());
			
			boltJavaMap.apply(new float[]{1, 2, 3, 4});
			return true;
		} catch (Exception e) {
			editor.errorMessage = e.getMessage();
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data) {
		
		//in this plugin, validate also puts the user code into the mapper 
		validateParameters();
		
		return new Spectrum(boltJavaMap.apply(data.backingArray()));
	}

	@Override
	public boolean canFilterSubset() {
		return true;

	}

	@Override
	public String getFilterName() {
		return "Java Code";

	}

	@Override
	public String getFilterDescription() {
		return "Allows you to create your own filter in the Java programming language";
	}

	@Override
	public boolean pluginEnabled() {
		return true;

	}
	
}
