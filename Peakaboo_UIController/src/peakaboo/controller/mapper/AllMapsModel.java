package peakaboo.controller.mapper;

import java.util.List;

import fava.datatypes.Bounds;

import peakaboo.controller.Model;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.mapping.results.MapResultSet;
import scidraw.drawing.DrawingRequest;
import scitypes.Coord;
import scitypes.SISize;

public class AllMapsModel extends Model {

	public DrawingRequest dr;

	public List<Integer> badPoints;

	public Coord<Bounds<Number>> realDimensions;
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

		dr = new DrawingRequest();

	}

	public int mapSize() {
		return mapResults.size();
	}

}
