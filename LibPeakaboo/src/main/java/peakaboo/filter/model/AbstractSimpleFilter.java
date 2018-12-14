package peakaboo.filter.model;

import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;

public abstract class AbstractSimpleFilter extends AbstractFilter
{

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
