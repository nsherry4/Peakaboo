package peakaboo.dataset;


import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.datasize.DummyDataSize;
import peakaboo.datasource.model.components.interaction.CallbackInteraction;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.DummyScanData;
import peakaboo.datasource.model.components.scandata.ScanData;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import scitypes.Coord;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



/**
 * Given a DataSource, this class calculates the average and max spectra,
 * along with a few other values. This allows the data to be calculated 
 * once and accessed many times without adding cache logic elsewhere 
 * in the program. This is intended to layer features on top of a 
 * DataSource in order to keep the DataSource implementation simple. It
 * is not intended to store user settings/overrides.
 * 
 * @author Nathaniel Sherry, 2009-2017
 */

public class StandardDataSet implements DataSet
{

	protected Spectrum				averagedSpectrum;
	protected Spectrum				maximumSpectrum;
	
	protected float					maxValue;
	protected int					spectrumLength;
	
	protected DataSource			dataSource;

	//list of real coordinates for each scan
	protected List<Coord<Number>>	realCoords;
	
	protected File					dataSourcePath;

	

	public StandardDataSet()
	{
		super();
	}
	
	
	public StandardDataSet(DataSource ds, DummyExecutor progress, Supplier<Boolean> isAborted)
	{
		super();
		
		readDataSource(ds, progress, isAborted, new File(""));
		dataSource = ds;
		
	}


	@Override
	public Spectrum averagePlot()
	{
		return new ISpectrum(averagedSpectrum);
	}


	@Override
	public Spectrum averagePlot(final List<Integer> excludedIndcies)
	{

		if (excludedIndcies.size() == 0) return averagePlot();

		
		//Filter for *JUST* the scans which have been marked as bad
		List<ReadOnlySpectrum> badScans = excludedIndcies.stream().map(index -> dataSource.getScanData().get(index)).collect(toList());

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
		Nt = dataSource.getScanData().scanCount();
		Ne = badScans.size();

		// if all scans are marked as bad, lets just return a list of 0s of the same length as the average scan
		if (Nt == Ne)
		{
			return new ISpectrum(new ISpectrum(averagedSpectrum.size(), 0.0f));
		}

		float Net = (float) Ne / (float) Nt;
		float Ntte = Nt / ((float) Nt - (float) Ne);

		Spectrum goodAverage = new ISpectrum(averagedSpectrum.size());
		for (int i = 0; i < averagedSpectrum.size(); i++)
		{
			goodAverage.set(i, (At.get(i) - Ae.get(i) * Net) * Ntte);
		}

		return new ISpectrum(goodAverage);

	}


	@Override
	public Spectrum maximumPlot()
	{
		return new ISpectrum(maximumSpectrum);
	}


	@Override
	public float maximumIntensity()
	{
		if (dataSource.getScanData().scanCount() == 0) return 0;
				
		return maxValue;
	}




