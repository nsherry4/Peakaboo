package peakaboo.datasource.model.components.physicalsize;

import java.util.HashMap;
import java.util.Map;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;

public class SimplePhysicalSize implements PhysicalSize {

	private Map<Integer, Coord<Number>> points = new HashMap<>();
	private Coord<Bounds<Number>> dimensions = null;
	
	@Override
	public Coord<Number> getPhysicalCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		if (points.containsKey(index)) {
			return points.get(index);
		} else {
			throw new IndexOutOfBoundsException("Index " + index + " has no data");
		}
	}

	@Override
	public Coord<Bounds<Number>> getPhysicalDimensions() {
		if (dimensions == null) {
			float minx = Float.MAX_VALUE;
			float miny = Float.MAX_VALUE;
			float maxx = -Float.MAX_VALUE;
			float maxy = -Float.MAX_VALUE;
			
			for (Coord<Number> point : points.values()) {
				if (point.x.floatValue() < minx) minx = point.x.floatValue();
				if (point.y.floatValue() < miny) miny = point.y.floatValue();
				if (point.x.floatValue() > maxx) maxx = point.x.floatValue();
				if (point.y.floatValue() > maxy) maxy = point.y.floatValue();
			}
			
			dimensions = new Coord<>(new Bounds<Number>(minx, maxx), new Bounds<Number>(miny, maxy));
			
		}
		return dimensions;
		
	}

	@Override
	public SISize getPhysicalUnit() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void putPoint(int index, Coord<Number> point) {
		points.put(index, point);
	}

}
