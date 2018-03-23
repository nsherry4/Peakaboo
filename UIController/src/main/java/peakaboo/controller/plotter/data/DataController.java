package peakaboo.controller.plotter.data;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import eventful.Eventful;
import eventful.EventfulListener;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.discards.Discards;
import peakaboo.controller.plotter.data.discards.DiscardsList;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.dataset.DataSet;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.dataset.EmptyDataSet;
import peakaboo.dataset.StandardDataSet;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.internal.CroppedDataSource;
import peakaboo.datasource.model.internal.SelectionDataSource;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.MapTS;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import scitypes.Coord;
import scitypes.ReadOnlySpectrum;


/**
 * DataController wraps a DataSet in a UI-aware layer which integrates with the {@link IPlotController}
 * 
 */
public class DataController extends Eventful
{

	private DataSet 			dataModel;
	private PlotController		plot;
	private Discards			discards;
	
	
	public DataController(PlotController plotController)
	{
		this.plot = plotController;
		dataModel = new EmptyDataSet();
		discards = new DiscardsList(plot);
	}

	
	public DataSet getDataSet() {
		return dataModel;
	}
	
	
	// =============================================
	// Functions to implement IDataController
	// =============================================
	

	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<File> files, DataSource dsp)
	{

		//final LocalDataSetProvider dataset = new LocalDataSetProvider();
		final StandardDataSet dataset = new StandardDataSet();
		final ExecutorSet<DatasetReadResult> readTasks = dataset.TASK_readFileListAsDataset(files, dsp);


		
		EventfulListener datasetListener = new EventfulListener() {

			boolean loadedNewDataSet = false;
			
			public void change()
			{
				if (readTasks.getCompleted() && dataset.channelsPerScan() > 0 && !loadedNewDataSet) {
					DatasetReadResult result = readTasks.getResult();
					if (result.status == ReadStatus.SUCCESS) {
						setDataSetProvider(dataset);
						loadedNewDataSet = true;
					}

				}
			}

		};
		
		readTasks.addListener(datasetListener);

		return readTasks;

	}



	public CroppedDataSource getDataSourceForSubset(int x, int y, Coord<Integer> cstart, Coord<Integer> cend)
	{
		return new CroppedDataSource(dataModel.getDataSource(), x, y, cstart, cend);
	}

	public SelectionDataSource getDataSourceForSubset(List<Integer> points)
	{
		return new SelectionDataSource(dataModel.getDataSource(), points);
	}
	

	public boolean hasDataSet()
	{
		return dataModel.hasGenuineData();
	}


	public void setDataSetProvider(DataSet dsp)
	{
	
		if (dsp == null) return;
		
		DataSet old = dataModel;
		dataModel = dsp;
		
		plot.settings().setScanNumber( dsp.getAnalysis().firstNonNullScanIndex() );
		plot.settings().setMinEnergy(dsp.getDataSource().getScanData().minEnergy());
		plot.settings().setMaxEnergy(dsp.getDataSource().getScanData().maxEnergy());
		
	
		plot.history().clearUndos();
			
		// really shouldn't have to do this, but there is a reference to old datasets floating around somewhere
		// (task listener?) which is preventing them from being garbage-collected
		if (old != null && old != dsp) old.discard();
	
		updateListeners();

		

	}
	
	public void setDataSource(DataSource ds, DummyExecutor progress, Supplier<Boolean> isAborted)
	{
		StandardDataSet dataset = new StandardDataSet(ds, progress, isAborted);
		if (!isAborted.get()) {
			setDataSetProvider(dataset);
		}
	}


	

	
	public Discards getDiscards() {
		return discards;
	}
	
	


	
	public ExecutorSet<MapResultSet> TASK_calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type)
	{
		return MapTS.calculateMap(dataModel, filters, fittings, type);
	}
	

	
	

	
	
	public Iterator<ReadOnlySpectrum> getScanIterator()
	{
		
		return new Iterator<ReadOnlySpectrum>() {

			int nextIndex = dataModel.getAnalysis().firstNonNullScanIndex();
			ReadOnlySpectrum next = dataModel.getScanData().get(nextIndex);
			
			
			public boolean hasNext()
			{
				return next != null;
			}

			public ReadOnlySpectrum next()
			{
				ReadOnlySpectrum current = next;
				nextIndex = dataModel.getAnalysis().firstNonNullScanIndex(nextIndex+1);
				if (nextIndex == -1) {
					next = null;
				} else {
					next = dataModel.getScanData().get(nextIndex);
				}
				return current;
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}};
		
	}

	
}
