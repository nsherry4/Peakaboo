package peakaboo.filter.plugins;

import peakaboo.filter.model.AbstractFilter;
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

	/**
	 * Filter the given {@link Spectrum} and return the modified result
	 * @param data the Spectrum to filter
	 */
	protected abstract Spectrum filterApplyTo(Spectrum data);
	
}
