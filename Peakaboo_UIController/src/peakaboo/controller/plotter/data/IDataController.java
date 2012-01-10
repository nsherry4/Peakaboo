package peakaboo.controller.plotter.data;

import java.util.List;
import java.util.Set;

import commonenvironment.AbstractFile;
import eventful.IEventful;
import fava.datatypes.Maybe;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.dataset.provider.AbstractDataSetProvider;
import peakaboo.fileio.DataSource;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.ExecutorSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;


public interface IDataController extends IEventful 
{

	public DataSource getDataSourceForSubset(int x, int y, Coord<Integer> cstart, Coord<Integer> cend);
	
	public void setDataSource(DataSource ds);
	public void setDataSetProvider(AbstractDataSetProvider dsp);
	public Set<String> getSupportedFileExtensions();
	public ExecutorSet<Maybe<Boolean>> TASK_readFileListAsDataset(final List<AbstractFile> files);
	
	public ExecutorSet<MapResultSet> TASK_calculateMap(FilterSet filters, FittingSet fittings, FittingTransform type);
	

	public Spectrum getAveragePlot();
	public Spectrum getMaximumPlot();
	public Spectrum getScanAtIndex(int index);
	public float maximumIntensity();
	
	
	public int firstNonNullScanIndex(int start);
	public int firstNonNullScanIndex();
	public int lastNonNullScanIndex(int upto);
	public int lastNonNullScanIndex();
	
	public void invalidateFilteredData();
	

	public String getDatasetName();
	public String getDataSourceFolder();
	public boolean hasDataSet();
	public boolean hasDimensions();
	public int datasetScanCount();
	public int datasetScanSize();
	public Coord<Bounds<Number>> getRealDimensions();
	public SISize getRealDimensionsUnits();
	public Coord<Integer> getDataDimensions();
	public int getDataHeight();
	public int getDataWidth();
	
	

	public boolean getScanHasExtendedInformation();
	public String getScanCreationTime();
	public String getScanCreator();
	public String getScanEndTime();
	public String getScanExperimentName();
	public String getScanFacilityName();
	public String getScanInstrumentName();
	public String getScanLaboratoryName();
	public String getScanProjectName();
	public String getScanSampleName();
	public String getScanScanName();
	public String getScanSessionName();
	public String getScanStartTime();
	public String getScanTechniqueName();
	
	
	public String getCurrentScanName();

	
	public boolean getScanDiscarded(int scanNo);
	public boolean getScanDiscarded();
	public void setScanDiscarded(int scanNo, boolean discarded);
	public void setScanDiscarded(boolean discarded);
	public List<Integer> getDiscardedScanList();
	public void clearDiscardedScanList();
	
}
