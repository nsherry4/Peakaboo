package org.peakaboo.filter.model;

import org.peakaboo.dataset.DataSet;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

@Deprecated(forRemoval = true, since = "5.4")
public abstract class AbstractSimpleFilter extends AbstractFilter
{

	/**
	 * Filter the given {@link Spectrum} and return the modified result
	 * @param data the Spectrum to filter
	 */
	protected abstract ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, DataSet dataset);
	
}
