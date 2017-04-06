package peakaboo.datasource.components.scandata;

import java.util.List;

import peakaboo.datasource.SpectrumList;
import scitypes.Spectrum;

public class SimpleScanData implements ScanData {

	private List<Spectrum> spectra;
	private float maxEnergy;
	private String name;
	
	public SimpleScanData(String name) {
		this.name = name;
		spectra = SpectrumList.create(name);
	}

	@Override
	public Spectrum get(int index) throws IndexOutOfBoundsException {
		return new Spectrum(spectra.get(index)); //return read-only
	}
	
	public void add(Spectrum spectrum) {
		spectra.add(spectrum);
	}

	public void set(int index, Spectrum spectrum) {
		spectra.set(index, spectrum);
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
	public String datasetName() {
		return name;
	}
	
}
