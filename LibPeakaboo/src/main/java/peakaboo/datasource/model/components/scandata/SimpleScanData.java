package peakaboo.datasource.model.components.scandata;

import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import net.sciencestudio.scratch.list.ScratchList;
import net.sciencestudio.scratch.single.Compressed;
import peakaboo.common.PeakabooConfiguration;
import peakaboo.common.PeakabooConfiguration.MemorySize;
import peakaboo.datasource.model.PeakabooLists;
import peakaboo.datasource.model.components.scandata.loaderqueue.CompressedLoaderQueue;
import peakaboo.datasource.model.components.scandata.loaderqueue.LoaderQueue;
import peakaboo.datasource.model.components.scandata.loaderqueue.SimpleLoaderQueue;

public class SimpleScanData implements ScanData {

	
	private ScratchList<Spectrum> spectra;
	private float maxEnergy;
	private float minEnergy = 0;
	private String name;
	
	public SimpleScanData(String name) {
		this.name = name;
		this.spectra = PeakabooLists.create();
	}
		

	@Override
	public ReadOnlySpectrum get(int index) throws IndexOutOfBoundsException {
		return spectra.get(index); //return read-only
	}
	
	public void add(Spectrum spectrum) {
		spectra.add(spectrum);
	}
	
	/**
	 * Convenience method for adding a {@link Spectrum}
	 * @param spectrum a float array to add
	 */
	public void add(float[] spectrum) {
		add(new ISpectrum(spectrum));
	}
	
	public void add(Compressed<Spectrum> compressed) {
		spectra.addCompressed(compressed);
	}
	
	public void set(int index, Spectrum spectrum) {
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
	
	
	public void set(int index, Compressed<Spectrum> compressed) {
		spectra.setCompressed(index, compressed);
	}
	@Override
	public int scanCount() {
		return spectra.size();
	}

	@Override
	public String scanName(int index) {
		return "Scan #" + (index+1);
	}

	@Override
	public float maxEnergy() {
		return maxEnergy;
	}
	
	public void setMaxEnergy(float max) {
		maxEnergy = max;
	}

	@Override
	public float minEnergy() {
		return minEnergy;
	}
	
	public void setMinEnergy(float min) {
		minEnergy = min;
	}
	
	@Override
	public String datasetName() {
		return name;
	}
	
	public LoaderQueue createLoaderQueue(int capacity) {
		/*
		 * CompressedLoaderQueue will move compression up to before the point the Spectrum
		 * is stored in the queue. This saves order of 10s of MBs, but slows down the 
		 * DataSource thread, since it now handles the compression.
		 */
		if (PeakabooConfiguration.memorySize == MemorySize.SMALL || capacity > 5000) {
			return new CompressedLoaderQueue(this, capacity);
		} else {
			return new SimpleLoaderQueue(this, capacity);			
		}
	}


}
