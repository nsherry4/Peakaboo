package peakaboo.dataset.provider.implementations;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import commonenvironment.AbstractFile;
import commonenvironment.IOOperations;

import fava.datatypes.Maybe;
import fava.functionable.FList;
import fava.signatures.FnEach;
import fava.signatures.FnGet;
import fava.signatures.FnMap;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.dataset.mapping.MapTS;
import peakaboo.dataset.provider.AbstractDataSetProvider;
import peakaboo.fileio.DataFormat;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.DSRealDimensions;
import peakaboo.fileio.datasource.AbstractDataSourcePlugin;
import peakaboo.fileio.datasource.DataSourcePluginLoader;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



/**
 * This class contains a set of data. Given a data set, it calculates the average and max. This allows this data to be
 * calculated once and accessed many times without adding cache logic elsewhere in the program.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class DataSetProvider extends AbstractDataSetProvider
{

	protected float					maxValue;
	protected int					scanLength;
	
	protected DataSource			dataSource;
	
	protected String				Created, CreatedBy, ProjectName, SessionName, Facility, Laboratory, ExperimentName,
			Instrument, Technique, SampleName, ScanName, StartTime, EndTime;

	protected Coord<Bounds<Number>>	realDimension;
	protected SISize				realUnits;
	protected Coord<Integer>		dataDimension;

	protected List<Coord<Number>>	realCoords;


	public DataSetProvider()
	{
		super();
	}
	
	
	public DataSetProvider(DataSource ds)
	{
		super();
		
		readDataSource(ds, null, null, "");
		dataSource = ds;
		
	}


	@Override
	public Spectrum averagePlot()
	{
		return new Spectrum(dsc_average);
	}


	@Override
	public Spectrum averagePlot(final List<Integer> excludedIndcies)
	{

		if (excludedIndcies.size() == 0) return averagePlot();

		
		//Filter for *JUST* the scans which have been marked as bad
		FList<Spectrum> badScans = FList.wrap(excludedIndcies).map(new FnMap<Integer, Spectrum>() {

			public Spectrum f(Integer index)
			{
				return dataSource.getScanAtIndex(index);
			}
		});

		Spectrum Ae;
		Spectrum At;
		int Nt, Ne;

		// At - Total average for whole dataset
		// Ae - average for excluded data set (ie just the bad ones)
		// Nt = Total number of scans in whole dataset
		// Ne - number of bad scans
		//
		// In order to figure out what the new average should be once we exclude the bad scans
		// we could recalculate from the good scans, but that would take a long time. We will operate
		// under the assumption that there will be many more good scans than bad scans.
		// so calculating the average of the bad scans should be relatively fast.
		// (At - Ae*(Ne/Nt)) * (Nt/(Nt-Ne))

		Ae = SpectrumCalculations.getDatasetAverage(badScans);
		At = dsc_average;
		Nt = dataSource.getScanCount();
		Ne = badScans.size();

		// if all scans are marked as bad, lets just return a list of 0s of the same length as the average scan
		if (Nt == Ne)
		{
			return new Spectrum(new Spectrum(dsc_average.size(), 0.0f));
		}

		float Net = (float) Ne / (float) Nt;
		float Ntte = Nt / ((float) Nt - (float) Ne);

		Spectrum goodAverage = new Spectrum(dsc_average.size());
		for (int i = 0; i < dsc_average.size(); i++)
		{
			goodAverage.set(i, (At.get(i) - Ae.get(i) * Net) * Ntte);
		}

		return new Spectrum(goodAverage);

	}


	@Override
	public Spectrum maximumPlot()
	{
		return new Spectrum(dsc_maximum);
	}


	@Override
	public float maximumIntensity()
	{
		if (dataSource.getScanCount() == 0) return 0;
				
		return maxValue;
	}


	@Override
	public Spectrum getScan(int index)
	{
		Spectrum original = dataSource.getScanAtIndex(index);
		if (original == null) return null;
		return new Spectrum(original);

	}


	@Override
	public String getScanName(int index)
	{
		if (dataSource == null || index >= dataSource.getScanCount()) return "";
		return dataSource.getScanNames().get(index);
	}


	@Override
	public int scanCount()
	{
		return dataSource.getScanCount();
	}

	@Override
	public int expectedScanCount()
	{
		if (hasDimensions) return dataDimension.x * dataDimension.y;
		return scanCount();
	}

	@Override
	public void invalidateFilteredData()
	{
		
	}


	@Override
	public ExecutorSet<MapResultSet> calculateMap(final FilterSet filters, final FittingSet fittings, FittingTransform type)
	{

		return MapTS.calculateMap(dataSource, filters, fittings, type);
		
	}


	/**
	 * Reads the list of {@link AbstractFile}s as a {@link DataSource}
	 * @param files the files to read as a {@link DataSource}
	 * @return {@link ExecutorSet} which, when complated, returns a Boolean indicating success
	 */
	public ExecutorSet<Maybe<Boolean>> TASK_readFileListAsDataset(final List<AbstractFile> files)
	{

		
		// sort the filenames property
		Collections.sort(files);
		
		// Create the tasklist for reading the files
		final ExecutorSet<Maybe<Boolean>> tasklist;
		
		/*
		final EmptyMap opening = new EmptyMap("Opening Data Set");
		final EmptyProgressingMap reading = new EmptyProgressingMap("Reading Scans");
		final EmptyProgressingMap applying = new EmptyProgressingMap("Calculating Values");
		*/
		
		final DummyExecutor opening = new DummyExecutor(true);//"Opening Data Set");
		final DummyExecutor reading = new DummyExecutor();//"Reading Scans");
		final DummyExecutor applying = new DummyExecutor();//"Calculating Values");
		
		
		tasklist = new ExecutorSet<Maybe<Boolean>>("Opening Data Set") {

			@Override
			protected Maybe<Boolean> doMaps()
			{
				
				final int fileCount;
				final AbstractDataSourcePlugin dataSource;
				
				opening.advanceState();
				
				//anon function to call when we get the number of scans
				FnEach<Integer> gotScanCount = new FnEach<Integer>() {
					
					public void f(Integer count)
					{
						reading.setWorkUnits(count);
						opening.advanceState();
						reading.advanceState();
					}
				};
				
				//anon function to call to check if the user has requested the operation be aborted
				FnGet<Boolean> isAborted = new FnGet<Boolean>(){

					public Boolean f()
					{
						return isAborted() || isAbortRequested();
					}};
				
				//anon function to call when the loader reads a scan from the input data
				FnEach<Integer> readScans = new FnEach<Integer>(){

					public void f(Integer count)
					{
						reading.workUnitCompleted(count);
					}
				};
				
				
				//get the correct kind of DataSource
				List<String> filenames = new ArrayList<String>();
				for (AbstractFile file : files) {   filenames.add(file.getFileName());   }
				
				dataSource = findDataSourceForFiles(filenames);
				
				if (dataSource != null)
				{
					try
					{
						dataSource.setCallbacks(gotScanCount, readScans, isAborted);
						if (files.size() == 1)
						{
							dataSource.read(filenames.get(0));
						}
						else
						{
							dataSource.read(filenames);
						}
					
					}
					catch (Exception e)
					{
						e.printStackTrace();
						aborted();
						return new Maybe<Boolean>();
					}
					
				}
				else 
				{
					aborted();
					return new Maybe<Boolean>();
				}
				
				if (isAborted.f())
				{
					aborted();
					return new Maybe<Boolean>(false);
				}
				
				
				fileCount = dataSource.getScanCount();
				gotScanCount.f(fileCount);
				reading.advanceState();
				
				applying.setWorkUnits(dataSource.getScanCount());
				applying.advanceState();
				
				
				//now that we have the datasource, read it
				readDataSource(  dataSource, applying, isAborted, new File(files.get(0).getFileName()).getParent()  );
				
				
				if (isAborted.f())
				{
					aborted();
					return new Maybe<Boolean>(false);
				}
				
				
				//we're done
				applying.workUnitCompleted();
				applying.advanceState();
				
				return new Maybe<Boolean>(true);
				
			}
			
		};
		
		tasklist.addExecutor(opening, "Opening Data Set");
		tasklist.addExecutor(reading, "Reading Scans");
		tasklist.addExecutor(applying, "Calculating Values");
				
		return tasklist;

	}


	private AbstractDataSourcePlugin findDataSourceForFiles(List<String> filenames)
	{

		
		List<AbstractDataSourcePlugin> datasources = DataSourcePluginLoader.getDataSourcePlugins();
		
		if (filenames.size() == 1)
		{
			String filename = filenames.get(0);
			
			for (AbstractDataSourcePlugin datasource : datasources)
			{
			
				if ( !matchFileExtension(filename, datasource.getFileExtensions()) ) continue;
				if ( !datasource.canRead(filename) ) continue;
				return datasource;

				
			}//for datasources
		}
		else
		{
		
			//loop over every datasource
			for (AbstractDataSourcePlugin datasource : datasources)
			{
				
				if ( !matchFileExtensions(filenames, datasource.getFileExtensions()) ) continue;
				if ( !datasource.canRead(filenames) ) continue;
				return datasource;
				
			}
			
		}
		
		return null;
		
	}
	
	private boolean matchFileExtension(String filename, Collection<String> dsexts)
	{
		for (String dsext : dsexts)
		{
			
			if (IOOperations.getFileExt(filename).compareToIgnoreCase(dsext) == 0) return true;
		}
		return false;
	}
	
	private boolean matchFileExtensions(Collection<String> filenames, Collection<String> dsexts)
	{
		for (String filename : filenames)
		{
			if (!matchFileExtension(filename, dsexts)) return false;
		}
		return true;
	}
	
	
	@Override
	public int firstNonNullScanIndex()
	{
		return AbstractDataSetProvider.firstNonNullScanIndex(dataSource, 0);
	}
	
	@Override
	public int firstNonNullScanIndex(int start)
	{
		return AbstractDataSetProvider.firstNonNullScanIndex(dataSource, start);
	}
	
	@Override
	public int lastNonNullScanIndex()
	{
		return AbstractDataSetProvider.lastNonNullScanIndex(dataSource, dataSource.getScanCount()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		return AbstractDataSetProvider.lastNonNullScanIndex(dataSource, upto);
	}
	
	
	private void readDataSource(DataSource ds, DummyExecutor applying, FnGet<Boolean> isAborted, String dataSourcePath)
	{
		
		if (ds == null || ds.getScanCount() == 0) return;
				

		
		int nonNullScanIndex = AbstractDataSetProvider.firstNonNullScanIndex(ds, 0);
		if (nonNullScanIndex == -1) return;
		Spectrum nonNullScan = ds.getScanAtIndex(nonNullScanIndex);
		if (nonNullScan == null) return;
		
		scanLength = nonNullScan.size();
		
		
		
		//if this data source has dimensions, prepare to read them
		DSRealDimensions dims = null;
		if (ds.hasRealDimensions())
		{
		
			dims = (DSRealDimensions) ds;

			hasDimensions = true;

			dataDimension = dims.getDataDimensions();
			realDimension = dims.getRealDimensions();

			realCoords = new ArrayList<Coord<Number>>();


			realUnits = getSISizeFromUnitName(dims.getRealDimensionsUnit());


		}
		else
		{
			hasDimensions = false;
		}
		
		
		//go over each scan, calculating the average, max10th and max value
		float max = Float.MIN_VALUE;
		Spectrum avg, max10, current;
		
		avg = new Spectrum(scanLength);
		max10 = new Spectrum(scanLength);
		
		
		for (int i = 0; i < ds.getScanCount(); i++)
		{
			current = ds.getScanAtIndex(i);
			
			if (current == null) continue;
			
			SpectrumCalculations.addLists_inplace(avg, current);
			SpectrumCalculations.maxlist_inplace(max10, current);
			
			max = Math.max(max, SpectrumCalculations.max(current));
			
			//read the real coordinates for this scan
			if (hasDimensions) realCoords.add(dims.getRealCoordinatesAtIndex(i));
			
			
			if (applying != null) applying.workUnitCompleted();
			if (isAborted != null && isAborted.f()) return;
			
		}
		
		SpectrumCalculations.divideBy_inplace(avg, ds.getScanCount());
		
		dsc_average = avg;
		dsc_maximum = max10;
		
		maxValue = max;
		
		

		if (ds.hasMetadata())
		{
			
			hasMetadata = true;

			CreatedBy = ds.getCreator();
			Created = ds.getCreationTime();
			ProjectName = ds.getProjectName();
			SessionName = ds.getSessionName();
			Facility = ds.getFacilityName();
			Laboratory = ds.getLaboratoryName();
			ExperimentName = ds.getExperimentName();
			Instrument = ds.getInstrumentName();
			Technique = ds.getTechniqueName();
			SampleName = ds.getSampleName();
			ScanName = ds.getScanName();
			StartTime = ds.getStartTime();
			EndTime = ds.getEndTime();

		}
		else
		{
			hasMetadata = false;
		}

		this.dataSourcePath = dataSourcePath;
		this.dataSource = ds;
		

	}
	

	private SISize getSISizeFromUnitName(String unitName)
	{

		unitName = unitName.toLowerCase();

		for (SISize s : SISize.values())
		{
			if (unitName.equals(s.toString())) return s;
		}

		return SISize.mm;

	}


	@Override
	public String getDatasetName()
	{
		return dataSource.getDatasetName();
	}


	@Override
	public Coord<Integer> getDataDimensions()
	{
		if (hasDimensions)
		{
			return dataDimension;
		}
		return new Coord<Integer>(dataSource.getScanCount(), 1);
	}


	@Override
	public List<Coord<Number>> getCoordinateList()
	{
		if (hasDimensions)
		{
			return realCoords;
		}
		return null;
	}


	@Override
	public Coord<Bounds<Number>> getRealDimensions()
	{
		if (hasDimensions)
		{
			return realDimension;
		}
		return null;
	}


	@Override
	public SISize getRealDimensionsUnits()
	{
		if (hasDimensions)
		{
			return realUnits;
		}
		return null;
	}


	@Override
	public boolean hasData()
	{
		return dataSource.getScanCount() > 0;
	}


	@Override
	public boolean hasDimensions()
	{
		return hasDimensions;
	}


	@Override
	public boolean hasExtendedInformation()
	{
		return hasMetadata;
	}


	@Override
	public String getCreationTime()
	{
		return Created;
	}


	@Override
	public String getCreator()
	{
		return CreatedBy;
	}


	@Override
	public String getEndTime()
	{
		return EndTime;
	}


	@Override
	public String getExperimentName()
	{
		return ExperimentName;
	}


	@Override
	public String getFacilityName()
	{
		return Facility;
	}


	@Override
	public String getInstrumentName()
	{
		return Instrument;
	}


	@Override
	public String getLaboratoryName()
	{
		return Laboratory;
	}


	@Override
	public String getProjectName()
	{
		return ProjectName;
	}


	@Override
	public String getSampleName()
	{
		return SampleName;
	}


	@Override
	public String getScanName()
	{
		return ScanName;
	}


	@Override
	public String getSessionName()
	{
		return SessionName;
	}


	@Override
	public String getStartTime()
	{
		return StartTime;
	}


	@Override
	public String getTechniqueName()
	{
		return Technique;
	}


	@Override
	public void discard()
	{
		//discard references to large chunks of data
		dataSource = null;
	}


	@Override
	public int scanSize()
	{
		// TODO Auto-generated method stub
		return scanLength;
	}


	@Override
	public DataSource getDataSource()
	{
		return dataSource;
	}
	
	
	
	
	
	
	
	
	/*
	 * 
	 * STATIC METHODS
	 * 
	 */
	

	public static List<DataFormat> getDataFormats()
	{
		return DataSourcePluginLoader.getDataFormats();
	}
	
}
