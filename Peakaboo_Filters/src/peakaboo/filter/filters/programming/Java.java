package peakaboo.filter.filters.programming;

import javax.swing.JOptionPane;

import bolt.compiler.BoltJavaMap;
import peakaboo.common.Version;

import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

public class Java extends AbstractFilter{

	BoltJavaMap<float[], float[]> boltJavaMap;
	
	private static final int INCLUDES = 0;
	private static final int FUNCTION = 1;
	private static final int OTHERCODE = 2;
	
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
		addParameter(OTHERCODE, new Parameter(ValueType.CODE, "Other Code", defaultOther));
		addParameter(FUNCTION, new Parameter(ValueType.CODE, "Filter Function: float[] transform(float[] spectrumIn)", defaultFunction));
		addParameter(INCLUDES, new Parameter(ValueType.CODE, "Imports", defaultIncludes));
		
		
		getParameter(INCLUDES).setProperty("CodeHeight", "75");
		getParameter(FUNCTION).setProperty("CodeHeight", "75");
		getParameter(OTHERCODE).setProperty("CodeHeight", "250");
		
		getParameter(INCLUDES).setProperty("Language", "java");
		getParameter(FUNCTION).setProperty("Language", "java");
		getParameter(OTHERCODE).setProperty("Language", "java");
	}
	
	@Override
	public FilterType getFilterType() {
		return FilterType.PROGRAMMING;
	}

	@Override
	public PlotPainter getPainter() {
		return null;
	}

	@Override
	public boolean validateParameters() {
		
		try {
			boltJavaMap.setIncludeText(getParameter(INCLUDES).codeValue());
			boltJavaMap.setFunctionText(getParameter(FUNCTION).codeValue());
			boltJavaMap.setOtherText(getParameter(OTHERCODE).codeValue());
			boltJavaMap.f(new float[]{1, 2, 3});
			return true;
		} catch (Exception e) {
			getParameter(FUNCTION).setProperty("ErrorMessage", e.getMessage());
			getParameter(INCLUDES).setProperty("ErrorMessage", e.getMessage());
			getParameter(OTHERCODE).setProperty("ErrorMessage", e.getMessage());
			return false;
		}
		
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache) {
		return new Spectrum(boltJavaMap.f(data.backingArray()));
	}

	@Override
	public boolean canFilterSubset() {
		return true;

	}

	@Override
	public String getPluginName() {
		return "Java Code";

	}

	@Override
	public String getPluginDescription() {
		return "";
	}

	@Override
	public boolean pluginEnabled() {
		return !Version.release;

	}
	
	
}
