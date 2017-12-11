package peakaboo.controller.plotter.data;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import eventful.IEventful;
import peakaboo.controller.plotter.data.discards.Discards;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.dataset.DataSet;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.datasource.DataSource;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import scitypes.Coord;
import scitypes.ReadOnlySpectrum;


public interface IDataController extends IEventful 
{

	DataSource getDataSourceForSubset(int x, int y, Coord<Integer> cstart, Coord<Integer> cend);
	
	void setDataSource(DataSource ds);
	void setDataSetProvider(DataSet dsp);
	
	ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<File> files, DataSource dsp);
	ExecutorSet<MapResultSet> TASK_calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type);
	

	Iterator<ReadOnlySpectrum> getScanIterator();
	boolean hasDataSet();
	
	Discards getDiscards();
	DataSet getDataSet();
	
}
