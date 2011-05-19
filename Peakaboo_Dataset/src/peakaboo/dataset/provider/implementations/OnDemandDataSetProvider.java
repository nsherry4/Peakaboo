package peakaboo.dataset.provider.implementations;



import java.util.ArrayList;
import java.util.List;

import commonenvironment.AbstractFile;
import commonenvironment.IOOperations;

import fava.datatypes.Maybe;
import fava.signatures.FnEach;
import fava.signatures.FnGet;
import fava.signatures.FnMap;
import static fava.Fn.*;

import peakaboo.common.Version;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.dataset.mapping.MapTS;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.DataSourceDimensions;
import peakaboo.fileio.DataSourceExtendedInformation;
import peakaboo.fileio.implementations.MCADataSource;
import peakaboo.fileio.implementations.PlainTextDataSource;
import peakaboo.fileio.implementations.cdfml.CDFMLSaxDataSource;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.workers.AbstractPlural;
import plural.workers.EmptyMap;
import plural.workers.EmptyProgressingMap;
import plural.workers.PluralSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



/**
 * This class contains a set of data. Given a data set, it calculated the average and max. This allows this data to be
 * calculated once and accessed many times without adding cache logic elsewhere in the programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class OnDemandDataSetProvider extends DataSetProvider
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


	public OnDemandDataSetProvider()
	{
		super();
	}
	
	
	public OnDemandDataSetProvider(DataSource ds)
	{
		super();
		
		readDataSource(ds, null, "");
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
		List<Spectrum> badScans = map(excludedIndcies, new FnMap<Integer, Spectrum>() {

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
	public PluralSet<MapResultSet> calculateMap(final FilterSet filters, final FittingSet fittings, FittingTransform type)
	{

		return MapTS.calculateMap(dataSource, filters, fittings, type);
		
	}


	/**
	 * Reads the list of {@link AbstractFile}s as a {@link DataSource}
	 * @param files the files to read as a {@link DataSource}
	 * @return {@link PluralSet} which, when complated, returns a Boolean indicating success
	 */
	public PluralSet<Maybe<Boolean>> TASK_readFileListAsDataset(final List<AbstractFile> files)
	{

		
		// sort the filenames property
		IOOperations.sortAbstractFiles(files);

		// Create the tasklist for reading the files
		final PluralSet<Maybe<Boolean>> tasklist;
		
		
		final EmptyMap opening = new EmptyMap("Opening Data Set");
		final EmptyProgressingMap reading = new EmptyProgressingMap("Reading Scans");
		final EmptyProgressingMap applying = new EmptyProgressingMap("Calculating Values");
		
		tasklist = new PluralSet<Maybe<Boolean>>("Opening Data Set") {

			@Override
			protected Maybe<Boolean> doMaps()
			{
				
				final int fileCount;
				final DataSource dataSource;
				
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
				
				FnGet<Boolean> isAborted = new FnGet<Boolean>(){

					public Boolean f()
					{
						return isAborted() || isAbortRequested();
					}};
				
				FnEach<Integer> readScans = new FnEach<Integer>(){

					public void f(Integer count)
					{
						reading.workUnitCompleted(count);
					}
				};
				
				
				//get the correct kind of DataSource
				
				if (PlainTextDataSource.filesMatchCriteria(files))
				{
					try
					{
						dataSource = new PlainTextDataSource(files.get(0), gotScanCount, readScans, isAborted);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						aborted();
						return new Maybe<Boolean>();
					}
				} 
				else if (CDFMLSaxDataSource.filesMatchCriteria(files))
				{
										
					try
					{
						dataSource = new CDFMLSaxDataSource(files.get(0), gotScanCount, readScans, isAborted);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						aborted();
						return new Maybe<Boolean>();
					}
				}
				else if (MCADataSource.filesMatchCriteria(files.get(0)))
				{
										
					try
					{
						dataSource = new MCADataSource(files.get(0));
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
					dataSource = null;
				}
				
				
				
				if (dataSource == null) 
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
				readDataSource(dataSource, applying, IOOperations.getFilePath(files.get(0).getFileName()));
				
				//we're done
				applying.workUnitCompleted();
				applying.advanceState();
				
				return new Maybe<Boolean>(true);
				
			}
			
		};
		
		tasklist.addTask(opening);
		tasklist.addTask(reading);
		tasklist.addTask(applying);
				
		return tasklist;

	}


	@Override
	public int firstNonNullScanIndex()
	{
		return DataSetProvider.firstNonNullScanIndex(dataSource, 0);
	}
	
	@Override
	public int firstNonNullScanIndex(int start)
	{
		return DataSetProvider.firstNonNullScanIndex(dataSource, start);
	}
	
	@Override
	public int lastNonNullScanIndex()
	{
		return DataSetProvider.lastNonNullScanIndex(dataSource, dataSource.getScanCount()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		return DataSetProvider.lastNonNullScanIndex(dataSource, upto);
	}
	
	
	private void readDataSource(DataSource ds, AbstractPlural applying, String dataSourcePath)
	{
		
		if (ds == null || ds.getScanCount() == 0) return;
				

		
		int nonNullScanIndex = DataSetProvider.firstNonNullScanIndex(ds, 0);
		if (nonNullScanIndex == -1) return;
		Spectrum nonNullScan = ds.getScanAtIndex(nonNullScanIndex);
		if (nonNullScan == null) return;
		
		scanLength = nonNullScan.size();
		
		
		
		//if this data source has dimensions, prepare to read them
		DataSourceDimensions dims = null;
		if (ds instanceof DataSourceDimensions && ((DataSourceDimensions) ds).hasRealDimensions())
		{
		
			dims = (DataSourceDimensions) ds;

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
			
		}
		
		SpectrumCalculations.divideBy_inplace(avg, ds.getScanCount());
		
		dsc_average = avg;
		dsc_maximum = max10;
		
		maxValue = max;
		
		

		if (ds instanceof DataSourceExtendedInformation)
		{
			DataSourceExtendedInformation info = (DataSourceExtendedInformation) ds;

			hasExtendedInformation = info.hasExtendedInformation();

			CreatedBy = info.getCreator();
			Created = info.getCreationTime();
			ProjectName = info.getProjectName();
			SessionName = info.getSessionName();
			Facility = info.getFacilityName();
			Laboratory = info.getLaboratoryName();
			ExperimentName = info.getExperimentName();
			Instrument = info.getInstrumentName();
			Technique = info.getTechniqueName();
			SampleName = info.getSampleName();
			ScanName = info.getScanName();
			StartTime = info.getStartTime();
			EndTime = info.getEndTime();

		}
		else
		{
			hasExtendedInformation = false;
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
		return hasExtendedInformation;
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
	
	
}
