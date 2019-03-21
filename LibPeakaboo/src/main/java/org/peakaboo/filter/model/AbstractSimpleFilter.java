package org.peakaboo.filter.model;

import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;

public abstract class AbstractSimpleFilter extends AbstractFilter
{

	/**
	 * Filter the given {@link Spectrum} and return the modified result
	 * @param data the Spectrum to filter
	 */
	protected abstract ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data);
	
}
