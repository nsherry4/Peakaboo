package cyclops.visualization.drawing.map.painters;


import java.util.ArrayList;
import java.util.List;

import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;
import cyclops.visualization.drawing.DrawingRequest;
import cyclops.visualization.drawing.ViewTransform;
import cyclops.visualization.drawing.map.MapDrawing;
import cyclops.visualization.drawing.painters.Painter;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.palette.PaletteColour;
import cyclops.visualization.palette.palettes.AbstractPalette;

/**
 * 
 * A MapPainter is a specific way of drawing the {@link MapDrawing}
 * 
 * @author Nathaniel Sherry, 2009
 * @see MapDrawing
 *
 */

public abstract class MapPainter extends Painter
{

	
	protected List<AbstractPalette>	colourRules;


	public MapPainter(List<AbstractPalette> colourRules)
	{
		this.colourRules = colourRules;
	}
	
	public MapPainter(AbstractPalette colourRule)
	{
		List<AbstractPalette> rules = new ArrayList<AbstractPalette>();
		rules.add(colourRule);
		this.colourRules = rules;
	}


	@Override
	protected float getBaseUnitSize(cyclops.visualization.drawing.DrawingRequest dr)
	{
		// TODO Auto-generated method stub
		return 1;
	}
	
	public PaletteColour getColourFromRules(double intensity, double maximum, ViewTransform transform)
	{

		PaletteColour c;
		
		if (transform == ViewTransform.LOG) {
			//intensity will already have been log'd, we just have to log the max
			maximum = Math.log1p(maximum);
		}
		
		for (AbstractPalette r : colourRules) {
			c = r.getFillColour(intensity, maximum);
			if (c != null) return c;
		}

		return new PaletteColour(0x00000000);

	}
	
	public void setPalette(AbstractPalette palette)
	{
		colourRules.clear();
		colourRules.add(palette);
	}
	public void setPalettes(List<AbstractPalette> palettes)
	{
		colourRules.clear();
		colourRules.addAll(palettes);
	}


	
	
	@Override
	public final void drawElement(PainterData p)
	{
		
		p.context.save();
			
			// get the size of the cells
			float cellSize = MapDrawing.calcInterpolatedCellSize(p.plotSize.x, p.plotSize.y, p.dr);
			float rawCellSize = MapDrawing.calcUninterpolatedCellSize(p.plotSize.x, p.plotSize.y, p.dr);
	
			// clip the region
			p.context.rectAt(0, 0, p.dr.dataWidth * cellSize, p.dr.dataHeight * cellSize);
			p.context.clip();
			
			drawMap(p, cellSize, rawCellSize);
			
		p.context.restore();
		
	}
	
	public abstract void drawMap(PainterData p, float cellSize, float rawCellSize);
	
	protected Spectrum transformDataForMap(DrawingRequest dr, ReadOnlySpectrum data)
	{
		Spectrum transformedData = new ISpectrum(data);
		
		//log transform
		if (dr.viewTransform == ViewTransform.LOG) transformedData = SpectrumCalculations.logList(transformedData);
		
		//vertical orientation flip
		if (!dr.screenOrientation) {
			/*
			 * the screenOrientation setting puts the origin (0,0) in the top left rather
			 * than in the bottom left. BUT, having the origin in the top left is the
			 * cyclops-native format. So when flipY is set, we don't have to do anything.
			 * It's when it's not set that we have to flip it...
			 */
			GridPerspective<Float> grid = new GridPerspective<>(dr.dataWidth, dr.dataHeight, 0f);
			Spectrum flip = new ISpectrum(transformedData.size());
			for (int i = 0; i < data.size(); i++) {
				Pair<Integer, Integer> xy = grid.getXYFromIndex(i);
				int x = xy.first;
				int y = xy.second;
				y = (dr.dataHeight-1) - y;
				flip.set(i, grid.get(transformedData, x, y));
			}
			transformedData = flip;
		}		
		
		return transformedData;
	}
	
	public abstract boolean isBufferingPainter();
	public abstract void clearBuffer();
	
}
