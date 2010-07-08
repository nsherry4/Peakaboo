package peakaboo.controller.plotter;



import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import peakaboo.controller.CanvasController;
import peakaboo.controller.mapper.MapController;
import peakaboo.controller.settings.Settings;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.curvefit.painters.FittingMarkersPainter;
import peakaboo.curvefit.painters.FittingPainter;
import peakaboo.curvefit.painters.FittingSumPainter;
import peakaboo.curvefit.painters.FittingTitlePainter;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.dataset.provider.implementations.OnDemandDataSetProvider;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.PeakTable;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.filters.AbstractFilter;
import peakaboo.mapping.results.MapResultSet;
import scidraw.drawing.ViewTransform;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.painters.axis.AxisPainter;
import scidraw.drawing.painters.axis.LineAxisPainter;
import scidraw.drawing.painters.axis.TitleAxisPainter;
import scidraw.drawing.plot.PlotDrawing;
import scidraw.drawing.plot.PlotDrawingRequestFactory;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.axis.GridlinePainter;
import scidraw.drawing.plot.painters.axis.TickMarkAxisPainter;
import scidraw.drawing.plot.painters.plot.OriginalDataPainter;
import scidraw.drawing.plot.painters.plot.PrimaryPlotPainter;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;
import swidget.dialogues.fileio.AbstractFile;

import fava.Fn;
import fava.Functions;
import fava.datatypes.Bounds;
import fava.datatypes.Pair;
import fava.lists.FList;
import fava.signatures.FunctionMap;
import static fava.Fn.*;



