package org.peakaboo.controller.plotter.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.data.discards.Discards;
import org.peakaboo.controller.plotter.data.discards.DiscardsList;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.dataset.DatasetReadResult;
import org.peakaboo.dataset.EmptyDataSet;
import org.peakaboo.dataset.StandardDataSet;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.model.datafile.DataFile;
import org.peakaboo.dataset.source.model.internal.SelectionDataSource;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.eventful.Eventful;
import org.peakaboo.framework.eventful.EventfulListener;
import org.peakaboo.framework.plural.executor.AbstractExecutor;
import org.peakaboo.framework.plural.executor.ExecutorSet;


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
	private Map<String, Object>	dataSourceParameters;
	
	public DataController(PlotController plotController) {
		this.plot = plotController;
		dataModel = new EmptyDataSet();
		discards = new DiscardsList(plot);
		dataPaths = new ArrayList<>();
		dataSourcePluginUUID = null;
		dataSourceParameters = new LinkedHashMap<>();
	}

	
	public DataSet getDataSet() {
		return dataModel;
	}
	
	
	// =============================================
	// Functions to implement IDataController
	// =============================================
	

	public ExecutorSet<DatasetReadResult> asyncReadFileListAsDataset (
			List<DataFile> paths, 
			DataSourcePlugin dsp, 
			Consumer<DatasetReadResult> onResult) {

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
				case CANCELLED:
					//Error reporting is handled at the UI level in this case. 
					//Just don't try to read the result.
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

	public boolean hasDataSet() {
		return dataModel.hasGenuineScanData();
	}


	public void setDataSetProvider(DataSet dsp) {
	
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
	
	
	public void setDataSource(DataSource ds, AbstractExecutor<Void> progress, BooleanSupplier isAborted) {
		StandardDataSet dataset = new StandardDataSet(ds, progress, isAborted);
		if (!isAborted.getAsBoolean()) {
			setDataSetProvider(dataset);
		}
	}


	

	
	public Discards getDiscards() {
		return discards;
	}
	
	
	public Iterator<ReadOnlySpectrum> getScanIterator() {
		
		return new Iterator<ReadOnlySpectrum>() {

			int nextIndex = dataModel.getScanData().firstNonNullScanIndex();
			ReadOnlySpectrum next = dataModel.getScanData().get(nextIndex);
			
			
			public boolean hasNext() {
				return next != null;
			}

			public ReadOnlySpectrum next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				ReadOnlySpectrum current = next;
				nextIndex = dataModel.getScanData().firstNonNullScanIndex(nextIndex+1);
				if (nextIndex == -1) {
					next = null;
				} else {
					next = dataModel.getScanData().get(nextIndex);
				}
				return current;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}};
		
	}


	public List<DataFile> getDataPaths() {
		return dataPaths;
	}


	public void setDataPaths(List<DataFile> dataPaths) {
		this.dataPaths = dataPaths;
	}


	public Map<String, Object> getDataSourceParameters() {
		return dataSourceParameters;
	}

	public void setDataSourceParameters(Map<String, Object> dataSourceParameters) {
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
	
	/**
	 * Returns the user-specified title of the dataset. If the user has not
	 * specified a custom title, the return value will be null.
	 */
	public String getCustomTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
		updateListeners();
	}
	
}
