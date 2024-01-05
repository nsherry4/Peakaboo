package org.peakaboo.dataset.source.model.components.scandata;


import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.tier.Tier;

public class DummyScanData implements ScanData {

	
	@Override
	public SpectrumView get(int index) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int scanCount() {
		return 0;
	}

	@Override
	public String scanName(int index) {
		return "<No Data>";
	}

	@Override
	public float maxEnergy() {
		return 0;
	}

	@Override
	public String datasetName() {
		return "<No Data>";
	}

	@Override
	public float minEnergy() {
		return 0;
	}

	@Override
	public int firstNonNullScanIndex(int start) {
		return 0;
	}

	@Override
	public int lastNonNullScanIndex(int upto) {
		return 0;
	}

	@Override
	public Analysis getAnalysis() {
		return Tier.provider().createDataSourceAnalysis();
	}

}
