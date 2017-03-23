package peakaboo.controller.plotter.data;

import java.util.Iterator;
import java.util.List;

import eventful.IEventful;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.dataset.AbstractDataSet;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.DataSourceMetadata;
import peakaboo.datasource.internal.AbstractDataSource;
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
	void setDataSetProvider(AbstractDataSet dsp);
	List<AbstractDataSource> getDataSourcePlugins();
	ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<String> filenames, AbstractDataSource dsp);
	
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
	
	void invalidateFilteredData();
	

	String getDatasetName();
	String getDataSourceFolder();
	boolean hasDataSet();
	boolean hasDimensions();
	int size();
	int channelsPerScan();
	Coord<Bounds<Number>> getRealDimensions();
	SISize getRealDimensionsUnits();
	Coord<Integer> getDataDimensions();
	int getDataHeight();
	int getDataWidth();
	
	
	boolean hasMetadata();
	DataSourceMetadata getMetadata();
	
	
	String getCurrentScanName();

	
	boolean getScanDiscarded(int scanNo);
	boolean getScanDiscarded();
	void setScanDiscarded(int scanNo, boolean discarded);
	void setScanDiscarded(boolean discarded);
	List<Integer> getDiscardedScanList();
	void clearDiscardedScanList();
	
}
