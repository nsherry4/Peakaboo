package org.peakaboo.dataset.source.model.components.datasize;

import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.framework.cyclops.Coord;

public class DummyDataSize implements DataSize {
	
	DataSource datasource;
	
	public DummyDataSize(DataSource datasource) {
		this.datasource = datasource;
	}


	@Override
	public Coord<Integer> getDataDimensions()
	{
		return new Coord<>(datasource.getScanData().scanCount(), 1);
	}


	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		return new Coord<>(index, 0);
	}
	
}
