package peakaboo.filter.filters.advanced;


import bolt.BoltMap;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

public class Custom extends AbstractFilter {

	private static int CODE = 0;
	
	
	private BoltMap<float[], float[]> boltmap;
	
	public Custom() {
		super();
		
		boltmap = new BoltMap<float[], float[]>("jython", "spectrumIn", "spectrumOut", "");
		boltmap.setMultithreaded(true);
		
	}
	
	@Override
	public void initialize() {
		addParameter(CODE, new Parameter(ValueType.CODE, "Custom Code", "spectrumOut = spectrumIn"));
	}

	@Override
	public String getFilterName() {
		return "Custom";
	}

	@Override
	public String getFilterDescription() {
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
			boltmap.setScript(getParameter(CODE).textValue());
			boltmap.f(new float[]{1, 2, 3, 4});
		} catch (Exception e) {
			getParameter(CODE).errorMessage = e.getMessage();
			return false;
		}
		return true;
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache) {
		
		boltmap.setScript(getParameter(CODE).textValue());
		
		float[] source = data.backingArray();
		float[] result = boltmap.f(source);
				
		return new Spectrum(result);	
		
	}

	@Override
	public boolean showFilter() {
		return true;
	}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

}
