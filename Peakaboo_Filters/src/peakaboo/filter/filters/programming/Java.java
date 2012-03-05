package peakaboo.filter.filters.programming;


import bolt.compiler.BoltJavaMap;
import bolt.plugin.Plugin;

import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

@Plugin
public class Java extends AbstractSimpleFilter {

	BoltJavaMap<float[], float[]> boltJavaMap;
	
	private final int CODE 		= getNextParameterIndex();
	
	
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
		
		addParameter(CODE, new Parameter(ValueType.CODE, "Java Code", defaultCode));
		getParameter(CODE).setProperty("EditorVWeight", "1.0");
		getParameter(CODE).setProperty("Language", "java");
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
			boltJavaMap.setIncludeText(getParameter(CODE).codeValue());
			
			boltJavaMap.f(new float[]{1, 2, 3, 4});
			return true;
		} catch (Exception e) {
			getParameter(CODE).setProperty("ErrorMessage", e.getMessage());
			return false;
		}
		
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data) {
		
		//in this plugin, validate also puts the user code into the mapper 
		validateParameters();
		
		return new Spectrum(boltJavaMap.f(data.backingArray()));
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
