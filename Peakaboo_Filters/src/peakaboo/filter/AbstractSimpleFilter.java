package peakaboo.filter;

import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

public abstract class AbstractSimpleFilter extends AbstractFilter
{

	@Override
	public PlotPainter getPainter()
	{
		return null;
	}


	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return filterApplyTo(data);
	}

	protected abstract Spectrum filterApplyTo(Spectrum data);
	
}
