package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;


import java.util.List;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.IntPair;
import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.SingleColourPalette;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * 
 * This class implements the drawing of a map using block pixel filling
 * 
 * @author Nathaniel Sherry, 2009
 */

public class RasterColorMapPainter extends MapPainter
{
	
	private IntArrayList pixels;
	protected Buffer buffer;
	protected boolean stale = true;
	private boolean enabled = true;


	public RasterColorMapPainter()
	{
		super(new SingleColourPalette(new PaletteColour(0, 0, 0, 0)));
	}


	public synchronized void setPixels(IntArrayList pixels) {
		this.pixels = pixels;
		this.stale = true;
	}
	
	public synchronized void setPixels(List<PaletteColour> pixelColours) {
		this.pixels = new IntArrayList(pixelColours.size());
		for (int i = 0; i < pixelColours.size(); i++) {
			this.pixels.add(pixelColours.get(i).getARGB());
		}
		this.stale = true;
	}
	
	public synchronized void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public synchronized void drawMap(PainterData p, float cellSize, float rawCellSize)
	{

		if (!enabled) { return; }
		
		p.context.save();

			IntArrayList data = transformListDataForMap(p.dr, pixels);
	
			if (p.dr.drawToVectorSurface) {
				drawAsScalar(p, data, cellSize);
				buffer = null;
			} else {
				
				if (buffer == null || buffer.getWidth() != p.dr.dataWidth || buffer.getHeight() != p.dr.dataHeight) {
					buffer = createRasterBuffer(p);
				}
				if (stale) {
					drawToRasterBuffer(p, data, cellSize, p.dr.dataHeight * p.dr.dataWidth);
				}
				p.context.compose(buffer, 0, 0, cellSize);
				
			}

		p.context.restore();

	}


	private Buffer createRasterBuffer(PainterData p) {
		return p.context.getImageBuffer(p.dr.dataWidth, p.dr.dataHeight);
	}
	
	private void drawToRasterBuffer(PainterData p, final IntArrayList data, float cellSize, final int maximumIndex)
	{
		final PaletteColour transparent = new PaletteColour(0x00000000);
		
		int size = Math.min(maximumIndex, data.size());
		for (int ordinal = 0; ordinal < size; ordinal++) {
			buffer.setPixelARGB(ordinal, data.getInt(ordinal));
		}
		
		this.stale = false;
		
	}


	private void drawAsScalar(PainterData p, IntArrayList data, float cellSize)
	{

		p.context.save();

		p.context.rectAt(0, 0, p.plotSize.x, p.plotSize.y);
		p.context.clip();
		
		// draw the map
		for (int y = 0; y < p.dr.dataHeight; y++) {
			for (int x = 0; x < p.dr.dataWidth; x++) {



				int index = y * p.dr.dataWidth + x;
				p.context.rectAt(x * cellSize, y * cellSize, cellSize + 1, cellSize + 1);
				p.context.setSource(data.getInt(index));
				p.context.fill();

				
			}
		}
		
		p.context.restore();
	}

	
	protected IntArrayList transformListDataForMap(DrawingRequest dr, IntArrayList list)
	{
		IntArrayList flip = new IntArrayList(list.size());
		for (int i = 0; i < list.size(); i++) {
			flip.add(0);
		}
		
		//vertical orientation flip
		if (!dr.screenOrientation) {
			/*
			 * the screenOrientation setting puts the origin (0,0) in the top left rather
			 * than in the bottom left. BUT, having the origin in the top left is the
			 * cyclops-native format. So when flipY is set, we don't have to do anything.
			 * It's when it's not set that we have to flip it...
			 */
			
			//We have to accommodate interpolated and uninterpolated data here
			GridPerspective<Float> grid;
			int height = 0;
			if (list.size() == dr.dataHeight * dr.dataWidth) {
				grid = new GridPerspective<>(dr.dataWidth, dr.dataHeight, 0f);
				height = dr.dataHeight;
			} else if (list.size() == dr.uninterpolatedWidth * dr.uninterpolatedHeight) {
				grid = new GridPerspective<>(dr.uninterpolatedWidth, dr.uninterpolatedHeight, 0f);
				height = dr.uninterpolatedHeight;
			} else {
				CyclopsLog.get().log(Level.WARNING, "List has wrong dimensions");
				grid = new GridPerspective<>(dr.dataWidth, dr.dataHeight, 0f);
				//throw new IllegalArgumentException("List has wrong dimensions");
			}
			
			for (int i = 0; i < flip.size(); i++) {
				IntPair xy = grid.getXYFromIndex(i);
				int x = xy.first;
				int y = xy.second;
				y = (height-1) - y;
				int index = grid.getIndexFromXY(x, y);
				if (index == -1) continue;
				flip.set(index, list.getInt(i));
			}
		}		
		
		return flip;
	}
	
	/*
	 * We don't actually delete the buffer (we only do that when the size changes),
	 * but we mark it as needing a redraw. Redraws here change every pixel, so we
	 * don't need to worry about clearing the buffer either.
	 */
	public void clearBuffer()
	{
		stale = true;
	}


	@Override
	public boolean isBufferingPainter()
	{
		return true;
	}
	
}
