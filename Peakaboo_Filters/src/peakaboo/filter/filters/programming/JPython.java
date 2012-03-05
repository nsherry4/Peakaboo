package peakaboo.filter.filters.programming;


import bolt.plugin.Plugin;
import bolt.scripting.BoltMap;
import bolt.scripting.BoltScriptExecutionException;
import bolt.scripting.languages.Language;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

@Plugin
public class JPython extends AbstractSimpleFilter {

	private final int CODE = getNextParameterIndex();
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
			boltmap = new BoltMap<float[], float[]>(Language.python(), "spectrumIn", "spectrumOut", "");
			boltmap.setMultithreaded(true);	
		}
		catch (Throwable t)
		{
			pythonSupported = false;
		}
		
	}
	
	@Override
	public void initialize() 
	{
		addParameter(CODE, new Parameter(ValueType.CODE, "JPython Code", header + "spectrumOut = spectrumIn"));
		getParameter(CODE).setProperty("Language", "python");
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
			if (  boltmap.f(new float[]{1, 2, 3, 4}) instanceof float[]  ){
				return true;
			} else {
				throw new BoltScriptExecutionException("Type mismatch for spectrumOut");
			}
		} catch (Exception e) {
			getParameter(CODE).setProperty("ErrorMessage", e.getMessage());
			return false;
		}
		
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data) {
		
		boltmap.setScript(getCode());
		
		float[] source = data.backingArray();
		float[] result = boltmap.f(source);
				
		return new Spectrum(result);	
		
	}

	private String getCode()
	{
		return getParameter(CODE).codeValue();
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
