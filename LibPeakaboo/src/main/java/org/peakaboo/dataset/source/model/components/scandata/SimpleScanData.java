package org.peakaboo.dataset.source.model.components.scandata;

import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.tier.Tier;

public class SimpleScanData extends AbstractScanData {

	private Analysis analysis;
	
	public SimpleScanData(String name) {
		super(name);
		this.analysis = Tier.provider().createDataSourceAnalysis();
	}

	public void add(Spectrum spectrum) {
		analysis.process(spectrum);
		spectra.add(spectrum);
	}
	
	/**
	 * Convenience method for adding a {@link Spectrum}
	 * @param spectrum a float array to add
	 */
	public void add(float[] spectrum) {
		add(new ArraySpectrum(spectrum));
	}
	
	public void set(int index, Spectrum spectrum) {
		analysis.process(spectrum);
		spectra.set(index, spectrum);
	}
	
	/**
	 * Convenience method for setting a {@link Spectrum}
	 * @param index index to set at
	 * @param spectrum a float array to set as
	 */
	public void set(int index, float[] spectrum) {
		set(index, new ArraySpectrum(spectrum));
	}
	

	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

}