	/**
	 * Reads the list of filenames as a {@link DataSource}
	 * @param files the files to read as a {@link DataSource}
	 * @return {@link ExecutorSet} which, when completed, returns a Boolean indicating success
	 */
	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<File> files, final DataSource dataSource)
	{

		
		// sort the filenames property
		Collections.sort(files);
		
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
				
				try {
						
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
					
	

					dataSource.setInteraction(new CallbackInteraction(gotScanCount, readScans, isAborted));
					if (files.size() == 1)
					{
						dataSource.read(files.get(0).toPath());
					}
					else
					{
						dataSource.read(files.stream().map(File::toPath).collect(Collectors.toList()));
					}

	
					
					if (isAborted.get())
					{
						aborted();
						return new DatasetReadResult(ReadStatus.CANCELLED);
					}
					
					
					scanCount = dataSource.getScanData().scanCount();
					if (scanCount == 0) return new DatasetReadResult(ReadStatus.FAILED, "Did not find any data in file(s)");
					gotScanCount.accept(scanCount);
					reading.advanceState();
					
					applying.setWorkUnits(dataSource.getScanData().scanCount());
					applying.advanceState();
					
					
					//now that we have the datasource, read it
					readDataSource(  dataSource, applying, isAborted, files.get(0).getParentFile()  );
					
					
					if (isAborted.get())
					{
						aborted();
						return new DatasetReadResult(ReadStatus.CANCELLED);
					}
					
					
					//we're done
					applying.workUnitCompleted();
					applying.advanceState();
					
					return new DatasetReadResult(ReadStatus.SUCCESS);
					
					
					
				} catch (Exception e) {
					return new DatasetReadResult(e);
				}
				
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
		return DataSet.firstNonNullScanIndex(dataSource, 0);
	}
	
	@Override
	public int firstNonNullScanIndex(int start)
	{
		return DataSet.firstNonNullScanIndex(dataSource, start);
	}
	
	@Override
	public int lastNonNullScanIndex()
	{
		return DataSet.lastNonNullScanIndex(dataSource, dataSource.getScanData().scanCount()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		return DataSet.lastNonNullScanIndex(dataSource, upto);
	}
	
	
	private void readDataSource(DataSource ds, DummyExecutor applying, Supplier<Boolean> isAborted, File path)
	{
		
		if (ds == null || ds.getScanData().scanCount() == 0) return;
				

		
		int nonNullScanIndex = DataSet.firstNonNullScanIndex(ds, 0);
		if (nonNullScanIndex == -1) return;
		ReadOnlySpectrum nonNullScan = ds.getScanData().get(nonNullScanIndex);
		if (nonNullScan == null) return;
		
		spectrumLength = nonNullScan.size();
		
		
		
		//if this data source has dimensions, make space to store them all in a list
		if (ds.hasPhysicalSize())
		{
			realCoords = new ArrayList<Coord<Number>>();
		}

		
		//go over each scan, calculating the average, max10th and max value
		float max = Float.MIN_VALUE;
		ReadOnlySpectrum current;
		Spectrum avg, max10;
		
		avg = new ISpectrum(spectrumLength);
		max10 = new ISpectrum(spectrumLength);
		int updateInterval = Math.max(ds.getScanData().scanCount()/100, 20);
		
		for (int i = 0; i < ds.getScanData().scanCount(); i++)
		{
			current = ds.getScanData().get(i);
			
			if (current == null) continue;
			
			SpectrumCalculations.addLists_inplace(avg, current);
			SpectrumCalculations.maxLists_inplace(max10, current);
			
			max = Math.max(max, current.max());
			
			//read the real coordinates for this scan
			if (ds.hasPhysicalSize()) realCoords.add(ds.getPhysicalSize().getPhysicalCoordinatesAtIndex(i));
			
			if (i % updateInterval == 0) {
				if (applying != null) applying.workUnitCompleted(updateInterval);
				if (isAborted != null && isAborted.get()) return;
			}
			
		}
		
		SpectrumCalculations.divideBy_inplace(avg, ds.getScanData().scanCount());
		
		averagedSpectrum = avg;
		maximumSpectrum = max10;
		
		maxValue = max;

		dataSourcePath = path;
		this.dataSource = ds;
		

	}
	
	public File getDataSourcePath()
	{
		return dataSourcePath;
	}

	

	@Override
	public Metadata getMetadata() {
		return dataSource.getMetadata();
	}
	
	@Override
	public boolean hasMetadata() {
		return dataSource.hasMetadata();
	}
	


	@Override
	public boolean hasData()
	{
		return dataSource.getScanData().scanCount() > 0;
	}


	@Override
	public boolean hasPhysicalSize()
	{
		return dataSource.hasPhysicalSize();
	}
	
	
	@Override
	public PhysicalSize getPhysicalSize() {
		return dataSource.getPhysicalSize();
	}

	
	@Override
	public DataSize getDataSize() {
		if (dataSource.hasDataSize()) {
			return dataSource.getDataSize();
		} else {
			return new DummyDataSize(dataSource);
		}
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


	@Override
	public DataSource getDataSource()
	{
		return dataSource;
	}
	


	@Override
	public ScanData getScanData() {
		if (dataSource.hasScanData()) {
			return dataSource.getScanData();
		} else {
			return new DummyScanData();
		}
	}


	@Override
	public boolean hasGenuineDataSize() {
		return getDataSource().hasDataSize();
	}


	
}

