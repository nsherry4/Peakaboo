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
import peakaboo.filter.model.Filter;
import peakaboo.ui.swing.plotting.fitting.painters.FittingMarkersPainter;
import peakaboo.ui.swing.plotting.fitting.painters.FittingPainter;
import peakaboo.ui.swing.plotting.fitting.painters.FittingSumPainter;
import peakaboo.ui.swing.plotting.fitting.painters.FittingTitlePainter;
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

	private PlotDrawing				plot;
	private DrawingRequest			dr;
	

	private PlotController			controller;

	private Consumer<Integer>		grabChannelFromClickCallback;


	PlotCanvas(final PlotController controller, final PlotPanel parent)
	{

		super();
		this.setFocusable(true);

		this.controller = controller;
		dr = new DrawingRequest();
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

		int newWidth = (int) (controller.data().getDataSet().getAnalysis().channelsPerScan() * controller.settings().getZoom());
		if (newWidth < parentWidth) newWidth = (int) parentWidth;

		
		
		//Height
		double parentHeight = 1.0;
		if (this.getParent() != null)
		{
			parentHeight = this.getParent().getHeight();
		}

		int newHeight = (int) (200 * controller.settings().getZoom());
		if (newHeight < parentHeight) newHeight = (int) parentHeight;
		
		if (controller.settings().getLockPlotHeight()) {
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
		return (int) Math.max(1.0f, Math.round(controller.settings().getZoom() * multiplier));
	}


	int channelFromCoordinate(int x)
	{

		if (plot == null) return -1;

		Coord<Bounds<Float>> axesSize;
		int channel;

		// Plot p = new Plot(this.toyContext, model.dr);
		axesSize = plot.getPlotOffsetFromBottomLeft();

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
			if (dataForPlot.first == null) {
				PeakabooLog.get().log(Level.WARNING, "Could not draw plot, dataForPlot (filtered) was null");
				return;
			};
			if (dataForPlot.second == null) {
				PeakabooLog.get().log(Level.WARNING, "Could not draw plot, dataForPlot (raw) was null");
				return;
			};
			
			//white background
			context.rectangle(0, 0, (float)size.getWidth(), (float)size.getHeight());
			context.setSource(Color.white);
			context.fill();
	
			////////////////////////////////////////////////////////////////////
			// Colour Selections
			////////////////////////////////////////////////////////////////////
			Color fitting, fittingStroke, fittingSum;
			Color proposed, proposedStroke, proposedSum;
			Color selected, selectedStroke;
	
			fitting = new Color(0.0f, 0.0f, 0.0f, 0.3f);
			fittingStroke = new Color(0.0f, 0.0f, 0.0f, 0.5f);
			fittingSum = new Color(0.0f, 0.0f, 0.0f, 0.8f);
	
			// Colour/Monochrome colours for curve fittings
			if (controller.settings().getMonochrome())
			{
				proposed = new Color(0x50ffffff, true);
				proposedStroke = new Color(0x80ffffff, true);
				proposedSum = new Color(0xD0ffffff, true);
			}
			else
			{
				proposed = new Color(0x80D32F2F, true);
				proposedStroke = new Color(0x80B71C1C, true);
				proposedSum = new Color(0xD0B71C1C, true);
			}
			
			// Colour/Monochrome colours for highlighted/selected fittings
			if (controller.settings().getMonochrome())
			{
				selected = new Color(0x50ffffff, true);
				selectedStroke = new Color(0x80ffffff, true);
			}
			else
			{
				selected = new Color(0x800288D1, true);
				selectedStroke = new Color(0xff01579B, true);
			}
	
	
			
	
			////////////////////////////////////////////////////////////////////
			// Plot Painters
			////////////////////////////////////////////////////////////////////
	
			//if the filtered data somehow becomes taller than the maximum value from the raw data, we don't want to clip it.
			//but if the fitlered data gets weaker, we still want to scale it to the original data, so that its shrinking is obvious
			ReadOnlySpectrum drawingData = dataForPlot.first;
			float maxIntensity = Math.max(controller.data().getDataSet().getAnalysis().maximumIntensity(), drawingData.max());
			int datasetSize = Math.min(controller.data().getDataSet().getAnalysis().channelsPerScan(), drawingData.size());
			
			dr.imageHeight = (float) size.getHeight();
			dr.imageWidth = (float) size.getWidth();
			dr.viewTransform = controller.settings().getViewLog() ? ViewTransform.LOG : ViewTransform.LINEAR;
			dr.unitSize = (controller.settings().getMaxEnergy() - controller.settings().getMinEnergy()) / (float)datasetSize;
			dr.drawToVectorSurface = context.isVectorSurface();
			
			// if axes are shown, also draw horizontal grid lines
			List<PlotPainter> plotPainters = new ArrayList<PlotPainter>();
			if (controller.settings().getShowAxes()) plotPainters.add(new GridlinePainter(new Bounds<Float>(
				0.0f,
				maxIntensity)));
	
	
			// draw the filtered data
			plotPainters.add(new PrimaryPlotPainter(drawingData, controller.settings().getMonochrome()));
	
			
			// draw the original data
			if (controller.settings().getShowRawData())
			{
				ReadOnlySpectrum originalData = dataForPlot.second;
				plotPainters.add(new OriginalDataPainter(originalData, controller.settings().getMonochrome()));
			}
			
			
			// get any painters that the filters might want to add to the mix
			PlotPainter filterPainter;
			for (Filter f : controller.filtering().getActiveFilters())
			{
				filterPainter = f.getPainter();
				
				if (filterPainter != null && f.isEnabled()) {
					filterPainter.setSourceName(f.getFilterName());
					plotPainters.add(filterPainter);
				}
			}
	
			// draw curve fitting
			if (controller.settings().getShowIndividualSelections())
			{
				plotPainters.add(new FittingPainter(controller.fitting().getFittingSelectionResults(), fittingStroke, fitting));
				plotPainters.add(new FittingSumPainter(controller.fitting().getFittingSelectionResults().getTotalFit(), fittingSum));
			}
			else
			{			
				plotPainters.add(new FittingSumPainter(controller.fitting().getFittingSelectionResults().getTotalFit(), fittingSum, fitting));
			}
			
			//draw curve fitting for proposed fittings
			if (controller.fitting().getProposedTransitionSeries().size() > 0)
			{
				if (controller.settings().getShowIndividualSelections()) {
					plotPainters.add(new FittingPainter(controller.fitting().getFittingProposalResults(), proposedStroke, proposed));
				} else {
					plotPainters.add(new FittingSumPainter(controller.fitting().getFittingProposalResults().getTotalFit(), proposedStroke, proposed));
				}

				plotPainters.add(
	
					new FittingSumPainter(SpectrumCalculations.addLists(
							controller.fitting().getFittingProposalResults().getTotalFit(),
							controller.fitting().getFittingSelectionResults().getTotalFit()), proposedSum)
	
				);
			}
			
	
			//highlighted fittings
			List<TransitionSeries> selectedFits = controller.fitting().getHighlightedTransitionSeries();
			if (!selectedFits.isEmpty()) {
				List<FittingResult> selectedFitResults = controller.fitting().getFittingSelectionResults().getFits()
						.stream()
						.filter(r -> selectedFits.contains(r.getTransitionSeries()))
						.collect(Collectors.toList());
				plotPainters.add(new FittingPainter(selectedFitResults, selectedStroke, selected));
			}
			
			
			plotPainters.add(new FittingTitlePainter(
					controller.fitting().getFittingSelectionResults(),
					controller.settings().getShowElementTitles(),
					controller.settings().getShowElementIntensities(),
					fittingStroke
				)
			);
			
			plotPainters.add(new FittingTitlePainter(
					controller.fitting().getFittingProposalResults(),
					controller.settings().getShowElementTitles(),
					controller.settings().getShowElementIntensities(),
					proposedStroke
				)
			);
			
			if (controller.settings().getShowElementMarkers()) {
				plotPainters.add(new FittingMarkersPainter(controller.fitting().getFittingSelectionResults(), controller.settings().getEscapePeakType(), fittingStroke));
				plotPainters.add(new FittingMarkersPainter(controller.fitting().getFittingProposalResults(), controller.settings().getEscapePeakType(), proposedStroke));
			}
					
	
	
	
			////////////////////////////////////////////////////////////////////
			// Axis Painters
			////////////////////////////////////////////////////////////////////
	
			//if (axisPainters == null)
			//{
			List<AxisPainter> axisPainters = new ArrayList<AxisPainter>();
	
			if (controller.settings().getShowTitle())
			{
				axisPainters.add(new TitleAxisPainter(1.0f, null, null, controller.data().getDataSet().getScanData().datasetName(), null));
			}
	
			if (controller.settings().getShowAxes())
			{
	
				axisPainters.add(new TitleAxisPainter(1.0f, "Relative Intensity", null, null, "Energy (keV)"));
				axisPainters.add(new TickMarkAxisPainter(
					new Bounds<Float>(0.0f, maxIntensity),
					new Bounds<Float>(controller.settings().getMinEnergy(), controller.settings().getMaxEnergy()),
					null,
					new Bounds<Float>(0.0f, maxIntensity),
					dr.viewTransform == ViewTransform.LOG,
					dr.viewTransform == ViewTransform.LOG));
				axisPainters.add(new LineAxisPainter(true, true, controller.settings().getShowTitle(), true));
	
			}
	
			//}
	
			
			dr.maxYIntensity = maxIntensity;
			dr.dataWidth = datasetSize;
			
			
			plot = new PlotDrawing(context, dr, plotPainters, axisPainters);
			plot.draw();
			
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
