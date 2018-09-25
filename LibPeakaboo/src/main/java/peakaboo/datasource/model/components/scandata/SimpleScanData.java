package peakaboo.datasource.model.components.scandata;

import java.util.List;

import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import peakaboo.common.PeakabooConfiguration;
import peakaboo.common.PeakabooConfiguration.MemorySize;
import peakaboo.datasource.model.PeakabooLists;
import peakaboo.datasource.model.components.scandata.loaderqueue.CompressedLoaderQueue;
import peakaboo.datasource.model.components.scandata.loaderqueue.LoaderQueue;
import peakaboo.datasource.model.components.scandata.loaderqueue.SimpleLoaderQueue;

public class SimpleScanData implements ScanData {

	
	private List<Spectrum> spectra;
	private float maxEnergy;
	private float minEnergy = 0;
	private String name;
	
	public SimpleScanData(String name) {
		this.name = name;
		this.spectra = PeakabooLists.create();
	}
		
	public SimpleScanData(String name, List<Spectrum> backingList) {
		this.name = name; 
		this.spectra = backingList;
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
		if (PeakabooConfiguration.memorySize == MemorySize.LARGE) {
			return new SimpleLoaderQueue(this, capacity);
		} else {
			return new CompressedLoaderQueue(this, capacity);
		}
	}


}
