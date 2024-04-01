package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot;

import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.SpectrumPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class StackedAreaPainter extends SpectrumPainter {

	private List<SpectrumView> spectra;
	private List<PaletteColour> fills, strokes;

	/**
	 * Accepts a list of Spectrum to be drawn as a stacked area plot. Each spectra
	 * must contain the total height for it's line.
	 */
	public StackedAreaPainter(List<SpectrumView> spectra, List<PaletteColour> fills, List<PaletteColour> strokes) {
		super(spectra.get(0));
		this.spectra = spectra;
		this.fills = fills;
		this.strokes = strokes;
	}

	public StackedAreaPainter(SpectrumView data, PaletteColour fill, PaletteColour stroke) {
		this(List.of(data), List.of(fill), List.of(stroke));
	}
	
	@Override
	public void drawElement(PainterData p) {
		
		for (int i = 0; i < spectra.size(); i++) {
			
			// Pull values for this spectrum
			var spectrum = spectra.get(i);
			var fill = this.fills.get(i);
			var stroke = this.strokes.get(i);
			
			// Trace the line
			traceData(p, spectrum, traceType);
			
			// Fill the area, preserve for stroke
			p.context.setSource(fill);
			p.context.fillPreserve();
			
			// Stroke the line
			p.context.setSource(stroke);
			p.context.stroke();
		}
				
	}

}
