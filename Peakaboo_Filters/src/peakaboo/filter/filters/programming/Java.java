package peakaboo.filter.filters.programming;


import bolt.compiler.BoltJavaMap;
import bolt.plugin.Plugin;
import peakaboo.common.Version;

import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

@Plugin
public class Java extends AbstractSimpleFilter {

	BoltJavaMap<float[], float[]> boltJavaMap;
	
	private final int INCLUDES 	= getNextParameterIndex();
	private final int FUNCTION 	= getNextParameterIndex();
	private final int OTHERCODE = getNextParameterIndex();
	
	private static final String defaultIncludes = "" + 
		"import java.util.*;\n" +
		"import peakaboo.calculations.*;\n" +
		"import JSci.maths.Complex;\n" +
		"import scitypes.*;";
	
	private static final String defaultFunction = "" + 
		"//Identity Function \n" +
		"return spectrumIn;";
	
	private static final String defaultOther = "" + 
	"class Helper{ \n" +
	"	public int help(){ \n" +
	"		return 0; \n" +
	"	} \n" +
	"} \n" +
	"\n" +
	"int help(){ \n" +
	"	return 0; \n" +
	"}";
	
	public Java() {
		boltJavaMap = new BoltJavaMap<float[], float[]>("spectrumIn", float[].class, float[].class);
	}
	
	@Override
	public void initialize() {
		
		addParameter(INCLUDES, new Parameter(ValueType.CODE, "Imports", defaultIncludes));
		addParameter(FUNCTION, new Parameter(ValueType.CODE, "Filter Function: float[] transform(float[] spectrumIn)", defaultFunction));
		addParameter(OTHERCODE, new Parameter(ValueType.CODE, "Other Code", defaultOther));
		
		getParameter(INCLUDES).setProperty("EditorVWeight", "0.4");
		getParameter(FUNCTION).setProperty("EditorVWeight", "1.0");
		getParameter(OTHERCODE).setProperty("EditorVWeight", "0.8");
		
		getParameter(INCLUDES).setProperty("Language", "java");
		getParameter(FUNCTION).setProperty("Language", "java");
		getParameter(OTHERCODE).setProperty("Language", "java");
	}
	
	@Override
	public FilterType getFilterType() {
		return FilterType.PROGRAMMING;
	}


	@Override
	public boolean validateParameters() {
		
		try {
			boltJavaMap.setIncludeText(getParameter(INCLUDES).codeValue());
			boltJavaMap.setFunctionText(getParameter(FUNCTION).codeValue());
			boltJavaMap.setOtherText(getParameter(OTHERCODE).codeValue());
			boltJavaMap.f(new float[]{1, 2, 3, 4});
			return true;
		} catch (Exception e) {
			getParameter(FUNCTION).setProperty("ErrorMessage", e.getMessage());
			getParameter(INCLUDES).setProperty("ErrorMessage", e.getMessage());
			getParameter(OTHERCODE).setProperty("ErrorMessage", e.getMessage());
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
		return "";
	}

	@Override
	public boolean pluginEnabled() {
		return !Version.release;

	}

	@Override
	public boolean showSaveLoad()
	{
		return true;
	}
	
	
}
