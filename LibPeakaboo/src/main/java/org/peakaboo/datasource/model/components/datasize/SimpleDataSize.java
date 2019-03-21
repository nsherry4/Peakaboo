package org.peakaboo.datasource.model.components.datasize;


import org.peakaboo.framework.cyclops.Coord;

public class SimpleDataSize implements DataSize {


	int dataWidth, dataHeight;
	
	public SimpleDataSize() {}

	public int getDataWidth() {
		return dataWidth;
	}

	public void setDataWidth(int dataWidth) {
		this.dataWidth = dataWidth;
	}

	public int getDataHeight() {
		return dataHeight;
	}

	public void setDataHeight(int dataHeight) {
		this.dataHeight = dataHeight;
	}

	@Override
	public Coord<Integer> getDataDimensions() {
		return new Coord<>(dataWidth, dataHeight);
	}

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		int cx = index % dataWidth;
		int cy = (index - cx) / dataHeight;
		return new Coord<>(cx, cy);
	}
	
	
	
}
