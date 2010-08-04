package peakaboo.controller.plotter.data;

import java.util.List;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.PlotController.UpdateType;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.dataset.provider.implementations.EmptyDataSetProvider;
import peakaboo.dataset.provider.implementations.OnDemandDataSetProvider;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.fileio.CopiedDataSource;
import peakaboo.fileio.DataSource;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.workers.PluralSet;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;

import commonenvironment.AbstractFile;

import eventful.Eventful;
import eventful.EventfulListener;
import fava.datatypes.Bounds;


public class DataController extends Eventful implements IDataController
{

	private DataSetProvider dataModel;
	private PlotController plotController;
	public List<Integer>		badScans;
	private int dataHeight, dataWidth;
	
	
	public DataController(PlotController plotController)
	{
		this.plotController = plotController;
		dataModel = new EmptyDataSetProvider();
		badScans = DataTypeFactory.<Integer>list();
	}
	
	public DataSetProvider getDataModel()
	{
		return dataModel;
	}
	
	// =============================================
	// Functions to implement IDataController
	// =============================================
	
	public PluralSet<Boolean> TASK_readFileListAsDataset(final List<AbstractFile> files)
	{

		//final LocalDataSetProvider dataset = new LocalDataSetProvider();
		final OnDemandDataSetProvider dataset = new OnDemandDataSetProvider();
		final PluralSet<Boolean> readTasks = dataset.TASK_readFileListAsDataset(files);


		
		EventfulListener datasetListener = new EventfulListener() {

			boolean loadedNewDataSet = false;
			
			public void change()
			{
				if (readTasks.getCompleted())
				{
					if (dataset.scanSize() > 0 && !loadedNewDataSet)
					{
												
						setDataSetProvider(dataset);
						loadedNewDataSet = true;

					}
				}
			}

		};
		
		readTasks.addListener(datasetListener);

		return readTasks;

	}

	public int datasetScanCount()
	{
		if (!dataModel.hasData()) return 0;
		return dataModel.scanCount();
	}

	public int datasetScanSize()
	{
		if (!dataModel.hasData()) return 0;
		return dataModel.scanSize();
	}

	public Coord<Integer> getDataDimensions()
	{
		return dataModel.getDataDimensions();
	}

	public int getDataHeight()
	{
		return dataHeight;
	}
	
	public int getDataWidth()
	{
		return dataWidth;
	}

	public String getDataSourceFolder()
	{
		return dataModel.getDataSourcePath();
	}

	public DataSource getDataSourceForSubset(int x, int y, Coord<Integer> cstart, Coord<Integer> cend)
	{
		return new CopiedDataSource(dataModel.getDataSource(), x, y, cstart, cend);
	}

	public String getDatasetName()
	{
		return dataModel.getDatasetName();
	}

	public Coord<Bounds<Number>> getRealDimensions()
	{
		return dataModel.getRealDimensions();
	}

	public SISize getRealDimensionsUnits()
	{
		return dataModel.getRealDimensionsUnits();
	}

	public boolean hasDataSet()
	{
		return dataModel.hasData();
	}

	public boolean hasDimensions()
	{
		return dataModel.hasDimensions();
	}

	public void setDataSetProvider(DataSetProvider dsp)
	{
	
		if (dsp == null) return;
		
		DataSetProvider old = dataModel;
		dataModel = dsp;
		
		plotController.settingsController.setScanNumber( dsp.firstNonNullScanIndex() );
		
		setDataWidth(dataModel.scanSize());
		setDataHeight(1);
		
		plotController.fittingController.setFittingParameters(dataModel.energyPerChannel());
				
		if (plotController.mapController != null) plotController.mapController.setInterpolation(0);
		
		plotController.undoController.clearUndos();
			
		// really shouldn't have to do this, but there is a reference to old datasets floating around somewhere
		// (task listener?) which is preventing them from being garbage-collected
		if (old != null && old != dsp) old.discard();
	
		updateListeners();

		

	}

	public void setDataSource(DataSource ds)
	{
		setDataSetProvider(new OnDemandDataSetProvider(ds));
	}

	public String getCurrentScanName()
	{
		return dataModel.getScanName(plotController.settingsController.getScanNumber());
	}

