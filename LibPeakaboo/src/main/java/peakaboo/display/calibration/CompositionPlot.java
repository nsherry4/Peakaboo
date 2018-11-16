package peakaboo.display.calibration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.Spectrum;
import cyclops.visualization.Surface;
import cyclops.visualization.drawing.DrawingRequest;
import cyclops.visualization.drawing.ViewTransform;
import cyclops.visualization.drawing.painters.axis.AxisPainter;
import cyclops.visualization.drawing.painters.axis.LineAxisPainter;
import cyclops.visualization.drawing.painters.axis.TitleAxisPainter;
import cyclops.visualization.drawing.plot.PlotDrawing;
import cyclops.visualization.drawing.plot.painters.PlotPainter;
import cyclops.visualization.drawing.plot.painters.PlotPainter.TraceType;
import cyclops.visualization.drawing.plot.painters.axis.GridlinePainter;
import cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter;
import cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter.TickFormatter;
import cyclops.visualization.drawing.plot.painters.plot.AreaPainter;
import cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter;
import cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
import cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import cyclops.visualization.palette.PaletteColour;
import peakaboo.calibration.Composition;
import peakaboo.curvefit.peak.table.Element;

public class CompositionPlot {

	private Composition comp;
	
	private PlotDrawing plotDrawing;
	private Spectrum data;
	private DrawingRequest dr = new DrawingRequest();
	private List<PlotPainter> plotPainters;
	private List<AxisPainter> axisPainters;
		
	public CompositionPlot(Composition conc) {
		this.comp = conc;
		this.data = getData();
		configure();
	}
	
	protected void configure() {
	
		dr.dataHeight = 1;
		dr.dataWidth = data.size();
		dr.drawToVectorSurface = false;
		dr.maxYIntensity = data.max();
		dr.unitSize = 1f;
		dr.viewTransform = ViewTransform.LINEAR;
		
		plotPainters = new ArrayList<>();
		plotPainters.add(new GridlinePainter(new Bounds<Float>(0f, dr.maxYIntensity*100f)));

		plotPainters.add(new AreaPainter(data, 
				new PaletteColour(0xff00897B), 
				new PaletteColour(0xff00796B), 
				new PaletteColour(0xff004D40)
			).withTraceType(TraceType.BAR));
		

		List<Element> elements = comp.elementsByConcentration();

		axisPainters = new ArrayList<>();
		
		axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TEXT, "Composition", null, null, "Elements - Calibrated With " + comp.getProfile().getName()));
		NumberFormat format = new DecimalFormat("0.0");
		Function<Integer, String> sensitivityFormatter = i -> format.format(  ((float)i/10000f)  ) + "%";
		axisPainters.add(new TickMarkAxisPainter(
				new TickFormatter(0f, dr.maxYIntensity, sensitivityFormatter), 
				new TickFormatter(-0.5f, data.size()-1-0.5f+0.999f, i -> elements.get(i).name()), 
				null, 
				new TickFormatter(0f, dr.maxYIntensity, sensitivityFormatter),
				false, 
				false));
		axisPainters.add(new LineAxisPainter(true, true, false, true));
	}

	public PlotDrawing draw(Surface context, Coord<Integer> size) {

		context.setSource(new PaletteColour(0xffffffff));
		context.rectAt(0, 0, size.x, size.y);
		context.fill();
		
		dr.imageWidth = size.x;
		dr.imageHeight = size.y;
		plotDrawing = new PlotDrawing(context, dr, plotPainters, axisPainters);	
		plotDrawing.draw();
		
		return plotDrawing;
	}
	
	private Spectrum getData() {	
		
		List<Element> es = comp.elementsByConcentration();
		Spectrum spectrum = new ISpectrum(es.size());
		for (Element e : es) {
			spectrum.add(comp.get(e));
		}
		
		return spectrum;
	}

	
}
