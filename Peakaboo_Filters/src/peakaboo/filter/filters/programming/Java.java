package peakaboo.filter.filters.programming;

import peakaboo.common.Version;
import peakaboo.filter.AbstractFilter;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

public class Java extends AbstractFilter{

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPluginName() {
		return "Java Code";
	}

	@Override
	public String getPluginDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean pluginEnabled() {
		// TODO Auto-generated method stub
		return !Version.release;
	}

}
