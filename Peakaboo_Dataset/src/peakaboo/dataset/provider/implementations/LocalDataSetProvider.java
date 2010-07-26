package peakaboo.dataset.provider.implementations;



import java.util.List;

import commonenvironment.AbstractFile;
import commonenvironment.IOOperations;

import fava.datatypes.Bounds;
import fava.signatures.FunctionMap;
import static fava.Fn.*;

import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.fileio.CDFMLDataSource;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.DataSourceDimensions;
import peakaboo.fileio.DataSourceExtendedInformation;
import peakaboo.fileio.XMLDataSource;
import peakaboo.fileio.ZipDataSource;
import peakaboo.fileio.DataSource.FileType;
import peakaboo.filter.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import plural.workers.EmptyMap;
import plural.workers.PluralEachIndex;
import plural.workers.PluralSet;
import plural.workers.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.workers.executor.eachindex.implementations.PluralUIEachIndexExecutor;
import plural.workers.executor.eachindex.implementations.SimpleUIEachIndexExecutor;
import plural.workers.executor.maps.implementations.PluralUIMapExecutor;
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

public class LocalDataSetProvider extends DataSetProvider
{

	protected List<Spectrum>		dsc_dataset;
	protected List<String>			dsc_scannames;
	protected List<Spectrum>		filteredDataSet;
	protected Boolean				filteredDataInvalid;

	protected float					maxIntensity;
	
	protected String				datasetName;

	protected String				Created, CreatedBy, ProjectName, SessionName, Facility, Laboratory, ExperimentName,
			Instrument, Technique, SampleName, ScanName, StartTime, EndTime;

	protected Coord<Bounds<Number>>	realDimension;
	protected SISize				realUnits;
	protected Coord<Integer>		dataDimension;

	protected List<Coord<Number>>	realCoords;


	public LocalDataSetProvider()
	{
		super();
	}


	public LocalDataSetProvider(List<Spectrum> dataset)
	{
		super();
		setDataset(dataset);
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
		List<Spectrum> badScans = map(excludedIndcies, new FunctionMap<Integer, Spectrum>(){

			public Spectrum f(Integer index) {
				return dsc_dataset.get(index);
			}
		});
		
		/*List<Spectrum> badScans = filter_index(dsc_dataset, new Function11<Integer, Boolean>() {

			public Boolean f(Integer element)
			{
				return (excludedIndcies.indexOf(element) != -1);
			}
		});*/

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
		Nt = dsc_dataset.size();
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
		if (dsc_dataset.size() == 0) return 0;		
		return maxIntensity;
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
		// return dsc_dataset.get(index);
		return new Spectrum(dsc_dataset.get(index));

	}


	@Override
	public int firstNonNullScanIndex()
	{
		return firstNonNullScanIndex(0);
	}
	
