package peakaboo.datasource.model.components.physicalsize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.encoders.CompoundEncoder;
import net.sciencestudio.scratch.encoders.compressors.Compressors;
import net.sciencestudio.scratch.encoders.serializers.Serializers;
import net.sciencestudio.scratch.list.ScratchLists;
import peakaboo.common.MemoryProfile;
import peakaboo.common.MemoryProfile.Size;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.ISpectrum;
import scitypes.SISize;
import scitypes.SparsedList;

public class SimplePhysicalSize implements PhysicalSize {

	private List<Coord<Number>> points;
	private Coord<Bounds<Number>> dimensions = null;
	
	private SISize units;
	
	@SuppressWarnings("unchecked")
	public SimplePhysicalSize(SISize units) {
		this.units = units;
		
		List<Coord<Number>> scratch = new ArrayList<>();
		if (MemoryProfile.size == Size.SMALL) {
			//Physical coordinates on large maps can eat up ~10MB, which is enough to 
			//warrant disk-based storage when we're low on memory 
			
			ScratchEncoder<?> fst = Serializers.fst(Coord.class);
			ScratchEncoder<byte[]> lz4 = Compressors.lz4fast();
			scratch = (List<Coord<Number>>) ScratchLists.tryDiskBacked(new CompoundEncoder<>(fst, lz4));
		}
		points = new SparsedList<>(scratch);
	}
	
	@Override
	public Coord<Number> getPhysicalCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		if (points.get(index) != null) {
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
			
			for (Coord<Number> point : points) {
				if (point == null) { continue; }
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
		return units;
	}
	
	public void putPoint(int index, Coord<Number> point) {
		points.set(index, point);
	}
	
}
