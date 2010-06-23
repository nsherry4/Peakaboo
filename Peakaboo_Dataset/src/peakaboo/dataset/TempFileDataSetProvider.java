package peakaboo.dataset;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import fava.*;
import static fava.Fn.*;
import static fava.Functions.*;

import peakaboo.calculations.SpectrumCalculations;
import peakaboo.common.Env;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.results.FittingResult;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.SISize;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.TempFileList;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.tasks.EmptyTask;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.datatypes.tasks.executor.implementations.SimpleUITaskExecutor;
import peakaboo.datatypes.tasks.executor.implementations.TicketingUITaskExecutor;
import peakaboo.fileio.AbstractFile;
import peakaboo.fileio.IOCommon.FileType;
import peakaboo.fileio.xrf.CDFMLDataSource;
import peakaboo.fileio.xrf.DataSource;
import peakaboo.fileio.xrf.DataSourceDimensions;
import peakaboo.fileio.xrf.DataSourceExtendedInformation;
import peakaboo.fileio.xrf.XMLDataSource;
import peakaboo.fileio.xrf.ZipDataSource;
import peakaboo.filters.FilterSet;
import peakaboo.mapping.MapResultSet;



/**
 * This class contains a set of data. Given a data set, it calculated the average and max. This allows this data to be
 * calculated once and accessed many times without adding cache logic elsewhere in the programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class TempFileDataSetProvider extends DataSetProvider
{

	/////////////////////////////////////////////////
	// WARNING!!!
	// ANY CHANGES TO THE SPECTRUMS IN DSC_DATASET
	// MUST BE RE-ADDED TO THAT LIST OR ELSE THE
	// CHANGE WILL BE WIPED OUT NEXT TIME IT IS
	// READ. THIS IMPLEMENTATION USES A LIST
	// WHICH WRITES OUT ITS VALUES TO DISK TO SAVE
	// MEMORY.
	/////////////////////////////////////////////////
	
	
	protected List<Spectrum>		dsc_dataset;
	protected List<String>			dsc_scannames;
	protected List<Spectrum>		filteredDataSet;
	protected Boolean				filteredDataInvalid;

	protected float					maxIntensity;
	
	protected String				datasetName;

	protected String				Created, CreatedBy, ProjectName, SessionName, Facility, Laboratory, ExperimentName,
			Instrument, Technique, SampleName, ScanName, StartTime, EndTime;

	protected Coord<Range<Number>>	realDimension;
	protected SISize				realUnits;
	protected Coord<Integer>		dataDimension;

	protected List<Coord<Number>>	realCoords;


	public TempFileDataSetProvider()
	{
		super();
	}


	public TempFileDataSetProvider(List<Spectrum> dataset)
	{
		super();
		setDataset(dataset);
	}


	@Override
	public ScanContainer averagePlot()
	{
		return new ScanContainer(dsc_average);
	}


	@Override
	public ScanContainer averagePlot(final List<Integer> excludedIndcies)
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
			return new ScanContainer(new Spectrum(dsc_average.size(), 0.0f));
		}

		float Net = (float) Ne / (float) Nt;
		float Ntte = (float) Nt / ((float) Nt - (float) Ne);

		Spectrum goodAverage = new Spectrum(dsc_average.size());
		for (int i = 0; i < dsc_average.size(); i++)
		{
			goodAverage.set(i, (At.get(i) - Ae.get(i) * Net) * Ntte);
		}

		return new ScanContainer(goodAverage);

	}


	@Override
	public ScanContainer maximumPlot()
	{
		return new ScanContainer(dsc_maximum);
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
	public ScanContainer getScan(int index)
	{
		// return dsc_dataset.get(index);
		return new ScanContainer(dsc_dataset.get(index));

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
	public void invalidateFilteredData()
	{
		filteredDataInvalid = true;
	}


	@Override
	public TaskList<MapResultSet> calculateMap(final FilterSet filters, final FittingSet fittings)
	{

		final TaskList<MapResultSet> tasklist;

		// ======================================================================
		// LOGIC FOR FILTERS: List<List<Double>> => List<List<Double>>
		// ======================================================================
		//final List<List<Double>> filteredData;


		
		final Task t_filter = new Task("Apply Filters") {

			@Override
			public boolean work(int ordinal)
			{

				Spectrum data = filters.filterDataUnsynchronized(dsc_dataset.get(ordinal), false);
				filteredDataSet.set(ordinal, data);
				return true;

			}

		};

		// ======================================================================================
		// LOGIC FOR FITTINGS:
		// List<List<Double>> => List<FittingResultSet> => Map<TransitionSeries, List<Double>>
		// ======================================================================================
		// Map (the data structure) to store our maps (the XRF thingy)
		final List<TransitionSeries> transitionSeries = fittings.getVisibleTransitionSeries();
		final MapResultSet maps = new MapResultSet(transitionSeries, scanCount());

		final Task t_curvefit = new Task("Fitting Element Curves") {

			@Override
			public boolean work(int ordinal)
			{

				FittingResultSet frs = fittings.calculateFittings(filteredDataSet.get(ordinal));
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
				// PROCESS FILTERS
				// ================================
				if (filteredDataInvalid)
				{

					//we'll be making changes to the filtered data, so mark it as invalid now
					filteredDataInvalid = true;
					
					// process these scans in parallel
					executor = new TicketingUITaskExecutor(scanCount(), t_filter, this);

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
				executor = new TicketingUITaskExecutor(filteredDataSet.size(), t_curvefit, this);
				executor.executeBlocking();

				if (isAborted()) return null;

				// return intensities;
				// return ListCalculations.subtractFromList(intensities, 0.0, 0.0);
				return maps;
			}

		};

		tasklist.addTask(t_filter);
		tasklist.addTask(t_curvefit);
		// tasklist.addTask(t_scanToMaps);

		return tasklist;
	}


	public TaskList<Boolean> TASK_readFileListAsDataset(final List<AbstractFile> files)
	{

		final FileType type;

		// sort the filenames property
		peakaboo.fileio.IOCommon.sortFiles(files);

		// Create the tasklist for reading the files
		final TaskList<Boolean> tasklist;

		// logic for opening a file
		final List<Spectrum> dataset;
		// final List<String> usedFileNames;
		final int fileCount;

		// a data source and a task to read from it
		final DataSource dataSource;
		final Task reading;

		
		
		//Functions to serialize and deserialize a spectrum should we end up using the 
		//temp file backed list
		final FunctionMap<Spectrum, byte[]> encode = new FunctionMap<Spectrum, byte[]>()
		{
			public byte[] f(Spectrum s)
			{
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos;
				try
				{
					oos = new ObjectOutputStream(baos);
					oos.writeObject(s);
					oos.close();
					return baos.toByteArray();
				}
				catch (IOException e)
				{
					return new byte[0];
				}

			}		
		};
		
		final FunctionMap<byte[], Spectrum> decode = new FunctionMap<byte[], Spectrum>()
		{
			public Spectrum f(byte[] bs)
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(bs);
				ObjectInputStream ois;
				try
				{
					ois = new ObjectInputStream(bais);
					Spectrum s = (Spectrum)ois.readObject();
					ois.close();
					return s;
				}
				catch (IOException e)
				{
					return null;
				}
				catch (ClassNotFoundException e)
				{
					return null;
				}

			}		
		};

		
		
		//Create the data source based on the kind of file(s) that we were given
		String typeName;
		if (files.size() == 1 && files.get(0).getFileName().toLowerCase().endsWith(".zip"))
		{

			type = FileType.ZIP;

			dataSource = ZipDataSource.getArchiveFromFileName(files.get(0).getFileName());
			typeName = "Zip";

		}
		else if (files.size() == 1 && files.get(0).getFileName().toLowerCase().endsWith(".xml")
				&& CDFMLDataSource.isCDFML(files.get(0)) == 0)
		{

			type = FileType.CDFML;

			dataSource = CDFMLDataSource.getCDFMLFromFile(files.get(0));
			typeName = "CDFML";

		}
		else
		{

			type = FileType.CLSXML;

			dataSource = XMLDataSource.getXMLFileSet(files);
			typeName = "XML";

		}
		if (dataSource == null) return null;

		
		//determine if we should be using the temp file backed list
		fileCount = dataSource.getScanCount();
		List<Spectrum> tempDataset;
		int datumSize = 4;
		int dataSizeMiB = dataSource.estimateDataSourceSize() * datumSize / 1024/1024;
		
		
		//we'll use a temp list if the data set is more than 70% of our heap space, or if total
		//heap space is less than 250 MiB.
		//we do this so that we don't eat up valuable memory that we will need later to store things
		//like maps
		boolean useTempList = true; //dataSizeMiB > Env.heapSize() * 0.7 || Env.heapSize() < 250;
		try
		{
			if (useTempList)
			{
								
				tempDataset = new TempFileList<Spectrum>(fileCount, "Data Set", encode, decode);
				for (int i = 0; i < fileCount; i++)
				{
					tempDataset.add(i, null);
				}
			} else {
				tempDataset = DataTypeFactory.spectrumSetInit(fileCount);				
			}
		}
		catch (IOException e)
		{
			tempDataset = DataTypeFactory.spectrumSetInit(fileCount);
		}
		dataset = tempDataset;
		
		
		//get the reading task for this datasource, datasets
		reading = getReadingTaskForDataSource(dataSource, dataset, "Reading " + typeName + " File");
		

		//empty task for calculating values like the average scan...
		final EmptyTask applying = new EmptyTask("Calculating Values");
		
		
		//tasklist contains logic about how to execute the various tasks
		tasklist = new TaskList<Boolean>("Opening Data Set") {

			@Override
			public Boolean doTasks()
			{

				// XML parser doesn't play nice with multithreading?
				if (type != FileType.CDFML)
				{
					new TicketingUITaskExecutor(fileCount, reading, this).executeBlocking();
					hasDimensions = false;
				}
				else
				{
					new SimpleUITaskExecutor(fileCount, reading, this).executeBlocking();
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

				dataSourcePath = peakaboo.fileio.IOCommon.getFilePath(files.get(0).getFileName());

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

		
		//add the tasks to the task list for the sake of the gui components
		tasklist.addTask(reading);
		tasklist.addTask(applying);

		return tasklist;

	}


	private Task getReadingTaskForDataSource(final DataSource dataSource, final List<Spectrum> targetDataset,
			String title)
	{

		return new Task(title) {

			@Override
			public boolean work(int index)
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
				return true;

			}
			
		};

	}


	public void readFileListAsDataset(final List<AbstractFile> files)
	{
		TaskList<Boolean> tl = TASK_readFileListAsDataset(files);
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