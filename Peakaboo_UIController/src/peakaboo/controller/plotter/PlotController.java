package peakaboo.controller.plotter;



import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


import peakaboo.controller.CanvasController;
import peakaboo.controller.mapper.MapController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.filtering.FilteringController;
import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.controller.plotter.settings.ChannelCompositeMode;
import peakaboo.controller.plotter.settings.SettingsController;
import peakaboo.controller.plotter.undo.UndoController;
import peakaboo.controller.settings.Settings;
import peakaboo.curvefit.painters.FittingMarkersPainter;
import peakaboo.curvefit.painters.FittingPainter;
import peakaboo.curvefit.painters.FittingSumPainter;
import peakaboo.curvefit.painters.FittingTitlePainter;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.filter.AbstractFilter;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.workers.PluralSet;
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
import scitypes.Coord;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


import eventful.EventfulListener;
import eventful.EventfulTypeListener;
import fava.datatypes.Bounds;
import fava.datatypes.Pair;




/**
 * This class is the controller for plot displays.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotController extends CanvasController
{
	
	
	private PlotDrawing						plot;
	public List<AxisPainter>				axisPainters;
	public DrawingRequest					dr;

	
	public UndoController					undoController;
	public MapController					mapController;
	public DataController					dataController;
	public FilteringController				filteringController;
	public FittingController				fittingController;
	public SettingsController				settingsController;


	public static enum UpdateType
	{
		DATA, FITTING, FILTER, UNDO, UI
	}
	
	public PlotController(Object toyContext)
	{
		super(toyContext);
		initPlotController();
	}

	private void initPlotController()
	{
		
		dr = new DrawingRequest();
		
		
		undoController = new UndoController(this);
		dataController = new DataController(this);
		filteringController = new FilteringController(this);
		fittingController = new FittingController(this);
		settingsController = new SettingsController(this);
		
		undoController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.UNDO.toString());
			}
		});
		
		dataController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.DATA.toString());
			}
		});
		
		filteringController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.FILTER.toString());
			}
		});
		
		fittingController.addListener(new EventfulTypeListener<Boolean>() {
			
			public void change(Boolean b)
			{
				updateListeners(UpdateType.FITTING.toString());
			}
		});
		
		settingsController.addListener(new EventfulListener() {
			
			public void change()
			{
				updateListeners(UpdateType.UI.toString());
			}
		});
		
		
		setUndoPoint("");
	}

	

	// =============================================
	// Functions for performing tasks which are not
	// described in a controller interface and which
	// do not fall easily into one category
	// =============================================
	
	public MapController getMapController()
	{
		return mapController;
	}

	public void setMapController(MapController mapController)
	{
		this.mapController = mapController;
	}
	
	public InputStream getSerializedPlotSettings()
	{
		//save the current state
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		savePreferences(baos);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		return bais;	
	}
	
	public void savePreferences(OutputStream outStream)
	{
		Settings.savePreferences(
				this, 
				settingsController.getSettingsModel(), 
				fittingController.getFittingModel(),
				filteringController.getFilteringMode(), 
				outStream
			);
	}

	public void loadPreferences(InputStream inStream, boolean isUndoAction)
	{
		Settings.loadPreferences(this, dataController,
				settingsController.getSettingsModel(), 
				fittingController.getFittingModel(),
				filteringController.getFilteringMode(),
				inStream
			);
		
		if (!isUndoAction) setUndoPoint("Load Session");
		filteringController.filteredDataInvalidated();
	}

	/**
	 * Get the scan that should currently be shown.
	 * @return a Spectrum which contains a scan
	 */
	private Spectrum currentScan()
	{
		Spectrum originalData = null;
		
		if (settingsController.getChannelCompositeType() == ChannelCompositeMode.AVERAGE) {
			originalData = dataController.getAveragePlot();
		} else if (settingsController.getChannelCompositeType()  == ChannelCompositeMode.MAXIMUM) {
			originalData = dataController.getMaximumPlot();
		} else {
			originalData = dataController.getScanAtIndex(settingsController.getScanNumber());
		}
		
		return originalData;
		
	}
	
	/**
	 * Returns a pair of spectra. The first one is the filtered data, the second is the original
	 */
	public Pair<Spectrum, Spectrum> getDataForPlot()
	{

		Spectrum originalData = null;

		if (!dataController.hasDataSet() || currentScan() == null) return null;

		
		// get the original data
		originalData = currentScan();

		regenerateCahcedData();
		
		return new Pair<Spectrum, Spectrum>(filteringController.getFilteredPlot(), originalData);
	}

	public void regenerateCahcedData()
	{

		// Regenerate Filtered Data
		if (dataController.hasDataSet() && currentScan() != null)
		{

			
			if (filteringController.getFilteredPlot() == null)
			{
				filteringController.calculateFilteredData(currentScan());
			}

			// Fitting Selections
			if (!fittingController.hasSelectionFitting())
			{
				fittingController.calculateSelectionFittings(filteringController.getFilteredPlot());
			}

			// Fitting Proposals
			if (!fittingController.hasProposalFitting())
			{
				fittingController.calculateProposalFittings();
			}

		}

	}
	
	public PluralSet<MapResultSet> TASK_getDataForMapFromSelectedRegions(FittingTransform type)
	{
		return dataController.TASK_calculateMap(filteringController.getActiveFilters(), fittingController.getFittingSelections(), type);
	}
	
	public void setUndoPoint(String s)
	{
		undoController.setUndoPoint(s);
	}
	
	
	
	
	
	
	
	
	
	
	// =============================================
	// Functions to extend CanvasController
	// =============================================
	@Override
	public void setOutputIsPDF(boolean isPDF)
	{
		dr.drawToVectorSurface = isPDF;

	}
	
	@Override
	public float getUsedHeight()
	{
		return getImageHeight();
	}

	@Override
	public float getUsedWidth()
	{
		return getImageWidth();
	}
	
	@Override
	protected void drawBackend(Surface backend, boolean scalar)
	{

		////////////////////////////////////////////////////////////////////
		// Data Calculation
		////////////////////////////////////////////////////////////////////

		// calculates filters and fittings if needed
		Pair<Spectrum, Spectrum> dataForPlot = getDataForPlot();
		if (dataForPlot == null) return;
		

		////////////////////////////////////////////////////////////////////
		// Colour Selections
		////////////////////////////////////////////////////////////////////
		Color fitting, fittingStroke, fittingSum;
		Color proposed, proposedStroke, proposedSum;

		fitting = new Color(0.0f, 0.0f, 0.0f, 0.3f);
		fittingStroke = new Color(0.0f, 0.0f, 0.0f, 0.5f);
		fittingSum = new Color(0.0f, 0.0f, 0.0f, 0.8f);

		// Colour/Monochrome colours for curve fittings
		if (settingsController.getMonochrome())
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
		List<PlotPainter> plotPainters = DataTypeFactory.<PlotPainter> list();
		if (settingsController.getShowAxes()) plotPainters.add(new GridlinePainter(new Bounds<Float>(
			0.0f,
			dataController.maximumIntensity())));

		// draw the original data in the background
		if (settingsController.getShowRawData())
		{
			Spectrum originalData = dataForPlot.second;
			plotPainters.add(new OriginalDataPainter(originalData, settingsController.getMonochrome()));
		}

		// draw the filtered data
		final Spectrum drawingData = dataForPlot.first;
		plotPainters.add(new PrimaryPlotPainter(drawingData, settingsController.getMonochrome()));

		// get any painters that the filters might want to add to the mix
		PlotPainter extension;
		for (AbstractFilter f : filteringController.getActiveFilters())
		{
			extension = f.getPainter();
			if (extension != null && f.enabled) plotPainters.add(extension);
		}

		// draw curve fitting
		if (settingsController.getShowIndividualSelections())
		{
			plotPainters.add(new FittingPainter(fittingController.getFittingSelectionResults(), fittingStroke, fitting));
			plotPainters.add(new FittingSumPainter(fittingController.getFittingSelectionResults().totalFit, fittingSum));
		}
		else
		{			
			plotPainters.add(new FittingSumPainter(fittingController.getFittingSelectionResults().totalFit, fittingSum, fitting));
		}
		
		//draw curve fitting for proposed fittings
		if (fittingController.getProposedTransitionSeries().size() > 0)
		{
			if (settingsController.getShowIndividualSelections())
			{
				plotPainters.add(new FittingPainter(fittingController.getFittingProposalResults(), proposedStroke, proposed));
			}
			else
			{
				plotPainters
					.add(new FittingSumPainter(fittingController.getFittingProposalResults().totalFit, proposedStroke, proposed));
			}

			plotPainters.add(

				new FittingSumPainter(SpectrumCalculations.addLists(
					fittingController.getFittingProposalResults().totalFit,
					fittingController.getFittingSelectionResults().totalFit), proposedSum)

			);
		}
		

		plotPainters.add(new FittingTitlePainter(
				fittingController.getFittingSelectionResults(),
				settingsController.getShowElementTitles(),
				settingsController.getShowElementIntensities(),
				fittingStroke
			)
		);
		
		plotPainters.add(new FittingTitlePainter(
				fittingController.getFittingProposalResults(),
				settingsController.getShowElementTitles(),
				settingsController.getShowElementIntensities(),
				proposedStroke
			)
		);
		
		if (settingsController.getShowElementMarkers()) {
			plotPainters.add(new FittingMarkersPainter(fittingController.getFittingSelectionResults(), settingsController.getEscapePeakType(), fittingStroke));
			plotPainters.add(new FittingMarkersPainter(fittingController.getFittingProposalResults(), settingsController.getEscapePeakType(), proposedStroke));
		}
				



		////////////////////////////////////////////////////////////////////
		// Axis Painters
		////////////////////////////////////////////////////////////////////

		if (axisPainters == null)
		{

			axisPainters = DataTypeFactory.<AxisPainter> list();

			if (settingsController.getShowTitle())
			{
				axisPainters.add(new TitleAxisPainter(1.0f, null, null, dataController.getDatasetName(), null));
			}

			if (settingsController.getShowAxes())
			{

				axisPainters.add(new TitleAxisPainter(1.0f, "Relative Intensity", null, null, "Energy (keV)"));
				axisPainters.add(new TickMarkAxisPainter(
					new Bounds<Float>(0.0f, dataController.maximumIntensity()),
					new Bounds<Float>(0.0f, dr.unitSize * dataController.datasetScanSize()),
					null,
					new Bounds<Float>(0.0f, dataController.maximumIntensity()),
					dr.viewTransform == ViewTransform.LOG,
					dr.viewTransform == ViewTransform.LOG));
				axisPainters.add(new LineAxisPainter(true, true, settingsController.getShowTitle(), true));

			}

		}

		//if the filtered data somehow becomes taller than the maximum value from the raw data, we don't want to clip it.
		//but if the fitlered data gets weaker, we still want to scale it to the original data, so that its shrinking is obvious
		dr.maxYIntensity = Math.max(dataController.maximumIntensity(), SpectrumCalculations.max(filteringController.getFilteredPlot()));
		dr.dataWidth = dataController.datasetScanSize();
		
		plot = new PlotDrawing(backend, dr, plotPainters, axisPainters);
		plot.draw();

	}	

	
	// =============================================
	// Helper Functions for CanvasController
	// =============================================
	
	public void axisSetInvalidated()
	{
		axisPainters = null;
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

		if (x < 0 || !dataController.hasDataSet()) return -1;

		channel = (int) ((x / plotWidth) * dataController.datasetScanSize());
		return channel;

	}
	
	public void setImageHeight(float height)
	{
		float oldHeight = dr.imageHeight;
		dr.imageHeight = height;
		if (height != oldHeight) updateListeners(UpdateType.UI.toString());
	}

	public float getImageHeight()
	{
		return dr.imageHeight;
	}

	public void setImageWidth(float width)
	{
		float oldWidth = dr.imageWidth;
		dr.imageWidth = width;
		if (width != oldWidth) updateListeners(UpdateType.UI.toString());
	}

	public float getImageWidth()
	{
		return dr.imageWidth;
	}


	
}
