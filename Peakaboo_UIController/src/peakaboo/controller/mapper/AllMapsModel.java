package peakaboo.controller.mapper;

import java.util.List;

import peakaboo.controller.Model;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.SISize;
import peakaboo.mapping.MapResultSet;

public class AllMapsModel extends Model {

	public peakaboo.drawing.DrawingRequest dr;

	public List<Integer> badPoints;

	public Coord<Range<Number>> realDimensions;
	public SISize realDimensionsUnits;
	public Coord<Integer> dataDimensions;
	public Coord<Integer> interpolatedSize = new Coord<Integer>(0, 0);

	public boolean dimensionsProvided;

	public AllMapsViewOptions viewOptions;

	private MapResultSet mapResults;

	public AllMapsModel(MapResultSet maps) {

		mapResults = maps;
		
		dimensionsProvided = false;
		badPoints = DataTypeFactory.<Integer> list();
		viewOptions = new AllMapsViewOptions();

		dr = new peakaboo.drawing.DrawingRequest();

	}

	public int mapSize() {
		return mapResults.size();
	}

}
