package peakaboo.controller.plotter.data;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import eventful.Eventful;
import eventful.EventfulListener;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.discards.Discards;
import peakaboo.controller.plotter.data.discards.DiscardsList;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.dataset.DataSet;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.dataset.EmptyDataSet;
import peakaboo.dataset.StandardDataSet;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.internal.CroppedDataSource;
import peakaboo.datasource.model.internal.SelectionDataSource;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.Mapping;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.streams.StreamExecutor;
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
	

	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<Path> paths, DataSource dsp)
	{

		//final LocalDataSetProvider dataset = new LocalDataSetProvider();
		final StandardDataSet dataset = new StandardDataSet();
		final ExecutorSet<DatasetReadResult> readTasks = dataset.TASK_readFileListAsDataset(paths, dsp);


		
		EventfulListener datasetListener = new EventfulListener() {

			boolean loadedNewDataSet = false;
			
			public void change()
			{
				if (!readTasks.getCompleted()) { return; }
				
				switch (readTasks.getResult().status) {
				case SUCCESS:
					if (dataset.getAnalysis().channelsPerScan() > 0 && !loadedNewDataSet) {
						DatasetReadResult result = readTasks.getResult();
						if (result.status == ReadStatus.SUCCESS) {
							setDataSetProvider(dataset);
							loadedNewDataSet = true;
						}
					}
					return;
					
				case FAILED:
					//Error reporting is handled at the UI level in this case. 
					//Just don't try to read the result.
					return;
					
				case CANCELLED:
					return;
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
		return dataModel.hasGenuineScanData();
	}


	public void setDataSetProvider(DataSet dsp)
	{
	
		if (dsp == null) return;
		
		DataSet old = dataModel;
		dataModel = dsp;
		
		plot.view().setScanNumber( dsp.getAnalysis().firstNonNullScanIndex() );
		plot.view().setMinEnergy(dsp.getDataSource().getScanData().minEnergy());
		plot.view().setMaxEnergy(dsp.getDataSource().getScanData().maxEnergy());
		
	
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
	
	

	
	public StreamExecutor<MapResultSet> getMapTask(FilterSet filters, FittingSet fittings, CurveFitter fitter, FittingSolver solver)
	{
		return Mapping.mapTask(dataModel, filters, fittings, fitter, solver);
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
