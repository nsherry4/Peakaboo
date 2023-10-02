package org.peakaboo.dataset.source.model.components.scandata;

import org.peakaboo.app.PeakabooConfiguration;
import org.peakaboo.app.PeakabooConfiguration.MemorySize;
import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.dataset.source.model.components.scandata.loaderqueue.CompressedLoaderQueue;
import org.peakaboo.dataset.source.model.components.scandata.loaderqueue.LoaderQueue;
import org.peakaboo.dataset.source.model.components.scandata.loaderqueue.SimpleLoaderQueue;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.single.Compressed;

public class SimpleScanData extends AbstractScanData {

	private Analysis analysis;
	
	public SimpleScanData(String name) {
		super(name);
		this.analysis = new DataSourceAnalysis();
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
		add(new ISpectrum(spectrum));
	}
	
	/**
	 * Adds a previously compressed spectrum. Note that this will not trigger the {@link Analysis}, and will need to be done manually
	 * @param compressed
	 */
	public void add(Compressed<Spectrum> compressed) {
		spectra.addCompressed(compressed);
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
		set(index, new ISpectrum(spectrum));
	}
	
	/**
	 * Adds a previously compressed spectrum. Note that this will not trigger the {@link Analysis}, and will need to be done manually
	 * @param compressed
	 */
	public void set(int index, Compressed<Spectrum> compressed) {
		spectra.setCompressed(index, compressed);
	}
	
	
	public LoaderQueue createLoaderQueue(int capacity) {
		/*
		 * CompressedLoaderQueue will move compression up to before the point the Spectrum
		 * is stored in the queue. This saves order of 10s of MBs, but slows down the 
		 * DataSource thread, since it now handles the compression.
		 */
		if (
				(PeakabooConfiguration.memorySize == MemorySize.TINY && capacity > 200) || //1.6 - 3.2 MB
				(PeakabooConfiguration.memorySize == MemorySize.SMALL && capacity > 400) || //3.2 - 6.4 MB
				(PeakabooConfiguration.memorySize == MemorySize.MEDIUM && capacity > 1000) || //8 - 16 MB
				(PeakabooConfiguration.memorySize == MemorySize.LARGE && capacity > 20000) //160 - 320 MB
			) {
			return new CompressedLoaderQueue(this, analysis, capacity);
		} else {
			return new SimpleLoaderQueue(this, capacity);			
		}
	}

	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

}