/**
 * This class is the controller for plot displays.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotController extends CanvasController implements FilterController, FittingController,
		SettingsController, UndoController
{

	private PlotModel						model;
	private MapController					mapController;
	private PlotDrawing						plot;

	private List<AxisPainter>				axisPainters;

	private Stack<ByteArrayOutputStream>	undoStack;
	private Stack<ByteArrayOutputStream>	redoStack;


	public PlotController(Object toyContext)
	{
		super(toyContext);
		initPlotController();
	}


	private void initPlotController()
	{
		model = new PlotModel();

		model.dr = PlotDrawingRequestFactory.getDrawingRequest();
		model.viewOptions.channelComposite = ChannelCompositeMode.NONE;

		undoStack = new Stack<ByteArrayOutputStream>();
		redoStack = new Stack<ByteArrayOutputStream>();
		setUndoPoint();
	}


	public MapController getMapController()
	{
		return mapController;
	}


	public void setMapController(MapController mapController)
	{
		this.mapController = mapController;
	}


	// =============================================
	// DATA/UNIT CONVERSION FUNCTIONS
	// =============================================
	public float getEnergyForChannel(float channel)
	{
		if (!model.dataset.hasData()) return 0.0f;
		return channel * model.dr.unitSize;
	}


	public Pair<Float, Float> getValueForChannel(int channel)
	{
		if (channel == -1) return null;
		if (channel >= model.dataset.scanSize()) return null;

		Pair<Spectrum, Spectrum> scans = getDataForPlot();
		if (scans == null) return new Pair<Float, Float>(0.0f, 0.0f);

		return new Pair<Float, Float>(scans.first.get(channel), scans.second.get(channel));
	}


	// =============================================
	// PREFERENCES SERIALIZATION
	// =============================================
	public void savePreferences(OutputStream outStream)
	{
		Settings.savePreferences(model, outStream);
	}


	public void loadPreferences(InputStream inStream, boolean isUndoAction)
	{
		Settings.loadPreferences(model, inStream);
		if (!isUndoAction) setUndoPoint();
		filteredDataInvalidated();
	}


	// =============================================
	// DATA SET SETTINGS --
	// =============================================

	public void setDataSetProvider(DataSetProvider dsp)
	{

		if (dsp == null) return;

		DataSetProvider old = model.dataset;
		model.dataset = dsp;

		model.viewOptions.scanNumber = dsp.firstNonNullScanIndex();


		setDataWidth(model.dataset.scanSize());
		setDataHeight(1);

		setFittingParameters(model.dataset.scanSize(), model.dataset.energyPerChannel());
		// clear any interpolation from previous use
		if (mapController != null) mapController.setInterpolation(0);

		clearUndos();

		// really shouldn't have to do this, but there is a reference to old datasets floating around somewhere
		// (task listener?) which is preventing them from being garbage-collected
		if (old != null && old != dsp) old.discard();

		updateListeners();

	}


	public TaskList<Boolean> TASK_readFileListAsDataset(final List<AbstractFile> files)
	{

		//final LocalDataSetProvider dataset = new LocalDataSetProvider();
		final OnDemandDataSetProvider dataset = new OnDemandDataSetProvider();
		final TaskList<Boolean> readTasks = dataset.TASK_readFileListAsDataset(files);


		readTasks.addListener(new PeakabooSimpleListener() {

			public void change()
			{
				if (readTasks.getCompleted())
				{
					if (dataset.scanSize() > 0)
					{

						setDataSetProvider(dataset);

					}
				}
			}

		});

		return readTasks;

	}


	public String getDatasetName()
	{
		return model.dataset.getDatasetName();
	}


	public String getDataSourceFolder()
	{
		return model.dataset.getDataSourcePath();
	}


	public boolean hasDataSet()
	{
		return model.dataset.hasData();
	}


	public boolean hasDimensions()
	{
		return model.dataset.hasDimensions();
	}


	public int datasetScanCount()
	{
		if (!model.dataset.hasData()) return 0;
		return model.dataset.scanCount();
	}


	public int datasetScanSize()
	{
		if (!model.dataset.hasData()) return 0;
		return model.dataset.scanSize();
	}


	public Coord<Bounds<Number>> getRealDimensions()
	{
		return model.dataset.getRealDimensions();
	}


	public SISize getRealDimensionsUnits()
	{
		return model.dataset.getRealDimensionsUnits();
	}


	public Coord<Integer> getDataDimensions()
	{
		return model.dataset.getDataDimensions();
	}


	// data height and width
	private void setDataHeight(int height)
	{
		model.dr.dataHeight = height;
		updateListeners();
	}


	public int getDataHeight()
	{
		return model.dr.dataHeight;
	}


	private void setDataWidth(int width)
	{
		model.dr.dataWidth = width;
		updateListeners();
	}


	public int getDataWidth()
	{
		return model.dr.dataWidth;
	}


	// image height and width
	public void setImageHeight(float height)
	{
		float oldHeight = model.dr.imageHeight;
		model.dr.imageHeight = height;
		if (height != oldHeight) updateListeners();
	}


	public float getImageHeight()
	{
		return model.dr.imageHeight;
	}


	public void setImageWidth(float width)
	{
		float oldWidth = model.dr.imageWidth;
		model.dr.imageWidth = width;
		if (width != oldWidth) updateListeners();
	}


	public float getImageWidth()
	{
		return model.dr.imageWidth;
	}


	private void setFittingParameters(int scanSize, float energyPerChannel)
	{

		model.dr.unitSize = energyPerChannel;
		model.fittingSelections.setDataParameters(scanSize, energyPerChannel);
		model.fittingProposals.setDataParameters(scanSize, energyPerChannel);

		setUndoPoint();
		filteredDataInvalidated();
	}


	public String getCurrentScanName()
	{
		return model.dataset.getScanName(getScanNumber());
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

		if (x < 0 || !model.dataset.hasData()) return -1;

		channel = (int) ((x / plotWidth) * model.dataset.scanSize());
		return channel;

	}


	public boolean getScanDiscarded(int scanNo)
	{
		return (model.badScans.indexOf(scanNo) != -1);
	}


	public boolean getScanDiscarded()
	{
		return getScanDiscarded(getScanNumber());
	}


	public void setScanDiscarded(int scanNo, boolean discarded)
	{

		if (discarded)
		{
			if (!getScanDiscarded(scanNo)) model.badScans.add(scanNo);
			filteredDataInvalidated();
		}
		else
		{
			if (getScanDiscarded(scanNo)) model.badScans.remove(model.badScans.indexOf(scanNo));
			filteredDataInvalidated();
		}

		setUndoPoint();

	}


	public void setScanDiscarded(boolean discarded)
	{
		setScanDiscarded(getScanNumber(), discarded);
	}


	public List<Integer> getDiscardedScanList()
	{
		return DataTypeFactory.<Integer> listInit(model.badScans);
	}


	// =============================================
	// DRAWING COMMANDS
	// =============================================

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
		if (model.viewOptions.monochrome)
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
		if (model.viewOptions.showAxes) plotPainters.add(new GridlinePainter(new Bounds<Float>(
			0.0f,
			model.dataset.maximumIntensity())));

		// draw the original data in the background
		if (model.viewOptions.backgroundShowOriginal)
		{
			Spectrum originalData = dataForPlot.second;
			plotPainters.add(new OriginalDataPainter(originalData, model.viewOptions.monochrome));
		}

		// draw the filtered data
		final Spectrum drawingData = dataForPlot.first;
		plotPainters.add(new PrimaryPlotPainter(drawingData, model.viewOptions.monochrome));

		// get any painters that the filters might want to add to the mix
		PlotPainter extension;
		for (AbstractFilter f : model.filters)
		{
			extension = f.getPainter();
			if (extension != null && f.enabled) plotPainters.add(extension);
		}

		// draw curve fitting
		if (model.viewOptions.showIndividualFittings)
		{
			plotPainters.add(new FittingPainter(model.fittingSelectionResults, fittingStroke, fitting));
			plotPainters.add(new FittingSumPainter(model.fittingSelectionResults.totalFit, fittingSum));
		}
		else
		{
			plotPainters.add(new FittingSumPainter(model.fittingSelectionResults.totalFit, fittingSum, fitting));
		}
		if (getProposedTransitionSeries().size() > 0)
		{
			if (model.viewOptions.showIndividualFittings)
			{
				plotPainters.add(new FittingPainter(model.fittingProposalResults, proposedStroke, proposed));
			}
			else
			{
				plotPainters
					.add(new FittingSumPainter(model.fittingProposalResults.totalFit, proposedStroke, proposed));
			}

			plotPainters.add(

			new FittingSumPainter(SpectrumCalculations.addLists(
					model.fittingProposalResults.totalFit,
					model.fittingSelectionResults.totalFit), proposedSum)

			);
		}

		plotPainters.add(new FittingTitlePainter(
			model.fittingSelectionResults,
			model.viewOptions.showElementFitTitles,
			model.viewOptions.showElementFitIntensities));
		if (model.viewOptions.showElementFitMarkers) plotPainters.add(new FittingMarkersPainter(
			model.fittingSelectionResults));










		////////////////////////////////////////////////////////////////////
		// Axis Painters
		////////////////////////////////////////////////////////////////////

		if (axisPainters == null)
		{

			axisPainters = DataTypeFactory.<AxisPainter> list();

			if (model.viewOptions.showPlotTitle)
			{
				axisPainters.add(new TitleAxisPainter(1.0f, null, null, getDatasetName(), null));
			}

			if (model.viewOptions.showAxes)
			{

				axisPainters.add(new TitleAxisPainter(1.0f, "Relative Intensity", null, null, "Energy (keV)"));
				axisPainters.add(new TickMarkAxisPainter(
					new Bounds<Float>(0.0f, model.dataset.maximumIntensity()),
					new Bounds<Float>(0.0f, model.dr.unitSize * model.dataset.scanSize()),
					null,
					new Bounds<Float>(0.0f, model.dataset.maximumIntensity()),
					model.dr.viewTransform == ViewTransform.LOG,
					model.dr.viewTransform == ViewTransform.LOG));
				axisPainters.add(new LineAxisPainter(true, true, model.viewOptions.showPlotTitle, true));

			}

		}

		model.dr.maxYIntensity = model.dataset.maximumIntensity();

		plot = new PlotDrawing(backend, model.dr, plotPainters, axisPainters);
		plot.draw();

	}


	public void axisSetInvalidated()
	{
		axisPainters = null;
	}


	// =============================================
	// DATA RETRIEVAL FOR PLOTS AND MAPS
	// =============================================
	/**
	 * Returns a pair of spectra. The first one is the filtered data, the second is the original
	 */
	private Pair<Spectrum, Spectrum> getDataForPlot()
	{

		Spectrum originalData = null;

		if (!model.dataset.hasData() || model.currentScan() == null) return null;

		// get the original data
		// TODO: get rid of or change caching. the ScanContainers will not always return good data
		// they will return no data with the hasData flag set to false when the data needs to be fetched
		// over the network. In these cases, we should just display a "Loading..." message and register a
		// listener with the dataset asking to be notified when the data comes back. We will then repaint.
		originalData = model.currentScan();

		regenerateCahcedData();

		/*
		 * // if the filtered data has been invalidated, regenerate it if (model.filteredPlot == null) {
		 * model.filteredPlot = model.filters.filterData(originalData, true); }
		 * 
		 * // recalculate fittings if (model.fittingSelectionResults == null) { model.fittingSelectionResults =
		 * model.fittingSelections.calculateFittings(model.filteredPlot); }
		 * 
		 * if (model.fittingProposalResults == null) { model.fittingProposalResults = model.fittingProposals
		 * .calculateFittings(model.fittingSelectionResults.residual); }
		 */

		// return the filtered data and the infiltered data in a pair
		return new Pair<Spectrum, Spectrum>(model.filteredPlot, originalData);
	}


	public void regenerateCahcedData()
	{

		// Regenerate Filtered Data
		if (model.dataset.hasData() && model.currentScan() != null)
		{

			if (model.filteredPlot == null)
			{

				Spectrum originalData = null;

				// get the original data
				// TODO: get rid of or change caching. the ScanContainers will not always return good data
				// they will return no data with the hasData flag set to false when the data needs to be fetched
				// over the network. In these cases, we should just display a "Loading..." message and register a
				// listener with the dataset asking to be notified when the data comes back. We will then repaint.
				originalData = model.currentScan();

				// if the filtered data has been invalidated, regenerate it
				model.filteredPlot = model.filters.filterData(originalData, true);

			}

			// Fitting Selections
			if (model.fittingSelectionResults == null)
			{
				model.fittingSelectionResults = model.fittingSelections.calculateFittings(model.filteredPlot);
			}

			// Fitting Proposals
			if (model.fittingProposalResults == null)
			{
				model.fittingProposalResults = model.fittingProposals
					.calculateFittings(model.fittingSelectionResults.residual);
			}

		}

	}


	public TaskList<MapResultSet> TASK_getDataForMapFromSelectedRegions()
	{
		return model.dataset.calculateMap(model.filters, model.fittingSelections);
	}


	@Override
	public void setOutputIsPDF(boolean isPDF)
	{
		model.dr.drawToVectorSurface = isPDF;

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


	public boolean getScanHasExtendedInformation()
	{
		if (model.dataset == null) return false;
		return model.dataset.hasExtendedInformation();
	}


	public String getScanCreationTime()
	{
		if (model.dataset == null) return null;
		return model.dataset.getCreationTime();
	}


	public String getScanCreator()
	{
		if (model.dataset == null) return null;
		return model.dataset.getCreator();
	}


	public String getScanEndTime()
	{
		if (model.dataset == null) return null;
		return model.dataset.getEndTime();
	}


	public String getScanExperimentName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getExperimentName();
	}


	public String getScanFacilityName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getFacilityName();
	}


	public String getScanInstrumentName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getInstrumentName();
	}


	public String getScanLaboratoryName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getLaboratoryName();
	}


	public String getScanProjectName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getProjectName();
	}


	public String getScanSampleName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getSampleName();
	}


	public String getScanScanName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getScanName();
	}


	public String getScanSessionName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getSessionName();
	}


	public String getScanStartTime()
	{
		if (model.dataset == null) return null;
		return model.dataset.getStartTime();
	}


	public String getScanTechniqueName()
	{
		if (model.dataset == null) return null;
		return model.dataset.getTechniqueName();
	}


	// =============================================
	// FILTER FUNCTIONS TO IMPLEMENT FilterController
	// =============================================
	public void clearFilters()
	{
		model.filters.clearFilters();
		setUndoPoint();
		filteredDataInvalidated();
	}


	public List<String> getAvailableFiltersByName()
	{
		List<String> filterNames = DataTypeFactory.<String> list();

		for (AbstractFilter filter : model.filters.getAvailableFilters())
		{
			filterNames.add(filter.getFilterName());
		}

		Collections.sort(filterNames);

		return filterNames;
	}


	public List<AbstractFilter> getAvailableFilters()
	{
		return model.filters.getAvailableFilters();
	}


	public void addFilter(String name)
	{

		for (AbstractFilter f : model.filters.getAvailableFilters())
		{
			if (f.getFilterName().equals(name))
			{

				try
				{
					// this will call filterschanged, so we don't need to
					// manually update the listeners
					addFilter(f.getClass().newInstance());
					setUndoPoint();
					break;
				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}

			}
		}

	}


	public void addFilter(AbstractFilter f)
	{
		model.filters.addFilter(f);
		setUndoPoint();
		filteredDataInvalidated();
	}


	public void removeFilter(int index)
	{
		model.filters.removeFilter(index);
		setUndoPoint();
		filteredDataInvalidated();
	}


	public boolean filterSetContains(AbstractFilter f)
	{
		return model.filters.contains(f);
	}


	public int getFilterCount()
	{
		return model.filters.size();
	}


	public void setFilterEnabled(int index, boolean enabled)
	{
		model.filters.setFilterEnabled(index, enabled);
		setUndoPoint();
		filteredDataInvalidated();
	}


	public boolean getFilterEnabled(int index)
	{
		return model.filters.getFilterEnabled(index);
	}


	public void moveFilterUp(int index)
	{
		model.filters.moveFilterUp(index);
		setUndoPoint();
		filteredDataInvalidated();
	}


	public void moveFilterDown(int index)
	{
		model.filters.moveFilterDown(index);
		setUndoPoint();
		filteredDataInvalidated();
	}


	public AbstractFilter getFilter(int index)
	{
		return model.filters.getFilter(index);
	}


	public int filterIndex(AbstractFilter f)
	{
		return model.filters.indexOf(f);
	}


	public void filteredDataInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		model.filteredPlot = null;
		model.dataset.invalidateFilteredData();

		// a convenient place to clear the axis painters, since values such as max intensity will have changed
		axisSetInvalidated();

		// this will call updateListeners for us
		// invalidate any fitting data that may have been based on this
		fittingDataInvalidated();

	}


	// =============================================
	// FITTING FUNCTIONS TO IMPLEMENT FittingController
	// =============================================
	public void addTransitionSeries(TransitionSeries e)
	{
		if (e == null) return;
		model.fittingSelections.addTransitionSeries(e);
		setUndoPoint();
		fittingDataInvalidated();
	}


	public void addAllTransitionSeries(List<TransitionSeries> tss)
	{
		for (TransitionSeries ts : tss)
		{
			model.fittingSelections.addTransitionSeries(ts);
		}
		setUndoPoint();
		fittingDataInvalidated();
	}


	public void clearTransitionSeries()
	{
		model.fittingSelections.clear();
		setUndoPoint();
		fittingDataInvalidated();
	}


	public void removeTransitionSeries(TransitionSeries e)
	{
		model.fittingSelections.remove(e);
		setUndoPoint();
		fittingDataInvalidated();
	}


	public List<TransitionSeries> getFittedTransitionSeries()
	{
		return model.fittingSelections.getFittedTransitionSeries();
	}


	public List<TransitionSeries> getUnfittedTransitionSeries(final TransitionSeriesType tst)
	{

		final List<TransitionSeries> fitted = getFittedTransitionSeries();


		return filter(model.peakTable.getAllTransitionSeries(), new FunctionMap<TransitionSeries, Boolean>() {


			public Boolean f(TransitionSeries ts)
			{
				return (!fitted.contains(ts)) && tst.equals(ts.type);
			}
		});

	}


	public void setTransitionSeriesVisibility(TransitionSeries e, boolean show)
	{
		model.fittingSelections.setTransitionSeriesVisibility(e, show);
		setUndoPoint();
		fittingDataInvalidated();
	}


	public boolean getTransitionSeriesVisibility(TransitionSeries e)
	{
		return e.visible;
	}


	public List<TransitionSeries> getVisibleTransitionSeries()
	{

		return filter(getFittedTransitionSeries(), new FunctionMap<TransitionSeries, Boolean>() {


			public Boolean f(TransitionSeries ts)
			{
				return ts.visible;
			}
		});

	}


	public List<TransitionSeriesType> getTransitionSeriesTypesForElement(Element e, boolean onlyInEnergyRange)
	{

		List<TransitionSeries> tsl = model.fittingSelections.getFittedTransitionSeries();
		List<TransitionSeriesType> tst = DataTypeFactory.<TransitionSeriesType> list();

		for (TransitionSeries ts : tsl)
		{

			if (ts.element == e)
			{
				if (ts.getLowestEnergyValue() < 50 + (model.dr.unitSize * model.dr.dataWidth) || !onlyInEnergyRange)
				{
					tst.add(ts.type);
				}
			}

		}

		return tst;

	}


	public TransitionSeries getTransitionSeriesForElement(Element e, TransitionSeriesType tst)
	{
		List<TransitionSeries> tsl = model.fittingSelections.getFittedTransitionSeries();

		for (TransitionSeries ts : tsl)
		{
			if (ts.element == e && ts.type == tst) return ts;
		}

		return null;

	}


	public float getTransitionSeriesIntensity(TransitionSeries ts)
	{
		regenerateCahcedData();

		if (model.fittingSelectionResults == null) return 0.0f;

		for (FittingResult result : model.fittingSelectionResults.fits)
		{
			if (result.transitionSeries == ts) return result.scaleFactor;
		}
		return 0.0f;

	}


	public float getTransitionSeriesIntensityForElement(Element e, TransitionSeriesType tst)
	{
		regenerateCahcedData();

		if (model.fittingSelectionResults == null) return 0.0f;

		for (FittingResult result : model.fittingSelectionResults.fits)
		{
			if (result.transitionSeries.element == e && result.transitionSeries.type == tst) return result.scaleFactor;
		}
		return 0.0f;

	}


	public float getIntensityForElement(Element e)
	{
		regenerateCahcedData();

		if (model.fittingSelectionResults == null) return 0.0f;

		float intensity = 0.0f;
		for (FittingResult result : model.fittingSelectionResults.fits)
		{
			if (result.transitionSeries.element == e && result.scaleFactor > intensity) intensity = result.scaleFactor;
		}
		return intensity;

	}


	public void moveTransitionSeriesUp(TransitionSeries e)
	{
		model.fittingSelections.moveTransitionSeriesUp(e);
		setUndoPoint();
		fittingDataInvalidated();
	}


	public void moveTransitionSeriesDown(TransitionSeries e)
	{
		model.fittingSelections.moveTransitionSeriesDown(e);
		setUndoPoint();
		fittingDataInvalidated();
	}


	public void fittingDataInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		model.fittingSelectionResults = null;

		// this will call update listener for us
		fittingProposalsInvalidated();

	}


	// for showing potential elements - helps the user decide
	public void addProposedTransitionSeries(TransitionSeries e)
	{
		model.fittingProposals.addTransitionSeries(e);
		fittingProposalsInvalidated();
	}


	public void removeProposedTransitionSeries(TransitionSeries e)
	{
		model.fittingProposals.remove(e);
		fittingProposalsInvalidated();
	}


	public void clearProposedTransitionSeries()
	{
		model.fittingProposals.clear();
		fittingProposalsInvalidated();
	}


	public List<TransitionSeries> getProposedTransitionSeries()
	{
		return model.fittingProposals.getFittedTransitionSeries();
	}


	public void commitProposedTransitionSeries()
	{
		for (TransitionSeries e : model.fittingProposals.getFittedTransitionSeries())
		{
			model.fittingSelections.addTransitionSeries(e);
		}
		model.fittingProposals.clear();
		setUndoPoint();
		fittingDataInvalidated();
	}


	public void fittingProposalsInvalidated()
	{
		// Clear cached values, since they now have to be recalculated
		model.fittingProposalResults = null;
		updateListeners();
	}


	public List<TransitionSeries> proposeTransitionSeriesFromChannel(final int channel, TransitionSeries currentTS)
	{

		/*
		 * 
		 * Method description
		 * ------------------
		 * 
		 * We try to figure out which Transition Series are the best fit for the given channel.
		 * This is done in the following steps
		 * 
		 * 1. If we have suggested a TS previously, it should be passed in in currentTS
		 * 2. If currentTS isn't null, we remove it from the proposals, refit, and then readd it
		 * 		* we do this so that we can still suggest that same TS this time, otherwise, there would be no signal for it to fit
		 * 3. We get all TSs from the peak table, and add all summations of all fitted & proposed TSs
		 * 4. We remove all TSs which are already fitted or proposed.
		 * 5. We add currentTS to the list, since the last step will have removed it
		 * 6. We unique the list, don't want duplicates showing up
		 * 7. We sort by proximity, and take the top 15
		 * 8. We sort by a more detailed scoring function which involves fitting each TS and seeing how well it fits
		 * 9. We return the top 5 from the list in the last step
		 * 
		 */
		
		
		
		//remove the current transitionseries from the list of proposed trantision series so we can re-suggest it.
		//otherwise, the copy getting fitted eats all the signal from the one we would suggest during scoring
		boolean currentTSisUsed = currentTS != null && getProposedTransitionSeries().contains(currentTS);
		if (currentTSisUsed) removeProposedTransitionSeries(currentTS);
		regenerateCahcedData();
		
		final Spectrum s = model.fittingProposalResults.residual;
		
		if (currentTSisUsed) addProposedTransitionSeries(currentTS);
		regenerateCahcedData();
		
		
		
		
		final float energyPerChannel = getEnergyPerChannel();
		final float energy = channel * energyPerChannel;
		
		
		//scoring function to evaluate each TransitionSeries
		final FunctionMap<TransitionSeries, Float> score = new FunctionMap<TransitionSeries, Float>() {

			public Float f(TransitionSeries ts)
			{
				Double prox = Math.abs(ts.getProximityToEnergy(energy));
				if (prox <= 0.001) prox = 0.001;
				
				
				TransitionSeriesFitting tsf = new TransitionSeriesFitting(ts, getDataWidth(), energyPerChannel, FittingSet.escape);
				Float ratio = tsf.getRatioForCurveUnderData(s);
				ratio = (float) Math.pow(ratio, 2);
				
				return (ratio / prox.floatValue()) / tsf.getSizeOfBase();
			}
		};


		//get a list of all transition series to start with
		FList<TransitionSeries> tss = Fn.map(model.peakTable.getAllTransitionSeries(), Functions
			.<TransitionSeries> id());

		
		//add in any 2x summations from the list of previously fitted AND proposed peaks.
		//we exclude any that the caller requests so that if a UI component is *replacing* a TS with
		//these suggestions, it doesn't get summations for the now-removed TS
		List<TransitionSeries> summationCandidates = getFittedTransitionSeries();
		summationCandidates.addAll(getProposedTransitionSeries());
		if (currentTSisUsed) summationCandidates.remove(currentTS);
		
		for (TransitionSeries ts1 : summationCandidates)
		{
			for (TransitionSeries ts2 : summationCandidates)
			{
				tss.add(ts1.summation(ts2));
			}
		}
		

		//remove the transition series we have already fit, including any summations
		tss.removeAll(getFittedTransitionSeries());
		tss.removeAll(getProposedTransitionSeries());
		
		
		//We then re-add the TS passed to us so that we can still suggest the 
		//TS that is currently selected, if it fits
		if (currentTSisUsed) {
			tss.add(currentTS);
		}
		
		
		//remove any duplicates we might have created while adding the summations
		tss = Fn.unique(tss);
		

		//sort first by how close they are to the channel in quesiton
		Fn.sortBy(tss, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				Double prox1, prox2;

				prox1 = Math.abs(ts1.getProximityToEnergy(energy));
				prox2 = Math.abs(ts2.getProximityToEnergy(energy));

				return prox1.compareTo(prox2);

			}
		}, Functions.<TransitionSeries> id());
		
		//take the top n based on position alone
		tss = tss.take(15);
		
		//now sort by score
		Collections.sort(tss, new Comparator<TransitionSeries>() {

			public int compare(TransitionSeries ts1, TransitionSeries ts2)
			{
				Float prox1, prox2;

				prox1 = score.f(ts1);
				prox2 = score.f(ts2);

				return prox2.compareTo(prox1);

			}
		});
			
		//take the 5 best in sorted order based on score
		return tss.take(5);
	}






	// =============================================
	// UI SETTINGS FUNCTIONS TO IMPLEMENT SettingsController
	// =============================================
	public float getZoom()
	{
		return model.viewOptions.zoom;
	}


	public void setZoom(float zoom)
	{
		model.viewOptions.zoom = zoom;
		updateListeners();
	}


	public void setShowIndividualSelections(boolean showIndividualSelections)
	{
		model.viewOptions.showIndividualFittings = showIndividualSelections;
		setUndoPoint();
		fittingDataInvalidated();
	}


	public boolean getShowIndividualSelections()
	{
		return model.viewOptions.showIndividualFittings;
	}


	public void setEnergyPerChannel(float energy)
	{
		int scanSize = 0;
		if (model.dataset != null) scanSize = model.dataset.scanSize();
		//dont call setUndoPoint, as setFittingParameters will do that for us
		setFittingParameters(scanSize, energy);
		axisSetInvalidated();
	}


	public float getEnergyPerChannel()
	{
		return model.dr.unitSize;
	}


	public void setMaxEnergy(float energy)
	{
		if (!model.dataset.hasData() || model.dataset.scanSize() == 0)
		{
			return;
		}
		//dont set an undo point here -- setEnergyPerChannel does that already
		setEnergyPerChannel(energy / (model.dataset.scanSize()));

	}


	public float getMaxEnergy()
	{
		if (!model.dataset.hasData() || model.dataset.scanSize() == 0)
		{
			return 20.48f;
		}
		return model.dr.unitSize * (model.dataset.scanSize());
	}


	// log vs linear view
	public void setViewLog(boolean log)
	{
		if (log)
		{
			model.dr.viewTransform = ViewTransform.LOG;
		}
		else
		{
			model.dr.viewTransform = ViewTransform.LINEAR;
		}
		axisSetInvalidated();
		setUndoPoint();
		updateListeners();
	}


	public boolean getViewLog()
	{
		return (model.dr.viewTransform == ViewTransform.LOG);
	}


	// channel value composition type
	// averages, maxed, none
	public void setShowChannelAverage()
	{
		model.viewOptions.channelComposite = ChannelCompositeMode.AVERAGE;
		setUndoPoint();
		filteredDataInvalidated();
	}


	public void setShowChannelMaximum()
	{
		model.viewOptions.channelComposite = ChannelCompositeMode.MAXIMUM;
		setUndoPoint();
		filteredDataInvalidated();
	}


	public void setShowChannelSingle()
	{
		model.viewOptions.channelComposite = ChannelCompositeMode.NONE;
		setUndoPoint();
		filteredDataInvalidated();
	}


	public ChannelCompositeMode getChannelCompositeType()
	{
		return model.viewOptions.channelComposite;
	}


	public void setScanNumber(int number)
	{
		//negative is downwards, positive is upwards
		int direction = number - model.viewOptions.scanNumber;

		if (direction > 0)
		{
			number = model.dataset.firstNonNullScanIndex(number);
		}
		else
		{
			number = model.dataset.lastNonNullScanIndex(number);
		}

		if (number == -1)
		{
			updateListeners();
			return;
		}

		if (number > model.dataset.scanCount() - 1) number = model.dataset.scanCount() - 1;
		if (number < 0) number = 0;
		model.viewOptions.scanNumber = number;
		filteredDataInvalidated();
	}


	public int getScanNumber()
	{
		return model.viewOptions.scanNumber;
	}


	// axes and gridlines
	public void setShowAxes(boolean axes)
	{
		model.viewOptions.showAxes = axes;
		axisPainters = null;
		setUndoPoint();
		updateListeners();
	}


	public boolean getShowAxes()
	{
		return model.viewOptions.showAxes;
	}


	public boolean getShowTitle()
	{
		return model.viewOptions.showPlotTitle;
	}


	public void setShowTitle(boolean show)
	{
		model.viewOptions.showPlotTitle = show;
		axisPainters = null;
		setUndoPoint();
		updateListeners();
	}


	// monochrome view for print publication / visually impaired or colour blind
	// users
	public void setMonochrome(boolean mono)
	{
		model.viewOptions.monochrome = mono;
		setUndoPoint();
		updateListeners();
	}


	public boolean getMonochrome()
	{
		return model.viewOptions.monochrome;
	}


	public void setShowElementTitles(boolean show)
	{
		model.viewOptions.showElementFitTitles = show;
		setUndoPoint();
		updateListeners();
	}


	public void setShowElementMarkers(boolean show)
	{
		model.viewOptions.showElementFitMarkers = show;
		setUndoPoint();
		updateListeners();
	}


	public void setShowElementIntensities(boolean show)
	{
		model.viewOptions.showElementFitIntensities = show;
		setUndoPoint();
		updateListeners();
	}


	public boolean getShowElementTitles()
	{
		return model.viewOptions.showElementFitTitles;
	}


	public boolean getShowElementMarkers()
	{
		return model.viewOptions.showElementFitMarkers;
	}


	public boolean getShowElementIntensities()
	{
		return model.viewOptions.showElementFitIntensities;
	}


	public void setShowRawData(boolean show)
	{
		model.viewOptions.backgroundShowOriginal = show;
		setUndoPoint();
		updateListeners();
	}


	public boolean getShowRawData()
	{
		return model.viewOptions.backgroundShowOriginal;
	}






	//////////////////////////////////////////////////////////////
	// UNDO CONTROLLER METHODS
	//////////////////////////////////////////////////////////////
	public void setUndoPoint()
	{

		//save the current state
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		savePreferences(baos);

		if (undoStack.size() > 0)
		{
			byte[] lastState = undoStack.peek().toByteArray();
			byte[] thisState = baos.toByteArray();


			//if these two are the same size, lets compare them -- if they are identical, we don't bother saving the state
			if (thisState.length == lastState.length)
			{
				boolean same = true;
				for (int i = 0; i < thisState.length; i++)
				{
					if (lastState[i] != thisState[i])
					{
						same = false;
						break;
					}
				}

				if (same) return;

			}
		}

		undoStack.push(baos);

		redoStack.clear();

	}


	public void undo()
	{
		if (undoStack.size() < 2) return;

		redoStack.push(undoStack.pop());

		loadPreferences(new ByteArrayInputStream(undoStack.peek().toByteArray()), true);

	}


	public void redo()
	{

		if (redoStack.isEmpty()) return;

		undoStack.push(redoStack.pop());

		loadPreferences(new ByteArrayInputStream(undoStack.peek().toByteArray()), true);

	}


	public boolean canUndo()
	{
		return undoStack.size() >= 2;
	}


	public boolean canRedo()
	{
		return !redoStack.isEmpty();
	}



	public void clearUndos()
	{
		undoStack.clear();
		setUndoPoint();
	}


}
