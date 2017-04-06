package peakaboo.controller.plotter.data;

import java.util.Iterator;
import java.util.List;

import eventful.IEventful;
import peakaboo.controller.plotter.data.discards.Discards;
import peakaboo.controller.plotter.data.discards.DiscardsList;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.dataset.DataSet;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;


public interface IDataController extends IEventful 
{

	DataSource getDataSourceForSubset(int x, int y, Coord<Integer> cstart, Coord<Integer> cend);
	
	void setDataSource(DataSource ds);
	void setDataSetProvider(DataSet dsp);
	List<DataSource> getDataSourcePlugins();
	ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<String> filenames, DataSource dsp);
	
	ExecutorSet<MapResultSet> TASK_calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type);
	

	Spectrum getAveragePlot();
	Spectrum getMaximumPlot();
	Spectrum getScanAtIndex(int index);
	float maximumIntensity();
	
	
	int firstNonNullScanIndex(int start);
	int firstNonNullScanIndex();
	int lastNonNullScanIndex(int upto);
	int lastNonNullScanIndex();
	
	Iterator<Spectrum> getScanIterator();
	
	String getDatasetName();
	String getDataSourceFolder();
	boolean hasDataSet();
	int size();
	int channelsPerScan();

	
	boolean hasMetadata();
	Metadata getMetadata();
	
	
	String getCurrentScanName();

	
	Discards getDiscards();
	
	DataSet getDataSet();
	
}
