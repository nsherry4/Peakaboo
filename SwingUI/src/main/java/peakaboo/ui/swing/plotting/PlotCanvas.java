package peakaboo.ui.swing.plotting;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.Scrollable;

import eventful.EventfulTypeListener;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.display.plot.Plot;
import peakaboo.display.plot.PlotData;
import peakaboo.display.plot.PlotSettings;
import peakaboo.display.plot.fitting.FittingMarkersPainter;
import peakaboo.display.plot.fitting.FittingPainter;
import peakaboo.display.plot.fitting.FittingSumPainter;
import peakaboo.display.plot.fitting.FittingTitlePainter;
import peakaboo.filter.model.Filter;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.ViewTransform;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.painters.axis.AxisPainter;
import scidraw.drawing.painters.axis.LineAxisPainter;
import scidraw.drawing.painters.axis.TitleAxisPainter;
import scidraw.drawing.plot.PlotDrawing;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.axis.GridlinePainter;
import scidraw.drawing.plot.painters.axis.TickMarkAxisPainter;
import scidraw.drawing.plot.painters.plot.OriginalDataPainter;
import scidraw.drawing.plot.painters.plot.PrimaryPlotPainter;
import scidraw.swing.GraphicsPanel;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;
import scitypes.SpectrumCalculations;



/**
 * @author Nathaniel Sherry, 2009 This class creates a Canvas object which can be used to draw on. It implements
 *         Scrollable, and tracks a zoom property. It does not handle mouse events -- UIs wishing to handle mouse motion
 *         logic should add their own listeners
 */

public class PlotCanvas extends GraphicsPanel implements Scrollable
{

	private PlotDrawing				plotDrawing;
	private PlotController			controller;
	private Consumer<Integer>		grabChannelFromClickCallback;


