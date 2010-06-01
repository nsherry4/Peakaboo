package peakaboo.dataset;

import java.util.List;

public class ScanContainer {

	public boolean hasData;
	public List<Double> data;
	
	public ScanContainer() {
		hasData = false;
		data = null;
	}
	
	public ScanContainer(List<Double> list) {
		hasData = true;
		data = list;
	}
	
}
