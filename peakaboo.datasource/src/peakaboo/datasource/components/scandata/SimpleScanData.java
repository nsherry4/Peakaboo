package peakaboo.datasource.components.scandata;

import static java.util.stream.Collectors.toList;

import java.util.List;

import fava.functionable.Range;
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
		return spectra.get(index);
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
	public List<String> scanNames() {
		return new Range(0, scanCount()-1).stream().map(e -> "Scan #" + (e+1)).collect(toList());
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
