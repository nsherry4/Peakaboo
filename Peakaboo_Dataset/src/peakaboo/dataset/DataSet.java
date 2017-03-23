package peakaboo.dataset;


import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import commonenvironment.AbstractFile;
import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.DataSourceLoader;
import peakaboo.datasource.components.DataSourceMetadata;
import peakaboo.datasource.internal.AbstractDataSource;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



/**
 * Given a DataSource, this class calculates the average and max spectra,
 * along with a few other values. This allows the data to be calculated 
 * once and accessed many times without adding cache logic elsewhere 
 * in the program.
 * 
 * @author Nathaniel Sherry, 2009,2012
 */

public class DataSet extends AbstractDataSet
{

	protected Spectrum				averagedSpectrum;
	protected Spectrum				maximumSpectrum;
	
	protected float					maxValue;
	protected int					spectrumLength;
	
	protected DataSource			dataSource;

	//list of real coordinates for each scan
	protected List<Coord<Number>>	realCoords;
	
	protected String				dataSourcePath;

	protected float					maxEnergy;

	

	

	public DataSet()
	{
		super();
	}
	
	
	public DataSet(DataSource ds)
	{
		super();
		
		readDataSource(ds, null, null, "");
		dataSource = ds;
		
	}


	@Override
	public Spectrum averagePlot()
	{
		return new Spectrum(averagedSpectrum);
	}


	@Override
	public Spectrum averagePlot(final List<Integer> excludedIndcies)
	{

		if (excludedIndcies.size() == 0) return averagePlot();

		
		//Filter for *JUST* the scans which have been marked as bad
		List<Spectrum> badScans = excludedIndcies.stream().map(index -> dataSource.get(index)).collect(toList());

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
		At = averagedSpectrum;
		Nt = dataSource.scanCount();
		Ne = badScans.size();

		// if all scans are marked as bad, lets just return a list of 0s of the same length as the average scan
		if (Nt == Ne)
		{
			return new Spectrum(new Spectrum(averagedSpectrum.size(), 0.0f));
		}

		float Net = (float) Ne / (float) Nt;
		float Ntte = Nt / ((float) Nt - (float) Ne);

		Spectrum goodAverage = new Spectrum(averagedSpectrum.size());
		for (int i = 0; i < averagedSpectrum.size(); i++)
		{
			goodAverage.set(i, (At.get(i) - Ae.get(i) * Net) * Ntte);
		}

		return new Spectrum(goodAverage);

	}


	@Override
	public Spectrum maximumPlot()
	{
		return new Spectrum(maximumSpectrum);
	}


	@Override
	public float maximumIntensity()
	{
		if (dataSource.scanCount() == 0) return 0;
				
		return maxValue;
	}


	@Override
	public Spectrum getScan(int index)
	{
		Spectrum original = dataSource.get(index);
		if (original == null) return null;
		return new Spectrum(original);

	}


	@Override
	public String getScanName(int index)
	{
		if (dataSource == null || index >= dataSource.scanCount()) return "";
		return dataSource.scanNames().get(index);
	}


	@Override
	public int size()
	{
		return dataSource.scanCount();
	}

	@Override
	public int expectedScanCount()
	{
		
		if (dataSource.hasScanDimensions()) 
		{
			Coord<Integer> dataDimension = dataSource.getDataDimensions();
			return dataDimension.x * dataDimension.y;
		}
		return size();
	}

	@Override
	public void invalidateFilteredData()
	{
		
	}




