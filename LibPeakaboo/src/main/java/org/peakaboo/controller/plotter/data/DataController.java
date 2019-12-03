package org.peakaboo.controller.plotter.data;

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
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.datafile.DataFile;
import org.peakaboo.datasource.model.internal.SelectionDataSource;
import org.peakaboo.datasource.plugin.DataSourcePlugin;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.eventful.Eventful;
import org.peakaboo.framework.eventful.EventfulListener;
import org.peakaboo.framework.plural.executor.AbstractExecutor;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.mapping.Mapping;
import org.peakaboo.mapping.rawmap.RawMapSet;


/**
 * DataController wraps a DataSet in a UI-aware layer which integrates with the {@link IPlotController}
 * 
 */
public class DataController extends Eventful
{

	private DataSet 			dataModel;
	private PlotController		plot;
	private Discards			discards;
	private List<DataFile>		dataPaths;
	protected String			title;
	private String				dataSourcePluginUUID;
	private List<Object>		dataSourceParameters;
	
	public DataController(PlotController plotController)
	{
		this.plot = plotController;
		dataModel = new EmptyDataSet();
		discards = new DiscardsList(plot);
		dataPaths = new ArrayList<>();
		dataSourcePluginUUID = null;
		dataSourceParameters = new ArrayList<>();
	}

	
	public DataSet getDataSet() {
		return dataModel;
	}
	
	
	// =============================================
	// Functions to implement IDataController
	// =============================================
	

	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<DataFile> paths, DataSourcePlugin dsp, Consumer<DatasetReadResult> onResult)
	{

		//final LocalDataSetProvider dataset = new LocalDataSetProvider();
		final StandardDataSet dataset = new StandardDataSet();
		final ExecutorSet<DatasetReadResult> readTasks = dataset.asyncReadFileListAsDataset(paths, dsp);


		
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
						setDataSourcePluginUUID(dsp.pluginUUID());
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

	public SelectionDataSource getDataSourceForSubset(List<Integer> points, Coord<Integer> dimensions) {
		return new SelectionDataSource(dataModel.getDataSource(), dimensions, points);
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
		
		plot.view().setScanNumber( dsp.getScanData().firstNonNullScanIndex() );
		plot.fitting().setMinMaxEnergy(dsp.getDataSource().getScanData().minEnergy(), dsp.getDataSource().getScanData().maxEnergy());
		
	
		plot.history().clear();
			
		// really shouldn't have to do this, but there is a reference to old datasets floating around somewhere
		// (task listener?) which is preventing them from being garbage-collected
		if (old != null && old != dsp) old.discard();
	
		updateListeners();

		

	}
	
	public String getDataSourcePluginUUID() {
		return this.dataSourcePluginUUID;
	}
	
	public void setDataSourcePluginUUID(String uuid) {
		this.dataSourcePluginUUID = uuid;
	}
	
	
	public void setDataSource(DataSource ds, AbstractExecutor progress, Supplier<Boolean> isAborted)
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

			int nextIndex = dataModel.getScanData().firstNonNullScanIndex();
			ReadOnlySpectrum next = dataModel.getScanData().get(nextIndex);
			
			
			public boolean hasNext()
			{
				return next != null;
			}

			public ReadOnlySpectrum next()
			{
				ReadOnlySpectrum current = next;
				nextIndex = dataModel.getScanData().firstNonNullScanIndex(nextIndex+1);
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


	public List<DataFile> getDataPaths() {
		return dataPaths;
	}


	public void setDataPaths(List<DataFile> dataPaths) {
		this.dataPaths = dataPaths;
	}


	public List<Object> getDataSourceParameters() {
		return dataSourceParameters;
	}

	public void setDataSourceParameters(List<Object> dataSourceParameters) {
		this.dataSourceParameters = dataSourceParameters;
	}
	

	/**
	 * Returns the human readable title for this dataset. If the dataset has been
	 * set by the user, that value is returned, otherwise the call will be delegated
	 * to {@link ScanData#datasetName()}
	 */
	public String getTitle() {
		if (title == null) {
			return getDataSet().getScanData().datasetName();
		} else {
			return title;
		}
	}
	
	public void setTitle(String title) {
		this.title = title;
		updateListeners();
	}
	
}
