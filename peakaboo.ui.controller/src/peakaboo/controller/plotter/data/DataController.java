package peakaboo.controller.plotter.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eventful.Eventful;
import eventful.EventfulListener;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.dataset.StandardDataSet;
import peakaboo.dataset.DataSet;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.EmptyDataSet;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.internal.CroppedDataSource;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.MapTS;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import scitypes.Coord;
import scitypes.Spectrum;


public class DataController extends Eventful implements IDataController
{

	private DataSet 	dataModel;
	private IPlotController		plot;
	private List<Integer>		badScans;
	private int 				dataHeight, dataWidth;
	
	
	public DataController(IPlotController plotController)
	{
		this.plot = plotController;
		dataModel = new EmptyDataSet();
		badScans = new ArrayList<Integer>();
	}
	
	public DataSet getDataModel()
	{
		return dataModel;
	}
	
	// =============================================
	// Functions to implement IDataController
	// =============================================
	
	@Override
	public List<DataSource> getDataSourcePlugins()
	{
		return StandardDataSet.getDataSourcePlugins();
	}
	
	
	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<String> filenames, DataSource dsp)
	{

		//final LocalDataSetProvider dataset = new LocalDataSetProvider();
		final StandardDataSet dataset = new StandardDataSet();
		final ExecutorSet<DatasetReadResult> readTasks = dataset.TASK_readFileListAsDataset(filenames, dsp);


		
		EventfulListener datasetListener = new EventfulListener() {

			boolean loadedNewDataSet = false;
			
			public void change()
			{
				if (readTasks.getCompleted() && dataset.channelsPerScan() > 0 && !loadedNewDataSet) {
												
					setDataSetProvider(dataset);
					loadedNewDataSet = true;

				}
			}

		};
		
		readTasks.addListener(datasetListener);

		return readTasks;

	}

	
	public int size()
	{
		if (!dataModel.hasData()) return 0;
		return dataModel.size();
	}
	
	public int channelsPerScan()
	{
		if (!dataModel.hasData()) return 0;
		return dataModel.channelsPerScan();
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
		return new CroppedDataSource(dataModel.getDataSource(), x, y, cstart, cend);
	}

	public String getDatasetName()
	{
		return dataModel.getScanData().datasetName();
	}

	public boolean hasDataSet()
	{
		return dataModel.hasData();
	}

	@Override
	public boolean hasPhysicalSize()
	{
		return dataModel.hasPhysicalSize();
	}

	@Override
	public PhysicalSize getPhysicalSize() {
		return dataModel.getPhysicalSize();
	}
	
	@Override
	public DataSize getDataSize() {
		return dataModel.getDataSize();
	}


	public void setDataSetProvider(DataSet dsp)
	{
	
		if (dsp == null) return;
		
		DataSet old = dataModel;
		dataModel = dsp;
		
		plot.settings().setScanNumber( dsp.firstNonNullScanIndex() );
		
		setDataWidth(dataModel.channelsPerScan());
		setDataHeight(1);
		
		plot.fitting().setFittingParameters(dataModel.energyPerChannel());
				
		if (plot.mapping() != null) plot.mapping().mapsController.setInterpolation(0);
		
		plot.history().clearUndos();
			
		// really shouldn't have to do this, but there is a reference to old datasets floating around somewhere
		// (task listener?) which is preventing them from being garbage-collected
		if (old != null && old != dsp) old.discard();
	
		updateListeners();

		

	}

	public void setDataSource(DataSource ds)
	{
		setDataSetProvider(new StandardDataSet(ds));
	}

	public String getCurrentScanName()
	{
		return dataModel.getScanData().scanName(plot.settings().getScanNumber());
	}


	
	
	
	
	public boolean hasMetadata() {
		if (dataModel == null) { return false; }
		return dataModel.hasMetadata();
	}
	
	public Metadata getMetadata() {
		if (dataModel == null) { return null; }
		return dataModel.getMetadata();
	}
	

	
	
	
	public boolean getScanDiscarded(int scanNo)
	{
		return (badScans.indexOf(scanNo) != -1);
	}

	public boolean getScanDiscarded()
	{
		return getScanDiscarded(plot.settings().getScanNumber());
	}

	public void setScanDiscarded(int scanNo, boolean discarded)
	{

		if (discarded)
		{
			if (!getScanDiscarded(scanNo)) badScans.add(scanNo);
			plot.filtering().filteredDataInvalidated();
		}
		else
		{
			if (getScanDiscarded(scanNo)) badScans.remove(badScans.indexOf(scanNo));
			plot.filtering().filteredDataInvalidated();
		}

		plot.history().setUndoPoint("Marking Bad");

	}

	public void setScanDiscarded(boolean discarded)
	{
		setScanDiscarded(plot.settings().getScanNumber(), discarded);
	}

	public List<Integer> getDiscardedScanList()
	{
		return new ArrayList<Integer>(badScans);
	}	
	
	public void clearDiscardedScanList()
	{
		badScans.clear();
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
		return dataModel.getScanData().get(index);
	}
	
	
	public ExecutorSet<MapResultSet> TASK_calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type)
	{
		return MapTS.calculateMap(dataModel, filters, fittings, type);
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
	
	
	public Iterator<Spectrum> getScanIterator()
	{
		
		return new Iterator<Spectrum>() {

			int nextIndex = firstNonNullScanIndex();
			Spectrum next = getScanAtIndex(nextIndex);
			
			
			@Override
			public boolean hasNext()
			{
				return next != null;
			}

			@Override
			public Spectrum next()
			{
				Spectrum current = next;
				nextIndex = firstNonNullScanIndex(nextIndex+1);
				if (nextIndex == -1) {
					next = null;
				} else {
					next = getScanAtIndex(nextIndex);
				}
				return current;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}};
		
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