	/**
	 * Reads the list of {@link AbstractFile}s as a {@link DataSource}
	 * @param files the files to read as a {@link DataSource}
	 * @return {@link ExecutorSet} which, when completed, returns a Boolean indicating success
	 */
	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<String> filenames, final AbstractDataSource dataSource)
	{

		
		// sort the filenames property
		Collections.sort(filenames);
		
		// Create the tasklist for reading the files
		final ExecutorSet<DatasetReadResult> tasklist;
		
		/*
		final EmptyMap opening = new EmptyMap("Opening Data Set");
		final EmptyProgressingMap reading = new EmptyProgressingMap("Reading Scans");
		final EmptyProgressingMap applying = new EmptyProgressingMap("Calculating Values");
		*/
		
		final DummyExecutor opening = new DummyExecutor(true);//"Opening Data Set");
		final DummyExecutor reading = new DummyExecutor();//"Reading Scans");
		final DummyExecutor applying = new DummyExecutor();//"Calculating Values");
		
		
		tasklist = new ExecutorSet<DatasetReadResult>("Opening Data Set") {

			@Override
			protected DatasetReadResult execute()
			{
				
				final int scanCount;
				
				opening.advanceState();
				
				//anon function to call when we get the number of scans
				Consumer<Integer> gotScanCount = (Integer count) ->	{
					reading.setWorkUnits(count);
					opening.advanceState();
					reading.advanceState();
				};
				
				//anon function to call to check if the user has requested the operation be aborted
				Supplier<Boolean> isAborted = () -> isAborted() || isAbortRequested();
				
				//anon function to call when the loader reads a scan from the input data
				Consumer<Integer> readScans = (Integer count) -> reading.workUnitCompleted(count);
				

				try
				{
					dataSource.setCallbacks(gotScanCount, readScans, isAborted);
					if (filenames.size() == 1)
					{
						dataSource.read(filenames.get(0));
					}
					else
					{
						dataSource.read(new ArrayList<String>(filenames));
					}
				
				}
				catch (Exception e)
				{
					e.printStackTrace();
					aborted();
					return new DatasetReadResult(ReadStatus.FAILED, "Failed to read from the selected file(s)");
				}
				

				
				if (isAborted.get())
				{
					aborted();
					return new DatasetReadResult(ReadStatus.CANCELLED);
				}
				
				
				scanCount = dataSource.scanCount();
				if (scanCount == 0) return new DatasetReadResult(ReadStatus.FAILED, "Did not find any data in file(s)");
				gotScanCount.accept(scanCount);
				reading.advanceState();
				
				applying.setWorkUnits(dataSource.scanCount());
				applying.advanceState();
				
				
				//now that we have the datasource, read it
				readDataSource(  dataSource, applying, isAborted, new File(filenames.get(0)).getParent()  );
				
				
				if (isAborted.get())
				{
					aborted();
					return new DatasetReadResult(ReadStatus.CANCELLED);
				}
				
				
				//we're done
				applying.workUnitCompleted();
				applying.advanceState();
				
				return new DatasetReadResult(ReadStatus.SUCCESS);
				
			}
			
		};
		
		tasklist.addExecutor(opening, "Opening Data Set");
		tasklist.addExecutor(reading, "Reading Scans");
		tasklist.addExecutor(applying, "Calculating Values");
				
		return tasklist;

	}


	
	
	@Override
	public int firstNonNullScanIndex()
	{
		return AbstractDataSet.firstNonNullScanIndex(dataSource, 0);
	}
	
	@Override
	public int firstNonNullScanIndex(int start)
	{
		return AbstractDataSet.firstNonNullScanIndex(dataSource, start);
	}
	
	@Override
	public int lastNonNullScanIndex()
	{
		return AbstractDataSet.lastNonNullScanIndex(dataSource, dataSource.scanCount()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		return AbstractDataSet.lastNonNullScanIndex(dataSource, upto);
	}
	
	
	private void readDataSource(DataSource ds, DummyExecutor applying, Supplier<Boolean> isAborted, String path)
	{
		
		if (ds == null || ds.scanCount() == 0) return;
				

		
		int nonNullScanIndex = AbstractDataSet.firstNonNullScanIndex(ds, 0);
		if (nonNullScanIndex == -1) return;
		Spectrum nonNullScan = ds.get(nonNullScanIndex);
		if (nonNullScan == null) return;
		
		spectrumLength = nonNullScan.size();
		
		
		
		//if this data source has dimensions, make space to store them all in a list
		if (ds.hasScanDimensions())
		{
			realCoords = new ArrayList<Coord<Number>>();
		}

		
		//go over each scan, calculating the average, max10th and max value
		float max = Float.MIN_VALUE;
		Spectrum avg, max10, current;
		
		avg = new Spectrum(spectrumLength);
		max10 = new Spectrum(spectrumLength);
		
		
		for (int i = 0; i < ds.scanCount(); i++)
		{
			current = ds.get(i);
			
			if (current == null) continue;
			
			SpectrumCalculations.addLists_inplace(avg, current);
			SpectrumCalculations.maxlist_inplace(max10, current);
			
			max = Math.max(max, SpectrumCalculations.max(current));
			
			//read the real coordinates for this scan
			if (ds.hasScanDimensions()) realCoords.add(ds.getRealCoordinatesAtIndex(i));
			
			
			if (applying != null) applying.workUnitCompleted();
			if (isAborted != null && isAborted.get()) return;
			
		}
		
		SpectrumCalculations.divideBy_inplace(avg, ds.scanCount());
		
		averagedSpectrum = avg;
		maximumSpectrum = max10;
		
		maxValue = max;

		dataSourcePath = path;
		this.dataSource = ds;
		

	}
	
	public String getDataSourcePath()
	{
		return dataSourcePath;
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
	public DataSourceMetadata getMetadata() {
		return dataSource.getMetadata();
	}
	
	@Override
	public boolean hasMetadata() {
		return dataSource.hasMetadata();
	}
	
	
	@Override
	public String getDatasetName()
	{
		return dataSource.datasetName();
	}


	@Override
	public Coord<Integer> getDataDimensions()
	{
		if (dataSource.hasScanDimensions())
		{
			return dataSource.getDataDimensions();
		}
		return new Coord<Integer>(dataSource.scanCount(), 1);
	}


	@Override
	public List<Coord<Number>> getCoordinateList()
	{
		if (dataSource.hasScanDimensions())
		{
			return realCoords;
		}
		return null;
	}


	@Override
	public Coord<Bounds<Number>> getRealDimensions()
	{
		if (dataSource.hasScanDimensions())
		{
			return dataSource.getRealDimensions();
		}
		return null;
	}


	@Override
	public SISize getRealDimensionsUnits()
	{
		if (dataSource.hasScanDimensions())
		{
			return getSISizeFromUnitName(dataSource.getRealDimensionsUnit());
		}
		return null;
	}


	@Override
	public boolean hasData()
	{
		return dataSource.scanCount() > 0;
	}


	@Override
	public boolean hasDimensions()
	{
		return dataSource.hasScanDimensions();
	}


	@Override
	public boolean hasExtendedInformation()
	{
		return dataSource.hasMetadata();
	}



	@Override
	public void discard()
	{
		//discard our reference to the datasource
		dataSource = null;
	}


	@Override
	public int channelsPerScan()
	{
		// TODO Auto-generated method stub
		return spectrumLength;
	}


	public float energyPerChannel()
	{
		return maxEnergy / channelsPerScan();
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
	

	public static List<AbstractDataSource> getDataSourcePlugins()
	{
		return DataSourceLoader.getDSPs();
	}






	
}