	public boolean getScanHasExtendedInformation()
	{
		if (dataModel == null) return false;
		return dataModel.hasExtendedInformation();
	}

	public String getScanCreationTime()
	{
		if (dataModel == null) return null;
		return dataModel.getCreationTime();
	}

	public String getScanCreator()
	{
		if (dataModel == null) return null;
		return dataModel.getCreator();
	}

	public String getScanEndTime()
	{
		if (dataModel == null) return null;
		return dataModel.getEndTime();
	}

	public String getScanExperimentName()
	{
		if (dataModel == null) return null;
		return dataModel.getExperimentName();
	}

	public String getScanFacilityName()
	{
		if (dataModel == null) return null;
		return dataModel.getFacilityName();
	}

	public String getScanInstrumentName()
	{
		if (dataModel == null) return null;
		return dataModel.getInstrumentName();
	}

	public String getScanLaboratoryName()
	{
		if (dataModel == null) return null;
		return dataModel.getLaboratoryName();
	}

	public String getScanProjectName()
	{
		if (dataModel == null) return null;
		return dataModel.getProjectName();
	}

	public String getScanSampleName()
	{
		if (dataModel == null) return null;
		return dataModel.getSampleName();
	}

	public String getScanScanName()
	{
		if (dataModel == null) return null;
		return dataModel.getScanName();
	}

	public String getScanSessionName()
	{
		if (dataModel == null) return null;
		return dataModel.getSessionName();
	}

	public String getScanStartTime()
	{
		if (dataModel == null) return null;
		return dataModel.getStartTime();
	}

	public String getScanTechniqueName()
	{
		if (dataModel == null) return null;
		return dataModel.getTechniqueName();
	}

	public boolean getScanDiscarded(int scanNo)
	{
		return (badScans.indexOf(scanNo) != -1);
	}

	public boolean getScanDiscarded()
	{
		return getScanDiscarded(plotController.settingsController.getScanNumber());
	}

	public void setScanDiscarded(int scanNo, boolean discarded)
	{

		if (discarded)
		{
			if (!getScanDiscarded(scanNo)) badScans.add(scanNo);
			plotController.filteringController.filteredDataInvalidated();
		}
		else
		{
			if (getScanDiscarded(scanNo)) badScans.remove(badScans.indexOf(scanNo));
			plotController.filteringController.filteredDataInvalidated();
		}

		plotController.undoController.setUndoPoint("Marking Bad");

	}

	public void setScanDiscarded(boolean discarded)
	{
		setScanDiscarded(plotController.settingsController.getScanNumber(), discarded);
	}

	public List<Integer> getDiscardedScanList()
	{
		return DataTypeFactory.<Integer> listInit(badScans);
	}	
	
	
	public Spectrum getAveragePlot()
	{
		return dataModel.averagePlot(badScans);
	}

	public Spectrum getMaximumPlot()
	{
		return dataModel.maximumPlot();
	}

	public Spectrum getScanAtIndex(int index)
	{
		return dataModel.getScan(index);
	}
	
	
	public PluralSet<MapResultSet> TASK_calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type)
	{
		return dataModel.calculateMap(filters, fittings, type);
	}
	
	public float maximumIntensity()
	{
		return dataModel.maximumIntensity();
	}
	
	
	
	
	
	public int firstNonNullScanIndex(int start)
	{
		return dataModel.firstNonNullScanIndex(start);
	}
	public int firstNonNullScanIndex()
	{
		return dataModel.firstNonNullScanIndex();
	}
	public int lastNonNullScanIndex(int upto)
	{
		return dataModel.lastNonNullScanIndex(upto);
	}
	public int lastNonNullScanIndex()
	{
		return dataModel.lastNonNullScanIndex();
	}
	
	
	
	
	
	public void invalidateFilteredData()
	{
		dataModel.invalidateFilteredData();
	}
	
	
	
	
	// =============================================
	// Helper Functions
	// =============================================
	private void setDataHeight(int height)
	{
		dataHeight = height;
		updateListeners();
	}
	
	private void setDataWidth(int width)
	{
		dataWidth = width;
		updateListeners();
	}


	
	
	
}
