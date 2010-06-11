package peakaboo.dataset;

import peakaboo.datatypes.Spectrum;

public class ScanContainer {

	public boolean hasData;
	public Spectrum data;
	
	public ScanContainer() {
		hasData = false;
		data = null;
	}
	
	public ScanContainer(Spectrum list) {
		hasData = true;
		data = list;
	}
	
}
