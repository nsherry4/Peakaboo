package peakaboo.dataset.provider.implementations;



import java.util.List;

import fava.*;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;
import static fava.Fn.*;
import static fava.Functions.*;

import peakaboo.calculations.SpectrumCalculations;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.dataset.mapping.MapTS;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.SISize;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.tasks.EmptyProgressingTask;
import peakaboo.datatypes.tasks.EmptyTask;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.datatypes.tasks.executor.implementations.TicketingUITaskExecutor;
import peakaboo.fileio.xrf.CDFMLDataSource;
import peakaboo.fileio.xrf.CDFMLSaxDataSource;
import peakaboo.fileio.xrf.DataSource;
import peakaboo.fileio.xrf.DataSourceDimensions;
import peakaboo.fileio.xrf.DataSourceExtendedInformation;
import peakaboo.fileio.xrf.XMLDataSource;
import peakaboo.fileio.xrf.ZipDataSource;
import peakaboo.filters.FilterSet;
import peakaboo.mapping.results.MapResultSet;
import swidget.dialogues.fileio.AbstractFile;
import swidget.dialogues.fileio.IOCommon;



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
	
	protected String				datasetName;

	protected String				Created, CreatedBy, ProjectName, SessionName, Facility, Laboratory, ExperimentName,
			Instrument, Technique, SampleName, ScanName, StartTime, EndTime;

	protected Coord<Range<Number>>	realDimension;
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
		List<Spectrum> badScans = map(excludedIndcies, new FunctionMap<Integer, Spectrum>() {

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
		float Ntte = (float) Nt / ((float) Nt - (float) Ne);

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


	/*@Override
	public List<Double> calculateSumInRegion(ROI region)
	{
		List<Double> sums = DataTypeFactory.<Double> list();

		for (List<Double> scan : dsc_dataset)
		{
			sums.add(ROICalculations.getSumInRegion(scan, region));
		}

		return sums;
	}*/


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
	public TaskList<MapResultSet> calculateMap(final FilterSet filters, final FittingSet fittings)
	{

		return MapTS.calculateMap(dataSource, filters, fittings);
		/*
		final TaskList<MapResultSet> tasklist;

		// ======================================================================
		// LOGIC FOR FILTERS AND FITTING
		// Original => Filtered => Fittings => Stored-In-Map
		// ======================================================================
		//final List<List<Double>> filteredData;

		final List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		final MapResultSet maps = new MapResultSet(transitionSeries, expectedScanCount());
		
		final Task t_filter = new Task("Apply Filters and Fittings") {

			@Override
			public boolean work(int ordinal)
			{

				Spectrum data = filters.filterDataUnsynchronized(dataSource.getScanAtIndex(ordinal), false);
				//filteredDataSet.set(ordinal, data);
				
				FittingResultSet frs = fittings.calculateFittings(data);
				// fittingResults.set(ordinal, frs);

				for (FittingResult result : frs.fits)
				{
					maps.putIntensityInMapAtPoint(
						SpectrumCalculations.sumValuesInList(result.fit),
						result.transitionSeries,
						ordinal);
				}

				return true;
				

			}

		};


		tasklist = new TaskList<MapResultSet>("Generating Data for Map") {

			@Override
			public MapResultSet doTasks()
			{

				TicketingUITaskExecutor executor;

				// ================================
				// PROCESS FILTERS, FITTINGS
				// ================================
				

					
				// process these scans in parallel
				executor = new TicketingUITaskExecutor(scanCount(), t_filter, this);
				executor.executeBlocking();
				
				if (isAborted()) return null;


				// return intensities;
				// return ListCalculations.subtractFromList(intensities, 0.0, 0.0);
				return maps;
			}

		};

		tasklist.addTask(t_filter);

		// tasklist.addTask(t_scanToMaps);

		return tasklist;*/
	}


	public TaskList<Boolean> TASK_readFileListAsDataset(final List<AbstractFile> files)
	{

		
		// sort the filenames property
		IOCommon.sortFiles(files);

		// Create the tasklist for reading the files
		final TaskList<Boolean> tasklist;
		
		
		final EmptyTask opening = new EmptyTask("Opening Data Set");
		final EmptyProgressingTask reading = new EmptyProgressingTask("Reading Scans");
		final EmptyProgressingTask applying = new EmptyProgressingTask("Calculating Values");
		
		tasklist = new TaskList<Boolean>("Opening Data Set") {

			@Override
			protected Boolean doTasks()
			{
				
				final int fileCount;
				final DataSource dataSource;
				
				opening.advanceState();
				
				//anon function to call when we get the number of scans
				FunctionEach<Integer> gotScanCount = new FunctionEach<Integer>() {
					
					public void f(Integer count)
					{
						reading.setWorkUnits(count);
						opening.advanceState();
						reading.advanceState();
					}
				};
				
				FunctionMap<Boolean, Boolean> isAborted = new FunctionMap<Boolean, Boolean>(){

					public Boolean f(Boolean element)
					{
						return isAborted() || isAbortRequested();
					}};
				
				FunctionEach<Integer> readScans = new FunctionEach<Integer>(){

					public void f(Integer count)
					{
						reading.workUnitCompleted(count);
					}
				};
				
				
				//get the correct kind of DataSource
				if (files.size() == 1 && files.get(0).getFileName().toLowerCase().endsWith(".zip"))
				{
					dataSource = ZipDataSource.getArchiveFromFileName(files.get(0).getFileName());
				}
				else if (files.size() == 1 && files.get(0).getFileName().toLowerCase().endsWith(".xml"))
				{
					dataSource = new CDFMLSaxDataSource(files.get(0), gotScanCount, readScans, isAborted);
				}
				else if (files.get(0).getFileName().toLowerCase().endsWith(".xml"))
				{
					dataSource = XMLDataSource.getXMLFileSet(files);
				} 
				else 
				{
					dataSource = null;
				}
				
				
				fileCount = dataSource.getScanCount();
				gotScanCount.f(fileCount);
				
				
				reading.advanceState();
				
				if (dataSource == null || isAborted.f(true)) 
				{
					aborted();
					return null;
				}
				
				applying.setWorkUnits(dataSource.getScanCount());
				applying.advanceState();
				
				
				//now that we have the datasource, read it
				readDataSource(dataSource, applying, IOCommon.getFilePath(files.get(0).getFileName()));
				
				//we're done
				applying.workUnitCompleted();
				applying.advanceState();
				
				return true;
				
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
	
	
	private void readDataSource(DataSource ds, Task applying, String dataSourcePath)
	{
		
		if (ds == null || ds.getScanCount() == 0) return;
				

		
		
		Spectrum nonNullScan = ds.getScanAtIndex(DataSetProvider.firstNonNullScanIndex(ds, 0));
		if (nonNullScan == null) return;
		scanLength = nonNullScan.size();
		
		
		
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
			
			if (applying != null) applying.workUnitCompleted();
			
		}
		
		SpectrumCalculations.divideBy_inplace(avg, ds.getScanCount());
		
		dsc_average = avg;
		dsc_maximum = max10;
		
		maxValue = max;
		
		
		
		if (ds instanceof DataSourceDimensions)
		{
			DataSourceDimensions dims = (DataSourceDimensions) ds;
			hasDimensions = true;

			dataDimension = dims.getDataDimensions();
			// realBottomLeft = dataSource.getRealCoordinatesAtIndex(0);
			// //cdfml.getRealCoordinatesAtIndex(0);
			// realTopRight = dataSource.getRealCoordinatesAtIndex(dataDimension.x * dataDimension.y -
			// 1); //cdfml.getRealCoordinatesAtIndex(dataDimension.x * dataDimension.y - 1);

			realDimension = dims.getRealDimensions();

			realCoords = DataTypeFactory.<Coord<Number>> list();
			for (int i = 0; i < ds.getScanCount(); i++)
			{
				realCoords.add(dims.getRealCoordinatesAtIndex(i));
			}

			realUnits = getSISizeFromUnitName(dims.getRealDimensionsUnit());

		}
		else
		{
			hasDimensions = false;
		}

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
		return datasetName;
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
	public Coord<Range<Number>> getRealDimensions()
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
		//dsc_dataset.clear();
		//filteredDataSet.clear();
		//dsc_scannames.clear();
		dataSource = null;
	}


	@Override
	public int scanSize()
	{
		// TODO Auto-generated method stub
		return scanLength;
	}




	
	
}
