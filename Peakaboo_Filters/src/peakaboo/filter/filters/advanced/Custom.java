package peakaboo.filter.filters.advanced;

import java.util.List;

import fava.functionable.FList;

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
		
		boltmap = new BoltMap<float[], float[]>("jruby", "", "spectrumIn", "spectrumOut");
		
	}
	
	@Override
	public void initialize() {
		addParameter(CODE, new Parameter(ValueType.CODE, "Custom Code", "$spectrumOut = $spectrumIn"));
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
		
		//can't have more than one thread futzing about with the script context
		synchronized (this) {
			boltmap.setScript(getParameter(CODE).textValue());
			return new Spectrum(boltmap.f(data.toArray()));	
		}
		
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