	PlotCanvas(final PlotController controller, final PlotPanel parent)
	{

		super();
		this.setFocusable(true);

		this.controller = controller;
		this.setMinimumSize(new Dimension(100, 100));

		//setCanvasSize();

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{
				if (
						s.equals(PlotController.UpdateType.UI.toString())
						||
						s.equals(PlotController.UpdateType.DATA.toString())
						||
						s.equals(PlotController.UpdateType.UNDO.toString())
					)
				{
					updateCanvasSize();
				}
			}
		});
		
		
		new FileDrop(this, files -> {
			parent.loadFiles(Arrays.asList(files).stream().map(File::toPath).collect(Collectors.toList()));
		});

		
		addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e)
			{}
			
		
			public void mousePressed(MouseEvent e)
			{}
			
		
			public void mouseExited(MouseEvent e)
			{}
			
		
			public void mouseEntered(MouseEvent e)
			{}
			
		
			public void mouseClicked(MouseEvent e)
			{
				if (controller.data().hasDataSet() && grabChannelFromClickCallback != null){
					grabChannelFromClickCallback.accept(channelFromCoordinate(e.getX()));
				}
				//Make the plot canvas focusable
				if (!PlotCanvas.this.hasFocus()) {
					PlotCanvas.this.requestFocus();
				}
			}
		});

	}


	public void grabChannelFromClick(Consumer<Integer> callback)
	{
		grabChannelFromClickCallback = callback;
	}





	private Dimension calculateCanvasSize() {
		//Width
		double parentWidth = 1.0;
		if (this.getParent() != null)
		{
			parentWidth = this.getParent().getWidth();
		}

		int newWidth = (int) (controller.data().getDataSet().getAnalysis().channelsPerScan() * controller.view().getZoom());
		if (newWidth < parentWidth) newWidth = (int) parentWidth;

		
		
		//Height
		double parentHeight = 1.0;
		if (this.getParent() != null)
		{
			parentHeight = this.getParent().getHeight();
		}

		int newHeight = (int) (200 * controller.view().getZoom());
		if (newHeight < parentHeight) newHeight = (int) parentHeight;
		
		if (controller.view().getLockPlotHeight()) {
			newHeight = (int) parentHeight;
		}
		
		//Generate new size
		Dimension newSize = new Dimension(newWidth, newHeight);
		
		return newSize;
	}

	void updateCanvasSize()
	{
		
		Dimension newSize = calculateCanvasSize();
		Rectangle oldView = this.getVisibleRect();
		Dimension oldSize = getPreferredSize();
		
		if (newSize.equals(oldSize)) {
			return;
		}
		
		Rectangle newView = new Rectangle(oldView);
		

		//Ratio of new size to old one.
		float dx = (float)newSize.width / (float)oldSize.width;
		float dy = (float)newSize.height / (float)oldSize.height;

		//Scale view by size ratio
		newView.x = (int) (oldView.x * dx);
		newView.y = (int) (oldView.y * dy);

		//Set new size and update
		this.setPreferredSize(newSize);
		this.revalidate();
		this.scrollRectToVisible(newView);

	}

	public void validate() {
		updateCanvasSize();
		super.validate();
	}
	

	private int channelWidth(int multiplier)
	{
		return (int) Math.max(1.0f, Math.round(controller.view().getZoom() * multiplier));
	}


	int channelFromCoordinate(int x)
	{

		if (plotDrawing == null) return -1;

		Coord<Bounds<Float>> axesSize;
		int channel;

		// Plot p = new Plot(this.toyContext, model.dr);
		axesSize = plotDrawing.getPlotOffsetFromBottomLeft();

		float plotWidth = axesSize.x.end - axesSize.x.start; // width - axesSize.x;
		// x -= axesSize.x;
		x -= axesSize.x.start;

		if (x < 0 || !controller.data().hasDataSet()) return -1;

		channel = (int) ((x / plotWidth) * controller.data().getDataSet().getAnalysis().channelsPerScan());
		return channel;

	}
	


	
	
	
	
	
	
	
	
	
	
	
	
	

	//**************************************************************
	// GraphicsPanel extension
	//**************************************************************
	@Override
	protected void drawGraphics(Surface context, boolean vector, Dimension size)
	{
				
		try {
			
			
			

			
			////////////////////////////////////////////////////////////////////
			// Data Calculation
			////////////////////////////////////////////////////////////////////
	
			// calculates filters and fittings if needed
			Pair<ReadOnlySpectrum, ReadOnlySpectrum> dataForPlot = controller.getDataForPlot();
			if (dataForPlot == null) {
				return;
			}
			

			
			
			PlotData data = new PlotData();
			
			data.selectionResults = controller.fitting().getFittingSelectionResults();
			data.proposedResults = controller.fitting().getFittingProposalResults();
			data.calibration = controller.fitting().getEnergyCalibration();
			data.escape = controller.fitting().getEscapeType();
			data.highlightedTransitionSeries = controller.fitting().getHighlightedTransitionSeries();
			data.proposedTransitionSeries = controller.fitting().getProposedTransitionSeries();
			
			data.dataset = controller.data().getDataSet();
			
			data.filters = controller.filtering().getActiveFilters();
			
			data.filtered = dataForPlot.first;
			data.raw = dataForPlot.second;
			
			

			PlotSettings settings = new PlotSettings();
			
			settings.backgroundShowOriginal = controller.view().getShowRawData();
			settings.monochrome = controller.view().getMonochrome();
			settings.showAxes = controller.view().getShowAxes();
			settings.showElementFitIntensities = controller.view().getShowElementIntensities();
			settings.showElementFitMarkers = controller.view().getShowElementMarkers();
			settings.showElementFitTitles = controller.view().getShowElementTitles();
			settings.showIndividualFittings = controller.view().getShowIndividualSelections();
			settings.showPlotTitle = controller.view().getShowTitle();
			settings.viewTransform = controller.view().getViewLog() ? ViewTransform.LOG : ViewTransform.LINEAR;
			
			
			Plot plotObject = new Plot();
			plotDrawing = plotObject.draw(data, settings, context, size);
	
			
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to draw plot", e);
			throw e;
		}
	}


	@Override
	public float getUsedHeight()
	{
		return getUsedHeight(1);
	}


	@Override
	public float getUsedWidth()
	{
		return getUsedWidth(1);
	}
	
	@Override
	public float getUsedWidth(float zoom) {
		return getWidth() * zoom;
	}


	@Override
	public float getUsedHeight(float zoom) {
		return getHeight() * zoom;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//**************************************************************
	// Scrollable Interface
	//**************************************************************
	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(600, 1000);
	}


	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2)
	{
		return channelWidth(50);
	}


	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}


	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}


	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2)
	{
		return channelWidth(5);
	}



	
	
	
	



}
