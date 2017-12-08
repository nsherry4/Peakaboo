package peakaboo.controller.plotter.data;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import eventful.Eventful;
import eventful.EventfulListener;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.controller.plotter.data.discards.Discards;
import peakaboo.controller.plotter.data.discards.DiscardsList;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.dataset.StandardDataSet;
import peakaboo.dataset.DataSet;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.EmptyDataSet;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.internal.CroppedDataSource;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.MapTS;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import scitypes.Coord;
import scitypes.Spectrum;


/**
 * DataController wraps a DataSet in a UI-aware layer which integrates with the {@link IPlotController}
 * 
 */
public class DataController extends Eventful implements IDataController
{

	private DataSet 			dataModel;
	private IPlotController		plot;
	private Discards			discards;
	
	
	public DataController(IPlotController plotController)
	{
		this.plot = plotController;
		dataModel = new EmptyDataSet();
		discards = new DiscardsList(plot);
	}

	
	@Override
	public DataSet getDataSet() {
		return dataModel;
	}
	
	
	// =============================================
	// Functions to implement IDataController
	// =============================================
	

	@Override
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
												
					setDataSetProvider(dataset);
					loadedNewDataSet = true;

				}
			}

		};
		
		readTasks.addListener(datasetListener);

		return readTasks;

	}



	@Override
	public DataSource getDataSourceForSubset(int x, int y, Coord<Integer> cstart, Coord<Integer> cend)
	{
		return new CroppedDataSource(dataModel.getDataSource(), x, y, cstart, cend);
	}


	@Override
	public boolean hasDataSet()
	{
		return dataModel.hasData();
	}


	public void setDataSetProvider(DataSet dsp)
	{
	
		if (dsp == null) return;
		
		DataSet old = dataModel;
		dataModel = dsp;
		
		plot.settings().setScanNumber( dsp.firstNonNullScanIndex() );
		
		
		plot.fitting().setFittingParameters(dataModel.energyPerChannel());
				
		if (plot.mapping() != null) plot.mapping().mapsController.setInterpolation(0);
		
		plot.history().clearUndos();
			
		// really shouldn't have to do this, but there is a reference to old datasets floating around somewhere
		// (task listener?) which is preventing them from being garbage-collected
		if (old != null && old != dsp) old.discard();
	
		updateListeners();

		

	}
	
	@Override
	public void setDataSource(DataSource ds)
	{
		setDataSetProvider(new StandardDataSet(ds));
	}


	

	
	@Override
	public Discards getDiscards() {
		return discards;
	}
	
	


	
	public ExecutorSet<MapResultSet> TASK_calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type)
	{
		return MapTS.calculateMap(dataModel, filters, fittings, type);
	}
	

	
	

	
	
	@Override
	public Iterator<Spectrum> getScanIterator()
	{
		
		return new Iterator<Spectrum>() {

			int nextIndex = dataModel.firstNonNullScanIndex();
			Spectrum next = dataModel.getScanData().get(nextIndex);
			
			
			@Override
			public boolean hasNext()
			{
				return next != null;
			}

			@Override
			public Spectrum next()
			{
				Spectrum current = next;
				nextIndex = dataModel.firstNonNullScanIndex(nextIndex+1);
				if (nextIndex == -1) {
					next = null;
				} else {
					next = dataModel.getScanData().get(nextIndex);
				}
				return current;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}};
		
	}
	




	
	
	
}
