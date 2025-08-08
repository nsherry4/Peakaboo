package org.peakaboo.framework.cyclops.visualization.drawing.map;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.Drawing;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.Palette;

/**
 * 
 * This class contains logic for drawing maps. Specific methods of drawing the map data itself (raster,
 * contour) are contained elsewhere.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class MapDrawing extends Drawing
{

		
	private List<MapPainter>	painters;
	private List<AxisPainter>	axisPainters;
	

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
	 * @see Palette
	 */
	public MapDrawing(Surface context, DrawingRequest dr, List<AxisPainter> axisPainters)
	{
		super(dr);
		this.context = context;
		this.axisPainters = axisPainters;
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
	public MapDrawing(Surface context, DrawingRequest dr, AxisPainter axisPainter)
	{
		super(dr);
		
		this.context = context;

		List<AxisPainter> ps = new ArrayList<>();
		ps.add(axisPainter);
		this.axisPainters = ps;

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
	public MapDrawing(Surface context, DrawingRequest dr)
	{
		super(dr);
		this.context = context;
		axisPainters = new ArrayList<>();
	}

	
	/**
	 * Creates a Map object
	 * @see Drawing
	 */
	public MapDrawing()
	{
		super();
		axisPainters = new ArrayList<>();
	}

	public void setAxisPainters(List<AxisPainter> axisPainters) {
		this.axisPainters = axisPainters;
	}
	public void setAxisPainters(AxisPainter painter) {
		axisPainters = new ArrayList<>();
		axisPainters.add(painter);
	}
	public void clearAxisPainters()
	{
		axisPainters = new ArrayList<>();
	}
	public void setPainters(List<MapPainter> painters) {
		this.painters = painters;
	}
	public void setPainters(MapPainter painter) {
		painters = new ArrayList<>();
		painters.add(painter);
	}
	

	/**
	 * Draws a map using the given data and the given set of {@link MapPainter}s
	 * 
	 * @param data
	 *            the data to use to draw the map
	 * @param mapPainters
	 *            the {@link MapPainter}s to use to draw the map
	 */
	@Override
	public void draw()
	{
		
		if (context == null) return;
		
		float oldMaxIntensity = dr.maxYIntensity;

		Coord<Bounds<Float>> borderSizes = calcAxisBorders();
		Coord<Float> mapDimensions = calcMapSize();

		// Draw Map
		context.save();


			context.translate(borderSizes.x.start, borderSizes.y.start);
			
			for (MapPainter t : painters) {
				t.draw(new PainterData(context, dr, mapDimensions, null));
			}


			
		context.restore();


		// Draw Axes
		context.save();

	
			Bounds<Float> availableX, availableY;
			Coord<Float> totalSize = calcTotalSize();
	
			availableX = new Bounds<>(0.0f, totalSize.x);
			availableY = new Bounds<>(0.0f, totalSize.y);
			PainterData p = new PainterData(context, dr, new Coord<>(dr.imageWidth, dr.imageHeight), null);
	
			if (axisPainters != null) {
	
				Pair<Float, Float> axisSizeX, axisSizeY;
	
				for (AxisPainter axisPainter : axisPainters) {
	
					axisPainter.setDimensions(
	
					new Bounds<>(availableX.start, availableX.end),
							new Bounds<>(availableY.start, availableY.end)
	
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
	public Coord<Float> calculateMapDimensions()
	{

		Coord<Float> borders = calcBorderSize();

		float cellSize = calcInterpolatedCellSize(dr.imageWidth - borders.x, dr.imageHeight - borders.y, dr);
		if (cellSize < 0.01) cellSize = 0.01f;

		return new Coord<>(dr.dataWidth * cellSize + borders.x, dr.dataHeight * cellSize + borders.y);

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
	public static float calcInterpolatedCellSize(float availableWidth, float availableHeight, DrawingRequest dr)
	{

		float cellWidth, cellHeight;

		cellWidth = availableWidth / dr.dataWidth;
		cellHeight = availableHeight / dr.dataHeight;

		float cellSize;
		cellSize = cellWidth > cellHeight ? cellHeight : cellWidth;
	
		return cellSize;

	}
	
	public static float calcUninterpolatedCellSize(float availableWidth, float availableHeight, DrawingRequest dr)
	{

		float cellWidth, cellHeight;

		cellWidth = availableWidth / dr.uninterpolatedWidth;
		cellHeight = availableHeight / dr.uninterpolatedHeight;

		float cellSize;
		cellSize = cellWidth > cellHeight ? cellHeight : cellWidth;
	
		return cellSize;

	}



	/**
	 * Calculates the bounds of the space the borders will consume. 
	 * Not the total space consumed by the borders, but provides four
	 * coordinates which define a rectangle (which should be within the 
	 * bounds of the full drawing dimensions) within which the 
	 * data-drawing should be positioned. On a 100x100px image with
	 * 10px borders, this should return ((10, 10), (90, 90)) as the 
	 * coordinates.
	 * 
	 * 
	 * @return A coordinate pair defining the x and y space consumed by the axes
	 */
	public Coord<Bounds<Float>> calcAxisBorders()
	{
		return AxisPainter.calcAxisBorders(new PainterData(context, dr, new Coord<>(dr.imageWidth, dr.imageHeight), null), axisPainters);

	}

	/**
	 * Calculates the total space consumed by the border elements
	 */
	public Coord<Float> calcBorderSize()
	{

		Coord<Bounds<Float>> axisBorders = calcAxisBorders();
		float x, y;

		x = axisBorders.x.start + (dr.imageWidth - axisBorders.x.end);
		y = axisBorders.y.start + (dr.imageHeight - axisBorders.y.end);

		return new Coord<>(x, y);

	}


	/**
	 * Calculate the size the actual data-drawing will use, after accounting for the space consumed by the axis/border
	 */
	public Coord<Float> calcMapSize()
	{

		Coord<Float> borderSize = calcBorderSize();
		float x, y;

		float cellSize = calcInterpolatedCellSize(dr.imageWidth - borderSize.x, dr.imageHeight - borderSize.y, dr);
		x = dr.dataWidth * cellSize;
		y = dr.dataHeight * cellSize;

		return new Coord<>(x, y);

	}


	/**
	 * Calculates the total size of the drawing, including both the real data-drawing and the borders
	 */
	public Coord<Float> calcTotalSize()
	{

		Coord<Float> borderSize = calcBorderSize();
		Coord<Float> mapSize = calcMapSize();

		return new Coord<>(borderSize.x + mapSize.x, borderSize.y + mapSize.y);

	}

	

	/**
	 * Transforms a drawing-pixel-based location on the drawing to an (x, y) index in data dimensions
	 */
	public Coord<Integer> getMapCoordinateAtPoint(float x, float y, boolean allowOutOfBounds)
	{


		Coord<Bounds<Float>> borders = calcAxisBorders();
		float topOffset, leftOffset;
		topOffset = borders.y.start;
		leftOffset = borders.x.start;

		float mapX, mapY;
		mapX = x - leftOffset;
		mapY = y - topOffset;

		Coord<Float> mapSize = calcMapSize();
		float percentX, percentY;
		percentX = mapX / mapSize.x;
		percentY = mapY / mapSize.y;

		int indexX = (int) Math.floor(dr.uninterpolatedWidth * percentX);
		int indexY = (int) Math.floor(dr.uninterpolatedHeight * percentY);
		
		if(!dr.screenOrientation) {
			indexY = (dr.uninterpolatedHeight-1) - indexY;
		}
		
		if (!allowOutOfBounds && (indexX < 0 || indexX >= dr.uninterpolatedWidth)) return null;
		if (!allowOutOfBounds && (indexY < 0 || indexY >= dr.uninterpolatedHeight)) return null;
		
		return new Coord<>(indexX, indexY);

	}

	public void needsMapRepaint() {
		
		if (painters == null) return;
		
		for (MapPainter p : painters)
		{
			p.clearBuffer();
		}
		
	}





}
