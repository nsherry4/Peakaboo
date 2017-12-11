package peakaboo.filter.plugins.programming;


import autodialog.model.Parameter;
import bolt.scripting.BoltMap;
import bolt.scripting.BoltScriptExecutionException;
import bolt.scripting.languages.Language;
import bolt.scripting.languages.PythonLanguage;
import de.sciss.syntaxpane.lexers.PythonLexer;
import de.sciss.syntaxpane.syntaxkits.PythonSyntaxKit;
import peakaboo.filter.editors.CodeEditor;
import peakaboo.filter.model.AbstractSimpleFilter;
import scitypes.ISpectrum;
import scitypes.Spectrum;


public class JPython extends AbstractSimpleFilter {

	Parameter<String> code;
	private CodeEditor editor;	
	private boolean pythonSupported = true;
	
	private static final String header = "" + 
	"from java.util import * \n" +
	"import array\n" +
	"\n" +
	"# The input spectrum is located in a variable named spectrumIn\n" +
	"# Place the result of the filter in a variable named spectrumOut\n" +
	"# spectrumIn and spectrumOut are of Java type float[]. \n" +
	"\n\n";
	
	
	private BoltMap<float[], float[]> boltmap;
	
	public JPython() {
		super();
		
	
		try 
		{
			Language language = new PythonLanguage();
			language.setClassLoader(this.getClass().getClassLoader());
			boltmap = new BoltMap<float[], float[]>(language, "spectrumIn", "spectrumOut", "");
			boltmap.setMultithreaded(true);	
		}
		catch (Throwable t)
		{
			t.printStackTrace(System.out);
			pythonSupported = false;
		}
		
		
	}
	
	@Override
	public void initialize() 
	{
		editor = new CodeEditor("python", new PythonSyntaxKit());
		code = new Parameter<>("JPython Code", editor, header + "spectrumOut = spectrumIn");
		addParameter(code);
	}

	@Override
	public String getFilterName() {
		return "JPython Code";
	}

	@Override
	public String getFilterDescription() {
		return "Allows you to create your own filter in the Python programming language using JPython";
	}

	@Override
	public FilterType getFilterType() {
		return FilterType.PROGRAMMING;
	}

	@Override
	public boolean validateParameters() {
		try {
			boltmap.setScript(getCode());
			if (  boltmap.apply(new float[]{1, 2, 3, 4}) instanceof float[]  ){
				return true;
			} else {
				throw new BoltScriptExecutionException("Type mismatch for spectrumOut");
			}
		} catch (Exception e) {
			editor.errorMessage = e.getMessage();
			return false;
		}
		
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data) {
		
		boltmap.setScript(getCode());
		
		float[] source = data.backingArray();
		float[] result = boltmap.apply(source);
				
		return new ISpectrum(result);	
		
	}

	private String getCode()
	{
		return code.getValue();
	}
	
	@Override
	public boolean pluginEnabled() {
		return pythonSupported;
	}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

}
