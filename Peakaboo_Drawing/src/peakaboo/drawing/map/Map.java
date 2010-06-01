package peakaboo.drawing.map;


import peakaboo.calculations.ListCalculations;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.drawing.backends.Buffer;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.backends.graphics2d.ImageBuffer;
import peakaboo.drawing.map.painters.MapPainter;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.painters.axis.AxisPainter;

import java.util.List;

/**
 * 
 * This class contains logic for drawing maps. Specific methods of drawing the map data itself (raster,
 * contour) are contained elsewhere.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class Map extends peakaboo.drawing.Drawing
{

	private Buffer				mapBuffer;
		
	private List<Double>		mapdata;

	private List<MapPainter>	painters;
	private List<AxisPainter>	axisPainters;

	private boolean 			needsMapRepaint = true;


	/**
	 * Creates a Map object
	 * 
	 * @param context
	 *            the context to draw to
	 * @param dr
	 *            the {@link DrawingRequest} defining how to draw maps
	 * @param palettes
	 *            the {@link Color} palettes for drawing maps
	 * @see DrawingRequest
	 * @see Color
	 * @see AbstractPalette
	 */
	public Map(Surface context, DrawingRequest dr, List<AxisPainter> axisPainters)
	{
		super(dr);
		this.context = context;
		this.axisPainters = axisPainters;
		mapBuffer = new ImageBuffer(dr.dataWidth, dr.dataHeight);

		// mapSize = calculateMapDimensions(dr, context);
	}


	/**
	 * Creates a Map object
	 * 
	 * @param context
	 *            the context to draw to
	 * @param dr
	 *            the {@link DrawingRequest} defining how to draw maps
	 * @param axisPainter
	 *            the {@link AxisPainter} to paint the axes of this map with
	 * @see DrawingRequest
	 */
	public Map(Surface context, DrawingRequest dr, AxisPainter axisPainter)
	{
		super(dr);
		
		this.context = context;

		List<AxisPainter> axisPainters = DataTypeFactory.<AxisPainter> list();
		axisPainters.add(axisPainter);
		this.axisPainters = axisPainters;

	}


	/**
	 * Creates a Map object
	 * 
	 * @param context
	 *            the context to draw to
	 * @param dr
	 *            the {@link DrawingRequest} defining how to draw maps
	 * @see DrawingRequest
	 */
	public Map(Surface context, DrawingRequest dr)
	{
		super(dr);
		this.context = context;
		axisPainters = DataTypeFactory.<AxisPainter> list();
	}


	public void setAxisPainters(List<AxisPainter> axisPainters) {
		this.axisPainters = axisPainters;
	}
	public void setPainters(List<MapPainter> painters) {
		this.painters = painters;
	}
	public void setPainters(MapPainter painter) {
		painters = DataTypeFactory.<MapPainter> list();
		painters.add(painter);
	}
	
	public void setData(List<Double> data) {
		this.mapdata = data;
	}
	

	/**
	 * Draws a map using the given data and the given set of {@link MapPainter}s
	 * 
	 * @param data
	 *            the data to use to draw the map
	 * @param mapPainters
	 *            the {@link MapPainter}s to use to draw the map
	 */
	public void draw()
	{
		
		if (dr.maxYIntensity < 0) dr.maxYIntensity = ListCalculations.max(mapdata);
		double oldMaxIntensity = dr.maxYIntensity;

		Coord<Range<Double>> borderSizes = calcAxisBorders();
		Coord<Double> mapDimensions = calcMapSize();

		// Draw Map
		context.save();

			if (dr.drawToVectorSurface){
				
				context.translate(borderSizes.x.start, borderSizes.y.start);
				
				for (MapPainter t : painters) {
					t.draw(new PainterData(context, dr, mapDimensions, null));
				}
		
			}else{
				if (needsMapRepaint){
					mapBuffer = new ImageBuffer((int)dr.imageWidth, (int)dr.imageHeight);
					mapBuffer.translate(borderSizes.x.start, borderSizes.y.start);
					
					for (MapPainter t : painters) {
						t.draw(new PainterData(mapBuffer, dr, mapDimensions, null));
					}
					needsMapRepaint = false;
				}
			
				context.compose(mapBuffer, 0, 0, 1.0);
			}

			
		context.restore();


		// Draw Axes
		context.save();

	
			Range<Double> availableX, availableY;
			Coord<Double> totalSize = calcTotalSize();
	
			availableX = new Range<Double>(0.0, totalSize.x);
			availableY = new Range<Double>(0.0, totalSize.y);
			PainterData p = new PainterData(context, dr, new Coord<Double>(dr.imageWidth, dr.imageHeight), null);
	
			if (axisPainters != null) {
	
				Pair<Double, Double> axisSizeX, axisSizeY;
	
				for (AxisPainter axisPainter : axisPainters) {
	
					axisPainter.setDimensions(
	
					new Range<Double>(availableX.start, availableX.end),
							new Range<Double>(availableY.start, availableY.end)
	
					);
	
	
					axisPainter.draw(p);
	
					axisSizeX = axisPainter.getAxisSizeX(p);
					axisSizeY = axisPainter.getAxisSizeY(p);
	
					availableX.start += axisSizeX.first;
					availableX.end -= axisSizeX.second;
					availableY.start += axisSizeY.first;
					availableY.end -= axisSizeY.second;
	
				}
	
			}


		context.restore();

		dr.maxYIntensity = oldMaxIntensity;

		return;

	}



	/**
	 * Calculates the dimensions of the map. Aspect ratio is preserved, and image dimensions are drawn from
	 * the given DrawinRequest.
	 * 
	 * @param dr
	 *            the DrawingRequest to define how maps should be drawn
	 * @param context
	 *            a Surface for use in calculating things like Font sizes.
	 * @return a Coordinate defining the area available to the map proper
	 */
	public Coord<Double> calculateMapDimensions()
	{

		Coord<Double> borders = calcBorderSize();

		double cellSize = calcCellSize(dr.imageWidth - borders.x, dr.imageHeight - borders.y, dr);
		if (cellSize < 0.01) cellSize = 0.01;

		return new Coord<Double>(dr.dataWidth * cellSize + borders.x, dr.dataHeight * cellSize + borders.y);

	}



	/**
	 * 
	 * @param availableWidth
	 *            the width of the desired map
	 * @param availableHeight
	 *            the height of the desired map
	 * @param dr
	 *            the DrawingRequest to define how maps should be drawn
	 * @return a cell size (square) for a single data point
	 */
	public static double calcCellSize(double availableWidth, double availableHeight, peakaboo.drawing.DrawingRequest dr)
	{

		double cellWidth, cellHeight;

		cellWidth = availableWidth / dr.dataWidth;
		cellHeight = availableHeight / dr.dataHeight;

		double cellSize;
		cellSize = cellWidth > cellHeight ? cellHeight : cellWidth;

		return cellSize;

	}



	/**
	 * Calculates the space required for drawing the axes
	 * 
	 * @return A coordinate pair defining the x and y space consumed by the axes
	 */
	public Coord<Range<Double>> calcAxisBorders()
	{
		
		return AxisPainter.calcAxisBorders(new PainterData(context, dr, new Coord<Double>(dr.imageWidth, dr.imageHeight), null), axisPainters);
/*
		Range<Double> availableX, availableY;
		availableX = new Range<Double>(0.0, dr.imageWidth);
		availableY = new Range<Double>(0.0, dr.imageHeight);
		PainterData p = new PainterData(context, dr, new Coord<Double>(dr.imageWidth, dr.imageHeight), null);

		if (axisPainters != null) {

			Pair<Double, Double> axisSizeX, axisSizeY;

			for (AxisPainter axisPainter : axisPainters) {


				axisPainter.setDimensions(

				new Range<Double>(availableX.start, availableX.end),
						new Range<Double>(availableY.start, availableY.end)

				);

				axisSizeX = axisPainter.getAxisSizeX(p);
				axisSizeY = axisPainter.getAxisSizeY(p);

				availableX.start += axisSizeX.first;
				availableX.end -= axisSizeX.second;
				availableY.start += axisSizeY.first;
				availableY.end -= axisSizeY.second;

			}

		}

		return new Coord<Range<Double>>(availableX, availableY);
*/
	}


	public Coord<Double> calcBorderSize()
	{

		Coord<Range<Double>> axisBorders = calcAxisBorders();
		double x = 0.0, y = 0.0;

		x = axisBorders.x.start + (dr.imageWidth - axisBorders.x.end);
		y = axisBorders.y.start + (dr.imageHeight - axisBorders.y.end);

		return new Coord<Double>(x, y);

	}


	public Coord<Double> calcMapSize()
	{

		Coord<Double> borderSize = calcBorderSize();
		double x = 0.0, y = 0.0;

		double cellSize = calcCellSize(dr.imageWidth - borderSize.x, dr.imageHeight - borderSize.y, dr);
		x = dr.dataWidth * cellSize;
		y = dr.dataHeight * cellSize;

		return new Coord<Double>(x, y);

	}


	public Coord<Double> calcTotalSize()
	{

		Coord<Double> borderSize = calcBorderSize();
		Coord<Double> mapSize = calcMapSize();

		return new Coord<Double>(borderSize.x + mapSize.x, borderSize.y + mapSize.y);

	}


	public void needsMapRepaint() {
		needsMapRepaint = true;
	}


	public void setDrawingRequest(DrawingRequest dr2) {
		dr = dr2;
	}


}
