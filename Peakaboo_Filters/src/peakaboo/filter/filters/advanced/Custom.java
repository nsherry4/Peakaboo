package peakaboo.filter.filters.advanced;


import bolt.scripting.BoltMap;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

public class Custom extends AbstractFilter {

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
	
	public Custom() {
		super();
		
		boltmap = new BoltMap<float[], float[]>("jython", "spectrumIn", "spectrumOut", "");
		boltmap.setMultithreaded(true);
		
	}
	
	@Override
	public void initialize() {
		addParameter(CODE, new Parameter(ValueType.CODE, "Custom Code", header + "spectrumOut = spectrumIn"));
	}

	@Override
	public String getPluginName() {
		return "Custom";
	}

	@Override
	public String getPluginDescription() {
		return "";
	}

	@Override
	public FilterType getFilterType() {
		return FilterType.ADVANCED;
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
			getParameter(CODE).errorMessage = e.getMessage();
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
		return true;
	}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

}
