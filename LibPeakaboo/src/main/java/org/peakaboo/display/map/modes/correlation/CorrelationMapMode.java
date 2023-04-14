package org.peakaboo.display.map.modes.correlation;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapTechniqueFactory;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SelectionMaskPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SpectrumMapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.LineAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.PaddingAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.TitleAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.RangeTickFormatter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickFormatter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.Palette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.Spectrums;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.ColourListPalette;

public class CorrelationMapMode extends MapMode {

	public static String MODE_NAME = "Correlation";
	
	private SpectrumMapPainter correlationMapPainter;
		
	@Override
	public void draw(Coord<Integer> size, MapRenderData data, MapRenderSettings settings, Surface backend, int spectrumSteps) {
		map.setContext(backend);
		
		CorrelationModeData correlationData = (CorrelationModeData) data.mapModeData;
		
		//overrides for this display style
		settings.drawCoord = false;
		settings.physicalCoord = false;
		
		
		//TODO: move this call to Mapper
		size = this.setDimensions(settings, size);
		backend.rectAt(0, 0, (float)size.x, (float)size.y);
		backend.setSource(new PaletteColour(0xffffffff));
		backend.fill();
		
		dr.uninterpolatedWidth = correlationData.getSize().x;
		dr.uninterpolatedHeight = correlationData.getSize().y;
		dr.dataWidth = correlationData.getSize().x;
		dr.dataHeight = correlationData.getSize().y;
		dr.viewTransform = ViewTransform.LINEAR;
		dr.screenOrientation = false;
		dr.maxYIntensity = correlationData.getData().max();
		
		//why are we resetting this value?
		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = backend.isVectorSurface();
		
		map.setDrawingRequest(dr);

		List<AbstractPalette> paletteList = new ArrayList<>();
		if (settings.monochrome) {
			paletteList.add(new ColourListPalette(Spectrums.generateSpectrum(spectrumSteps, Palette.MONOCHROME_INVERTED.getPaletteData(), 1f, 1f), false));
		} else {
			paletteList.add(new ColourListPalette(Spectrums.generateSpectrum(spectrumSteps, Palette.GEORGIA.getPaletteData(), 1f, 1f), false));
		}
		
		List<AxisPainter> axisPainters = new ArrayList<>();
		super.setupTitleAxisPainters(settings, axisPainters);
		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));

		axisPainters.add(getDescriptionPainter(settings));
		axisPainters.add(MapMode.getSpectrumPainter(settings, spectrumSteps, paletteList));
		axisPainters.add(new PaddingAxisPainter(0, 0, 2, 0));
		
		axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TEXT, correlationData.yAxisTitle, "", "", correlationData.xAxisTitle));
		axisPainters.add(new PaddingAxisPainter(0, 0, 2, 2));
		
		TickFormatter xTick = new RangeTickFormatter(0, correlationData.xMaxCounts).withTickSize(0.5f);
		TickFormatter yTick = new RangeTickFormatter(0, correlationData.yMaxCounts).withTickSize(0.5f).withRotate(false);
		axisPainters.add(new TickMarkAxisPainter(null, xTick, null, yTick));
		axisPainters.add(new LineAxisPainter(true, false, false, true));
		
		map.setAxisPainters(axisPainters);
		
		
		List<MapPainter> mapPainters = new ArrayList<>();
		if (correlationMapPainter == null) {
			correlationMapPainter = MapTechniqueFactory.getTechnique(paletteList, correlationData.data, spectrumSteps); 
		} else {
			correlationMapPainter.setData(correlationData.data);
			correlationMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(correlationMapPainter);
		
		//Selection Painter
		SelectionMaskPainter selectionPainter = super.getSelectionPainter(
				new PaletteColour(0x80498ed7), 
				settings.selectedPoints, 
				correlationData.getSize().x, 
				correlationData.getSize().y);
		mapPainters.add(selectionPainter);
		
		map.setPainters(mapPainters);
		
		map.draw();
		
		dr.drawToVectorSurface = oldVector;
	}

	@Override
	public Coord<Integer> setDimensions(MapRenderSettings settings, Coord<Integer> size) {
		
		if (settings == null) {
			settings = new MapRenderSettings();
		}

		//need to set this up front so that calTotalSize has the right dimensions to work with
		//TODO: Fix me?
		dr.dataHeight = 100;
		dr.dataWidth = 100;
		
		double width = 0;
		double height = 0;
		
		if (size != null) {
			width = size.x;
			height = size.y;
		}
		
		//Auto-detect dimensions
		if (size == null || (size.x == 0 && size.y == 0)) {
			dr.imageWidth = 1000;
			dr.imageHeight = 1000;
			Coord<Float> newsize = map.calcTotalSize();
			width = newsize.x;
			height = newsize.y;
		}
		else if (size.x == 0) {
			dr.imageWidth = dr.imageHeight * 10;
			width = map.calcTotalSize().x;
			
		}
		else if (size.y == 0) {
			dr.imageHeight = dr.imageWidth * 10;
			height = map.calcTotalSize().y;
		}
		
		size = new Coord<>((int)Math.round(width), (int)Math.round(height));
		dr.imageWidth = (float)size.x;
		dr.imageHeight = (float)size.y;
		
		return size;
		
	}
	
	@Override
	public String mapModeName() {
		return MODE_NAME;
	}

	
	
	@Override
	public void invalidate() {
		map.needsMapRepaint();
		if (correlationMapPainter != null) { correlationMapPainter.clearBuffer(); }
	}

}