	@Override
	public int firstNonNullScanIndex(int start)
	{
		for (int i = start; i < scanCount(); i++)
		{
			if (dsc_dataset.get(i) != null) return i;
		}
		return -1;
	}

	
	@Override
	public int lastNonNullScanIndex()
	{
		return lastNonNullScanIndex(dsc_dataset.size()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		upto = Math.min(upto, dsc_dataset.size()-1);
		
		for (int i = upto; i >= 0; i--)
		{
			if (dsc_dataset.get(i) != null) return i;
		}
		return -1;
	}
	
	@Override
	public String getScanName(int index)
	{
		if (dsc_scannames == null) return "";
		return dsc_scannames.get(index);
	}


	@Override
	public int scanCount()
	{
		return dsc_dataset.size();
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
		filteredDataInvalid = true;
	}


	@Override
	public PluralSet<MapResultSet> calculateMap(final FilterSet filters, final FittingSet fittings, final FittingTransform type)
	{

		final PluralSet<MapResultSet> tasklist;

		// ======================================================================
		// LOGIC FOR FILTERS: List<List<Double>> => List<List<Double>>
		// ======================================================================
		//final List<List<Double>> filteredData;


		
		final PluralEachIndex t_filter = new PluralEachIndex("Apply Filters") {

			public void f(Integer ordinal)
			{

				Spectrum data = filters.filterDataUnsynchronized(dsc_dataset.get(ordinal), false);
				filteredDataSet.set(ordinal, data);

			}

		};

		// ======================================================================================
		// LOGIC FOR FITTINGS:
		// List<List<Double>> => List<FittingResultSet> => Map<TransitionSeries, List<Double>>
		// ======================================================================================
		// Map (the data structure) to store our maps (the XRF thingy)
		final List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		final MapResultSet maps = new MapResultSet(transitionSeries, expectedScanCount());

		final PluralEachIndex t_curvefit = new PluralEachIndex("Fitting Element Curves") {

			public void f(Integer ordinal)
			{

				FittingResultSet frs = fittings.calculateFittings(filteredDataSet.get(ordinal));
				// fittingResults.set(ordinal, frs);

				for (FittingResult result : frs.fits)
				{
					maps.putIntensityInMapAtPoint(
							type == FittingTransform.AREA ? SpectrumCalculations.sumValuesInList(result.fit) : SpectrumCalculations.max(result.fit),
						result.transitionSeries,
						ordinal);
				}

			}

		};

		tasklist = new PluralSet<MapResultSet>("Generating Data for Map") {

			@Override
			public MapResultSet doMaps()
			{

				PluralUIEachIndexExecutor executor;

				// ================================
				// PROCESS FILTERS
				// ================================
				if (filteredDataInvalid)
				{

					//we'll be making changes to the filtered data, so mark it as invalid now
					filteredDataInvalid = true;
					
					// process these scans in parallel
					executor = new PluralUIEachIndexExecutor(scanCount(), t_filter, this);

					executor.executeBlocking();

					//we have completed filtering the data, we can now mark the
					//filtered data as not needing a refresh
					filteredDataInvalid = false;
					
					if (isAborted()) return null;

				}
				else
				{

					t_filter.markTaskSkipped();

				}

				// ================================
				// PROCESS FITTINGS INTO MAPS
				// ================================

				// executor which will manage the threads and have them call the
				// work() method in the task
				executor = new PluralUIEachIndexExecutor(filteredDataSet.size(), t_curvefit, this);
				executor.executeBlocking();

				if (isAborted()) return null;

				// return intensities;
				// return ListCalculations.subtractFromList(intensities, 0.0, 0.0);
				return maps;
			}

		};

		tasklist.addTask(t_filter);
		tasklist.addTask(t_curvefit);

		return tasklist;
	}


	public PluralSet<Boolean> TASK_readFileListAsDataset(final List<AbstractFile> files)
	{

		final FileType type;

		// sort the filenames property
		IOOperations.sortAbstractFiles(files);

		// Create the tasklist for reading the files
		final PluralSet<Boolean> tasklist;

		// logic for opening a file
		final List<Spectrum> dataset;
		// final List<String> usedFileNames;
		final int fileCount;

		// a data source and a task to read from it
		final DataSource dataSource;
		final PluralEachIndex reading;

		// a single zip file
		if (files.size() == 1 && files.get(0).getFileName().toLowerCase().endsWith(".zip"))
		{

			type = FileType.ZIP;

			dataSource = ZipDataSource.getArchiveFromFileName(files.get(0).getFileName());
			fileCount = dataSource.getScanCount();
			dataset = DataTypeFactory.spectrumSetInit(fileCount);
			reading = getReadingTaskForDataSource(dataSource, dataset, "Reading Zip File");

		}
		else if (files.size() == 1 && files.get(0).getFileName().toLowerCase().endsWith(".xml")
				&& CDFMLDataSource.isCDFML(files.get(0)) == 0)
		{

			type = FileType.CDFML;

			dataSource = CDFMLDataSource.getCDFMLFromFile(files.get(0));
			fileCount = dataSource.getScanCount();
			dataset = DataTypeFactory.spectrumSetInit(fileCount);
			reading = getReadingTaskForDataSource(dataSource, dataset, "Reading CDFML File");

		}
		else
		{

			type = FileType.CLSXML;

			dataSource = XMLDataSource.getXMLFileSet(files);
			fileCount = dataSource.getScanCount();
			dataset = DataTypeFactory.spectrumSetInit(fileCount);
			reading = getReadingTaskForDataSource(dataSource, dataset, "Reading XML Files");

		}

		if (dataSource == null) return null;

		final EmptyMap applying = new EmptyMap("Calculating Values");

		tasklist = new PluralSet<Boolean>("Opening Data Set") {

			@Override
			public Boolean doMaps()
			{

				// XML parser doesn't play nice with multithreading?
				if (type != FileType.CDFML)
				{
					new PluralUIEachIndexExecutor(fileCount, reading, this).executeBlocking();
					hasDimensions = false;
				}
				else
				{
					new SimpleUIEachIndexExecutor(fileCount, reading, this).executeBlocking();
				}

				if (dataSource instanceof DataSourceDimensions)
				{
					DataSourceDimensions dims = (DataSourceDimensions) dataSource;
					hasDimensions = true;

					dataDimension = dims.getDataDimensions();
					// realBottomLeft = dataSource.getRealCoordinatesAtIndex(0);
					// //cdfml.getRealCoordinatesAtIndex(0);
					// realTopRight = dataSource.getRealCoordinatesAtIndex(dataDimension.x * dataDimension.y -
					// 1); //cdfml.getRealCoordinatesAtIndex(dataDimension.x * dataDimension.y - 1);

					realDimension = dims.getRealDimensions();

					realCoords = DataTypeFactory.<Coord<Number>> list();
					for (int i = 0; i < fileCount; i++)
					{
						realCoords.add(dims.getRealCoordinatesAtIndex(i));
					}

					realUnits = getSISizeFromUnitName(dims.getRealDimensionsUnit());

				}
				else
				{
					hasDimensions = false;
				}

				if (dataSource instanceof DataSourceExtendedInformation)
				{
					DataSourceExtendedInformation info = (DataSourceExtendedInformation) dataSource;

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

				if (isAborted()) return false;

				dataSourcePath = IOOperations.getFilePath(files.get(0).getFileName());

				int index;
				while (true)
				{

					index = dataset.indexOf(null);
					if (index < 0) break;
					dataset.remove(index);

				}

				if (dataset.size() > 0)
				{

					applying.advanceState();
					// set the data as the data for this datasetcontainer
					setDataset(dataset);
					dsc_scannames = dataSource.getScanNames();
					// model.dr.maxIntensity = data.maximumIntensity();
					// model.dataset = data;

					datasetName = dataSource.getDatasetName();
					setMaxEnergy(dataSource.getMaxEnergy());

					applying.workUnitCompleted();
					applying.advanceState();

					return true;

				}
				else
				{
					return false;
				}
			}

		};

		tasklist.addTask(reading);
		tasklist.addTask(applying);

		return tasklist;

	}


	private PluralEachIndex getReadingTaskForDataSource(final DataSource dataSource, final List<Spectrum> targetDataset,
			String title)
	{

		return new PluralEachIndex(title) {

			public void f(Integer index)
			{
				Spectrum dataFromFile = dataSource.getScanAtIndex(index);
				if (dataFromFile != null)
				{
					targetDataset.set(index, dataFromFile);
				}
				else
				{
					dataSource.markScanAsBad(index);
				}
				return;

			}
			
		};

	}


	public void readFileListAsDataset(final List<AbstractFile> files)
	{
		PluralSet<Boolean> tl = TASK_readFileListAsDataset(files);
		if (tl == null) return;
		tl.startWorkingBlocking();
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
		return new Coord<Integer>(dsc_dataset.size(), 1);
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
		return true;
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

	
	
	
	
	
	
	
	
	
	
	
	

	//Sets the scan data and all of the precalculated values 
	private void setDataset(List<Spectrum> dataset)
	{

		this.dsc_dataset = dataset;
		this.filteredDataSet = DataTypeFactory.spectrumSetInit(scanCount());
		
		dsc_scannames = DataTypeFactory.<String> list();
		for (int i = 0; i < dataset.size(); i++)
		{
			dsc_scannames.add("Scan #" + i);
		}

		dsc_average = SpectrumCalculations.getDatasetAverage(dataset);
		dsc_maximum = SpectrumCalculations.getDatasetMaximums(dataset);

		maxIntensity = SpectrumCalculations.maxDataset(dataset);
		
		dsc_scanSize = dsc_average.size();

	}


	@Override
	public void discard()
	{
		//discard references to large chunks of data
		dsc_dataset.clear();
		filteredDataSet.clear();
		dsc_scannames.clear();
	}


	@Override
	public int scanSize()
	{
		return dsc_scanSize;	
	}






	
	
}
