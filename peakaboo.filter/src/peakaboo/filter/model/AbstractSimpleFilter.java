package peakaboo.filter.model;

import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

public abstract class AbstractSimpleFilter extends AbstractFilter
{

	@Override
	public PlotPainter getPainter()
	{
		return null;
	}


	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, boolean cache)
	{
		return filterApplyTo(data);
	}

	/**
	 * Filter the given {@link Spectrum} and return the modified result
	 * @param data the Spectrum to filter
	 */
	protected abstract ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data);
	
}
