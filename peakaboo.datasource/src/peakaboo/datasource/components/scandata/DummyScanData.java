package peakaboo.datasource.components.scandata;


import scitypes.Spectrum;

public class DummyScanData implements ScanData {

	
	@Override
	public Spectrum get(int index) throws IndexOutOfBoundsException {
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

}
