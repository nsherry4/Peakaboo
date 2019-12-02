package org.peakaboo.display.map.modes.composite;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapTechniqueFactory;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SelectionMaskPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SpectrumMapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.PaddingAxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.ThermalScalePalette;

public class CompositeMapMode extends MapMode{


	private SpectrumMapPainter contourMapPainter;
	
	public void draw(Coord<Integer> size, MapRenderData data, MapRenderSettings settings, Surface backend, int spectrumSteps) {
		map.setContext(backend);
		
		CompositeModeData compositedata = (CompositeModeData) data.mapModeData;
		
		size = this.setDimensions(settings, size);
		backend.rectAt(0, 0, (float)size.x, (float)size.y);
		backend.setSource(new PaletteColour(0xffffffff));
		backend.fill();
		
		AbstractPalette palette				=		new ThermalScalePalette(spectrumSteps, settings.monochrome);
		List<AbstractPalette> paletteList	=		new ArrayList<>();
		
				
		dr.uninterpolatedWidth = settings.filteredDataWidth;
		dr.uninterpolatedHeight = settings.filteredDataHeight;
		dr.dataWidth = settings.filteredDataWidth;
		dr.dataHeight = settings.filteredDataHeight;
		dr.viewTransform = ViewTransform.LINEAR;
		dr.screenOrientation = false;
		
		if (settings.scalemode == MapScaleMode.RELATIVE) {
			dr.maxYIntensity = compositedata.getData().max();
		} else {
			dr.maxYIntensity = data.maxIntensity;
		}

		List<AxisPainter> axisPainters = new ArrayList<>();
		super.setupTitleAxisPainters(settings, axisPainters);
		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));
		axisPainters.add(getDescriptionPainter(settings));
		
		axisPainters.add(MapMode.getSpectrumPainter(settings, spectrumSteps, paletteList));
		map.setAxisPainters(axisPainters);
		
		
		//why are we resetting this value?
		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = backend.isVectorSurface();

		paletteList.add(palette);
		
		List<MapPainter> mapPainters = new ArrayList<>();
		if (contourMapPainter == null) {
			contourMapPainter = MapTechniqueFactory.getTechnique(paletteList, compositedata.getData(), spectrumSteps); 
		} else {
			contourMapPainter.setData(compositedata.getData());
			contourMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(contourMapPainter);
		
		
		//Selection Painter
		SelectionMaskPainter selectionPainter = super.getSelectionPainter(
				new PaletteColour(0xffffffff), 
				settings.selectedPoints, 
				settings.userDataWidth, 
				settings.userDataHeight);
		mapPainters.add(selectionPainter);
			
		
		map.setPainters(mapPainters);
		map.draw();

		dr.drawToVectorSurface = oldVector;

	}


	@Override
	public void invalidate() {
		map.needsMapRepaint();
		if (contourMapPainter != null)			contourMapPainter.clearBuffer();
	}


	@Override
	public MapModes getMode() {
		return MapModes.COMPOSITE;
	}


	
}
