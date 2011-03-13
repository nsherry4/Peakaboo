package peakaboo.ui.swing.plotting;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import commonenvironment.AbstractFile;
import commonenvironment.Env;

import eventful.EventfulTypeListener;
import fava.Fn;
import fava.datatypes.Bounds;
import fava.datatypes.Pair;
import fava.signatures.FnEach;
import fava.signatures.FnMap;

import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.painters.FittingMarkersPainter;
import peakaboo.curvefit.painters.FittingPainter;
import peakaboo.curvefit.painters.FittingSumPainter;
import peakaboo.curvefit.painters.FittingTitlePainter;
import peakaboo.filter.AbstractFilter;
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
import scitypes.Coord;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



/**
 * @author Nathaniel Sherry, 2009 This class creates a Canvas object which can be used to draw on. It implements
 *         Scrollable, and tracks a zoom property. It does not handle mouse events -- UIs wishing to handle mouse motion
 *         logic should add their own listeners
 */

public class PlotCanvas extends GraphicsPanel implements Scrollable
{

	private PlotDrawing				plot;
	public List<AxisPainter>		axisPainters;
	public DrawingRequest			dr;
	

	private PlotController			controller;

	private FnEach<Integer>	grabChannelFromClickCallback;


	public PlotCanvas(final PlotController controller, final PlotPanel parent)
	{

		super();


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
					axisPainters = null;
					updateCanvasSize();
				}
			}
		});
		
		
		//can't accept drag'n'drop if we're in a webstart session, since
		//there are security restrictions
		if (!Env.isWebStart())
		{

			new FileDrop(this, new FileDrop.Listener() {

				public void filesDropped(File[] files)
				{



					parent.loadFiles(
						Fn.map(files, new FnMap<File, AbstractFile>() {

							public AbstractFile f(File element)
							{
								return new AbstractFile(element);
							}
						})
						);



				}
			});

		}

		
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
				if (controller.dataController.hasDataSet() && grabChannelFromClickCallback != null){
					grabChannelFromClickCallback.f(channelFromCoordinate(e.getX()));
				}
			}
		});

	}


	public void grabChannelFromClick(FnEach<Integer> callback)
	{
		grabChannelFromClickCallback = callback;
	}






	public void updateCanvasSize()
	{
	
		double parentWidth = 1.0;
		if (this.getParent() != null)
		{
			parentWidth = this.getParent().getWidth();
		}

		int newWidth = (int) (controller.dataController.getDataWidth() * controller.settingsController.getZoom());
		if (newWidth < parentWidth) newWidth = (int) parentWidth;

		this.setPreferredSize(new Dimension(newWidth, 1));

		this.revalidate();

	}


	private int channelWidth(int multiplier)
	{
		return (int) Math.max(1.0f, Math.round(controller.settingsController.getZoom() * multiplier));
	}


	public int channelFromCoordinate(int x)
	{

		if (plot == null) return -1;

		Coord<Bounds<Float>> axesSize;
		int channel;

		// Plot p = new Plot(this.toyContext, model.dr);
		axesSize = plot.getPlotOffsetFromBottomLeft();

		float plotWidth = axesSize.x.end - axesSize.x.start; // width - axesSize.x;
		// x -= axesSize.x;
		x -= axesSize.x.start;

		if (x < 0 || !controller.dataController.hasDataSet()) return -1;

		channel = (int) ((x / plotWidth) * controller.dataController.datasetScanSize());
		return channel;

	}
	


	
	
	
	
	
	
	
	
	
	
	
	
	

	//**************************************************************
	// GraphicsPanel extension
	//**************************************************************
	@Override
	protected void drawGraphics(Surface context, boolean vector)
	{
		
		dr.imageHeight = getHeight();
		dr.imageWidth = getWidth();
		dr.viewTransform = controller.settingsController.getViewLog() ? ViewTransform.LOG : ViewTransform.LINEAR;
		dr.unitSize = controller.settingsController.getEnergyPerChannel();
		
		
		////////////////////////////////////////////////////////////////////
		// Data Calculation
		////////////////////////////////////////////////////////////////////

		// calculates filters and fittings if needed
		Pair<Spectrum, Spectrum> dataForPlot = controller.getDataForPlot();
		if (dataForPlot == null) return;
		
		//white background
		context.rectangle(0, 0, getWidth(), getHeight());
		context.setSource(Color.white);
		context.fill();

		////////////////////////////////////////////////////////////////////
		// Colour Selections
		////////////////////////////////////////////////////////////////////
		Color fitting, fittingStroke, fittingSum;
		Color proposed, proposedStroke, proposedSum;

		fitting = new Color(0.0f, 0.0f, 0.0f, 0.3f);
		fittingStroke = new Color(0.0f, 0.0f, 0.0f, 0.5f);
		fittingSum = new Color(0.0f, 0.0f, 0.0f, 0.8f);

		// Colour/Monochrome colours for curve fittings
		if (controller.settingsController.getMonochrome())
		{
			proposed = new Color(1.0f, 1.0f, 1.0f, 0.3f);
			proposedStroke = new Color(1.0f, 1.0f, 1.0f, 0.5f);
			proposedSum = new Color(1.0f, 1.0f, 1.0f, 0.8f);
		}
		else
		{
			proposed = new Color(0.64f, 0.0f, 0.0f, 0.3f);
			proposedStroke = new Color(0.64f, 0.0f, 0.0f, 0.5f);
			proposedSum = new Color(0.64f, 0.0f, 0.0f, 0.8f);
		}


		

		////////////////////////////////////////////////////////////////////
		// Plot Painters
		////////////////////////////////////////////////////////////////////

		// if axes are shown, also draw horizontal grid lines
		List<PlotPainter> plotPainters = new ArrayList<PlotPainter>();
		if (controller.settingsController.getShowAxes()) plotPainters.add(new GridlinePainter(new Bounds<Float>(
			0.0f,
			controller.dataController.maximumIntensity())));

		// draw the original data in the background
		if (controller.settingsController.getShowRawData())
		{
			Spectrum originalData = dataForPlot.second;
			plotPainters.add(new OriginalDataPainter(originalData, controller.settingsController.getMonochrome()));
		}

		// draw the filtered data
		final Spectrum drawingData = dataForPlot.first;
		plotPainters.add(new PrimaryPlotPainter(drawingData, controller.settingsController.getMonochrome()));

		// get any painters that the filters might want to add to the mix
		PlotPainter extension;
		for (AbstractFilter f : controller.filteringController.getActiveFilters())
		{
			extension = f.getPainter();
			if (extension != null && f.enabled) plotPainters.add(extension);
		}

		// draw curve fitting
		if (controller.settingsController.getShowIndividualSelections())
		{
			plotPainters.add(new FittingPainter(controller.fittingController.getFittingSelectionResults(), fittingStroke, fitting));
			plotPainters.add(new FittingSumPainter(controller.fittingController.getFittingSelectionResults().totalFit, fittingSum));
		}
		else
		{			
			plotPainters.add(new FittingSumPainter(controller.fittingController.getFittingSelectionResults().totalFit, fittingSum, fitting));
		}
		
		//draw curve fitting for proposed fittings
		if (controller.fittingController.getProposedTransitionSeries().size() > 0)
		{
			if (controller.settingsController.getShowIndividualSelections())
			{
				plotPainters.add(new FittingPainter(controller.fittingController.getFittingProposalResults(), proposedStroke, proposed));
			}
			else
			{
				plotPainters
					.add(new FittingSumPainter(controller.fittingController.getFittingProposalResults().totalFit, proposedStroke, proposed));
			}

			plotPainters.add(

				new FittingSumPainter(SpectrumCalculations.addLists(
						controller.fittingController.getFittingProposalResults().totalFit,
						controller.fittingController.getFittingSelectionResults().totalFit), proposedSum)

			);
		}
		

		plotPainters.add(new FittingTitlePainter(
				controller.fittingController.getFittingSelectionResults(),
				controller.settingsController.getShowElementTitles(),
				controller.settingsController.getShowElementIntensities(),
				fittingStroke
			)
		);
		
		plotPainters.add(new FittingTitlePainter(
				controller.fittingController.getFittingProposalResults(),
				controller.settingsController.getShowElementTitles(),
				controller.settingsController.getShowElementIntensities(),
				proposedStroke
			)
		);
		
		if (controller.settingsController.getShowElementMarkers()) {
			plotPainters.add(new FittingMarkersPainter(controller.fittingController.getFittingSelectionResults(), controller.settingsController.getEscapePeakType(), fittingStroke));
			plotPainters.add(new FittingMarkersPainter(controller.fittingController.getFittingProposalResults(), controller.settingsController.getEscapePeakType(), proposedStroke));
		}
				



		////////////////////////////////////////////////////////////////////
		// Axis Painters
		////////////////////////////////////////////////////////////////////

		if (axisPainters == null)
		{

			axisPainters = new ArrayList<AxisPainter>();

			if (controller.settingsController.getShowTitle())
			{
				axisPainters.add(new TitleAxisPainter(1.0f, null, null, controller.dataController.getDatasetName(), null));
			}

			if (controller.settingsController.getShowAxes())
			{

				axisPainters.add(new TitleAxisPainter(1.0f, "Relative Intensity", null, null, "Energy (keV)"));
				axisPainters.add(new TickMarkAxisPainter(
					new Bounds<Float>(0.0f, controller.dataController.maximumIntensity()),
					new Bounds<Float>(0.0f, dr.unitSize * controller.dataController.datasetScanSize()),
					null,
					new Bounds<Float>(0.0f, controller.dataController.maximumIntensity()),
					dr.viewTransform == ViewTransform.LOG,
					dr.viewTransform == ViewTransform.LOG));
				axisPainters.add(new LineAxisPainter(true, true, controller.settingsController.getShowTitle(), true));

			}

		}

		//if the filtered data somehow becomes taller than the maximum value from the raw data, we don't want to clip it.
		//but if the fitlered data gets weaker, we still want to scale it to the original data, so that its shrinking is obvious
		dr.maxYIntensity = Math.max(controller.dataController.maximumIntensity(), SpectrumCalculations.max(controller.filteringController.getFilteredPlot()));
		dr.dataWidth = controller.dataController.datasetScanSize();
		
		
		plot = new PlotDrawing(context, dr, plotPainters, axisPainters);
		plot.draw();
	}


	@Override
	public float getUsedHeight()
	{
		return getHeight();
	}


	@Override
	public float getUsedWidth()
	{
		return getWidth();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//**************************************************************
	// Scrollable Interface
	//**************************************************************
	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(600, 300);
	}


	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2)
	{
		return channelWidth(50);
	}


	public boolean getScrollableTracksViewportHeight()
	{
		return true;
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
