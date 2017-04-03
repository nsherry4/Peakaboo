package peakaboo.datasource.components.datasize;

import java.util.List;

import peakaboo.datasource.DataSource;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;

public class DummyDimensions implements DataSize {

	DataSource datasource;
	
	public DummyDimensions(DataSource datasource) {
		this.datasource = datasource;
	}


	@Override
	public Coord<Integer> getDataDimensions()
	{
		return new Coord<Integer>(datasource.getScanData().scanCount(), 1);
	}


	@Override
	public Coord<Bounds<Number>> getPhysicalDimensions()
	{
		return null;
	}


	
	
	

	@Override
	public Coord<Number> getPhysicalCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		return null;
	}


	@Override
	public SISize getPhysicalUnit() {
		return null;
	}


	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		return new Coord<Integer>(index, 0);
	}
	
}
