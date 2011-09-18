package peakaboo.filter.filters.programming;


import bolt.plugin.Plugin;
import bolt.scripting.BoltMap;
import bolt.scripting.languages.Language;
import peakaboo.common.Version;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

@Plugin
public class JPython extends AbstractFilter {

	private static int CODE = 0;
	
	private static final String header = "" + 
	"from java.util import * \n" +
	"from scitypes import Spectrum \n" +
	"from peakaboo.calculations import * \n" +
	"from JSci.maths import Complex \n" +
	"import array\n" +
	"\n" +
	"# spectrumIn and spectrumOut are of Java type float[]. \n" +
	"# \n" +
	"# Calling functions in Peakaboo (eg. peakaboo.calculations) \n" +
	"# will usually require wrapping the float[] in a Spectrum class. \n" +
	"# For Example: \n" +
	"# complexArray = Noise.DataToFFT(Spectrum(spectrumIn)) \n" +
	"\n\n";
	
	
	private BoltMap<float[], float[]> boltmap;
	
	public JPython() {
		super();
		
		boltmap = new BoltMap<float[], float[]>(Language.python(), "spectrumIn", "spectrumOut", "");
		boltmap.setMultithreaded(true);
		
	}
	
	@Override
	public void initialize() {
		addParameter(CODE, new Parameter(ValueType.CODE, "Custom Code", header + "spectrumOut = spectrumIn"));
		
		getParameter(CODE).setProperty("Language", "python");
	}

	@Override
	public String getPluginName() {
		return "JPython Code";
	}

	@Override
	public String getPluginDescription() {
		return "";
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
			boltmap.setScript(getCode());
			if (  boltmap.f(new float[]{1, 2, 3, 4}) instanceof float[]  ){
				return true;
			} else {
				throw new Exception("Type mismatch for spectrumOut");
			}
		} catch (Exception e) {
			getParameter(CODE).setProperty("ErrorMessage", e.getMessage());
			return false;
		}
		
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache) {
		
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
		return !Version.release;
	}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

}
