package org.peakaboo.datasource.model.components.scandata;


import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.components.scandata.analysis.DataSourceAnalysis;

import cyclops.ReadOnlySpectrum;

public class DummyScanData implements ScanData {

	
	@Override
	public ReadOnlySpectrum get(int index) throws IndexOutOfBoundsException {
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
		return new DataSourceAnalysis();
	}

}
