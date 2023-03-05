package org.peakaboo.datasource.model.components.scandata;

import org.peakaboo.datasource.model.PeakabooLists;
import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.list.ScratchList;

public abstract class AbstractScanData implements ScanData {

	protected ScratchList<Spectrum> spectra;
	protected String name;
	protected float maxEnergy;
	protected float minEnergy = 0;
	
	
	public AbstractScanData(String name) {
		this.name = name;
		this.spectra = PeakabooLists.create();
	}
	
	@Override
	public ReadOnlySpectrum get(int index) throws IndexOutOfBoundsException {
		return spectra.get(index); //return read-only
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
	
}