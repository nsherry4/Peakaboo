package org.peakaboo.controller.plotter.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.data.discards.Discards;
import org.peakaboo.controller.plotter.data.discards.DiscardsList;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.dataset.DatasetReadResult;
import org.peakaboo.dataset.EmptyDataSet;
import org.peakaboo.dataset.StandardDataSet;
import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.model.internal.CroppedDataSource;
import org.peakaboo.datasource.model.internal.SelectionDataSource;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.mapping.Mapping;
import org.peakaboo.mapping.rawmap.RawMapSet;

import cyclops.Coord;
import cyclops.ReadOnlySpectrum;
import eventful.Eventful;
import eventful.EventfulListener;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.streams.StreamExecutor;


/**
 * DataController wraps a DataSet in a UI-aware layer which integrates with the {@link IPlotController}
 * 
 */
public class DataController extends Eventful
{

	private DataSet 			dataModel;
	private PlotController		plot;
	private Discards			discards;
	private List<Path>			dataPaths;
	
	
	public DataController(PlotController plotController)
	{
		this.plot = plotController;
		dataModel = new EmptyDataSet();
		discards = new DiscardsList(plot);
		dataPaths = new ArrayList<>();
	}

	
	public DataSet getDataSet() {
		return dataModel;
	}
	
	
	// =============================================
	// Functions to implement IDataController
	// =============================================
	

	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<Path> paths, DataSource dsp, Consumer<DatasetReadResult> onResult)
	{

		//final LocalDataSetProvider dataset = new LocalDataSetProvider();
		final StandardDataSet dataset = new StandardDataSet();
		final ExecutorSet<DatasetReadResult> readTasks = dataset.TASK_readFileListAsDataset(paths, dsp);


		
		EventfulListener datasetListener = new EventfulListener() {

			boolean finished = false;
			
			public synchronized void change()
			{
				if (!readTasks.getCompleted() && !readTasks.isResultSet()) { return; }
				if (finished) { return; }
				finished = true;
				
				DatasetReadResult result = readTasks.getResult();
				
				switch (result.status) {
				case SUCCESS:
					
					if (dataset.getAnalysis().channelsPerScan() > 0) {
						setDataSetProvider(dataset);
					}
					onResult.accept(result);
					return;
					
				case FAILED:
					//Error reporting is handled at the UI level in this case. 
					//Just don't try to read the result.
					onResult.accept(result);
					return;
					
				case CANCELLED:
					onResult.accept(result);
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
		plot.fitting().setMinEnergy(dsp.getDataSource().getScanData().minEnergy());
		plot.fitting().setMaxEnergy(dsp.getDataSource().getScanData().maxEnergy());
		
	
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
	
	

	
	public StreamExecutor<RawMapSet> getMapTask(FilterSet filters, FittingSet fittings, CurveFitter fitter, FittingSolver solver)
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


	public List<Path> getDataPaths() {
		return dataPaths;
	}


	public void setDataPaths(List<Path> dataPaths) {
		this.dataPaths = dataPaths;
	}

	
	
	
}
