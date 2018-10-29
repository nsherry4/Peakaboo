package peakaboo.display.calibration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public abstract class ZCalibrationPlot {
	
	private TransitionSeriesType type;
	
	private PlotDrawing plotDrawing;
	private Spectrum data;
	private DrawingRequest dr = new DrawingRequest();
	private List<PlotPainter> plotPainters;
	private List<AxisPainter> axisPainters;
	
	private Element highlighted;
	
	public ZCalibrationPlot(TransitionSeriesType type) {
		this.type = type;
	}
	
	protected void configure() {
		
		int lowest = 0;
		int highest = 0;
				
		List<TransitionSeries> tss = getKeys(type);
		if (tss.size() >= 1) {
			lowest = tss.get(0).element.ordinal();
			highest = tss.get(tss.size() - 1).element.ordinal();
		}
		
		data = profileToSpectrum(getData(), type, lowest, highest);
		
		dr.dataHeight = 1;
		dr.dataWidth = data.size();
		dr.drawToVectorSurface = false;
		dr.maxYIntensity = data.max();
		dr.unitSize = 1f;
		dr.viewTransform = ViewTransform.LINEAR;
		
		plotPainters = new ArrayList<>();
		plotPainters.add(new GridlinePainter(new Bounds<Float>(0f, data.max()*100f)));
		
		plotPainters.add(new AreaPainter(data, 
				new PaletteColour(0xff00897B), 
				new PaletteColour(0xff00796B), 
				new PaletteColour(0xff004D40)
			).withTraceType(TraceType.BAR));
		
		
		plotPainters.add(new DataLabelPainter(getLabels(lowest, highest), 0.5f));
	
		
		axisPainters = new ArrayList<>();
		
		axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TEXT, getYAxisTitle(), null, getTitle(), "Element"));
		Function<Integer, String> sensitivityFormatter = getYAxisFormatter();
		axisPainters.add(new TickMarkAxisPainter(
				new TickFormatter(0f, data.max()*100f, sensitivityFormatter), 
				new TickFormatter((float)lowest-0.5f, (float)highest-0.5f+0.999f, i -> {  
					Element element = Element.values()[i];
					return element.name();
				}), 
				null, 
				new TickFormatter(0f, data.max()*100f, sensitivityFormatter),
				false, 
				false));
		axisPainters.add(new LineAxisPainter(true, true, true, true));
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
	
	private static Spectrum profileToSpectrum(Map<TransitionSeries, Float> values, TransitionSeriesType tst, int startOrdinal, int stopOrdinal) {	
		
		Spectrum spectrum = new ISpectrum(stopOrdinal - startOrdinal + 1);
		float value = 0;
		for (int ordinal = startOrdinal; ordinal <= stopOrdinal; ordinal++) {
			TransitionSeries ts = new TransitionSeries(Element.values()[ordinal], tst);
			if (ts != null && values.containsKey(ts)) {
				value = values.get(ts);
			} else {
				value = 0f;
			}
			int index = ordinal;
			spectrum.set(index - startOrdinal, value);
		}
		
		return spectrum;
	}
	
	
	
	public Element getHighlighted() {
		return highlighted;
	}

	public boolean setHighlighted(Element highlighted) {
		if (highlighted == this.highlighted) {
			return false;
		}
		this.highlighted = highlighted;
		configure();
		return true;
	}
		
	public Element getElement(int index) {
		List<TransitionSeries> keys = getKeys(type);
		if (keys.size() == 0) {
			return null;
		}
		int lowest = keys.get(0).element.ordinal();
		int highest = keys.get(keys.size()-1).element.ordinal();
		if (index < 0 || lowest + index > highest) {
			return null;
		} else {
			return Element.values()[lowest + index];
		}
	}
	
	public int getIndex(int xpos) {
		if (plotDrawing == null) return -1;

		Coord<Bounds<Float>> axesSize;
		int channel;

		// Plot p = new Plot(this.toyContext, model.dr);
		axesSize = plotDrawing.getPlotOffsetFromBottomLeft();

		float plotWidth = axesSize.x.end - axesSize.x.start; // width - axesSize.x;
		// x -= axesSize.x;
		xpos -= axesSize.x.start;

		if (xpos < 0) return -1;

		channel = (int) ((xpos / plotWidth) * data.size());
		return channel;

	}
	
	

	public TransitionSeriesType getType() {
		return type;
	}

	protected abstract List<TransitionSeries> getKeys(TransitionSeriesType type);
	protected abstract Map<TransitionSeries, Float> getData();
	protected abstract boolean isEmpty();
	protected abstract String getYAxisTitle();
	protected abstract String getTitle();
	protected abstract Function<Integer, String> getYAxisFormatter();
	protected abstract String getHighlightText(TransitionSeries ts);
	protected abstract List<DataLabel> getLabels(int lowest, int highest);
	
	
}
