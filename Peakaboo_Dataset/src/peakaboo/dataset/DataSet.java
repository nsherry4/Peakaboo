package peakaboo.dataset;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import commonenvironment.AbstractFile;

import fava.datatypes.Maybe;
import fava.functionable.FList;
import fava.signatures.FnEach;
import fava.signatures.FnGet;
import fava.signatures.FnMap;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.plugin.AbstractDSP;
import peakaboo.datasource.plugin.DSPLoader;
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
		At = averagedSpectrum;
		Nt = dataSource.getScanCount();
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
		
		if (dataSource.hasRealDimensions()) 
		{
			Coord<Integer> dataDimension = dataSource.getDataDimensions();
			return dataDimension.x * dataDimension.y;
		}
		return scanCount();
	}

	@Override
	public void invalidateFilteredData()
	{
		
	}




	/**
	 * Reads the list of {@link AbstractFile}s as a {@link DataSource}
	 * @param files the files to read as a {@link DataSource}
	 * @return {@link ExecutorSet} which, when complated, returns a Boolean indicating success
	 */
	public ExecutorSet<Maybe<Boolean>> TASK_readFileListAsDataset(final List<String> filenames, final AbstractDSP dataSource)
	{

		
		// sort the filenames property
		Collections.sort(filenames);
		
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
				
				
				if (dataSource != null)
				{
					try
					{
						dataSource.setCallbacks(gotScanCount, readScans, isAborted);
						if (filenames.size() == 1)
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
				readDataSource(  dataSource, applying, isAborted, new File(filenames.get(0)).getParent()  );
				
				
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
		return AbstractDataSet.lastNonNullScanIndex(dataSource, dataSource.getScanCount()-1);
	}
	
	@Override
	public int lastNonNullScanIndex(int upto)
	{
		return AbstractDataSet.lastNonNullScanIndex(dataSource, upto);
	}
	
	
	private void readDataSource(DataSource ds, DummyExecutor applying, FnGet<Boolean> isAborted, String path)
	{
		
		if (ds == null || ds.getScanCount() == 0) return;
				

		
		int nonNullScanIndex = AbstractDataSet.firstNonNullScanIndex(ds, 0);
		if (nonNullScanIndex == -1) return;
		Spectrum nonNullScan = ds.getScanAtIndex(nonNullScanIndex);
		if (nonNullScan == null) return;
		
		spectrumLength = nonNullScan.size();
		
		
		
		//if this data source has dimensions, make space to store them all in a list
		if (ds.hasRealDimensions())
		{
			realCoords = new ArrayList<Coord<Number>>();
		}

		
		//go over each scan, calculating the average, max10th and max value
		float max = Float.MIN_VALUE;
		Spectrum avg, max10, current;
		
		avg = new Spectrum(spectrumLength);
		max10 = new Spectrum(spectrumLength);
		
		
		for (int i = 0; i < ds.getScanCount(); i++)
		{
			current = ds.getScanAtIndex(i);
			
			if (current == null) continue;
			
			SpectrumCalculations.addLists_inplace(avg, current);
			SpectrumCalculations.maxlist_inplace(max10, current);
			
			max = Math.max(max, SpectrumCalculations.max(current));
			
			//read the real coordinates for this scan
			if (ds.hasRealDimensions()) realCoords.add(ds.getRealCoordinatesAtIndex(i));
			
			
			if (applying != null) applying.workUnitCompleted();
			if (isAborted != null && isAborted.f()) return;
			
		}
		
		SpectrumCalculations.divideBy_inplace(avg, ds.getScanCount());
		
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
	public String getDatasetName()
	{
		return dataSource.getDatasetName();
	}


	@Override
	public Coord<Integer> getDataDimensions()
	{
		if (dataSource.hasRealDimensions())
		{
			return dataSource.getDataDimensions();
		}
		return new Coord<Integer>(dataSource.getScanCount(), 1);
	}


	@Override
	public List<Coord<Number>> getCoordinateList()
	{
		if (dataSource.hasRealDimensions())
		{
			return realCoords;
		}
		return null;
	}


	@Override
	public Coord<Bounds<Number>> getRealDimensions()
	{
		if (dataSource.hasRealDimensions())
		{
			return dataSource.getRealDimensions();
		}
		return null;
	}


	@Override
	public SISize getRealDimensionsUnits()
	{
		if (dataSource.hasRealDimensions())
		{
			return getSISizeFromUnitName(dataSource.getRealDimensionsUnit());
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
		return dataSource.hasRealDimensions();
	}


	@Override
	public boolean hasExtendedInformation()
	{
		return dataSource.hasMetadata();
	}


	@Override
	public String getCreationTime()
	{
		if (dataSource.hasMetadata()) return dataSource.getCreationTime();
		return "";
	}


	@Override
	public String getCreator()
	{
		if (dataSource.hasMetadata()) return dataSource.getCreator();
		return "";
	}


	@Override
	public String getEndTime()
	{
		if (dataSource.hasMetadata()) return dataSource.getEndTime();
		return "";
	}


	@Override
	public String getExperimentName()
	{
		if (dataSource.hasMetadata()) return dataSource.getExperimentName();
		return "";
	}


	@Override
	public String getFacilityName()
	{
		if (dataSource.hasMetadata()) return dataSource.getFacilityName();
		return "";
	}


	@Override
	public String getInstrumentName()
	{
		if (dataSource.hasMetadata()) return dataSource.getInstrumentName();
		return "";
	}


	@Override
	public String getLaboratoryName()
	{
		if (dataSource.hasMetadata()) return dataSource.getLaboratoryName();
		return "";
	}


	@Override
	public String getProjectName()
	{
		if (dataSource.hasMetadata()) return dataSource.getProjectName();
		return "";
	}


	@Override
	public String getSampleName()
	{
		if (dataSource.hasMetadata()) return dataSource.getSampleName();
		return "";
	}


	@Override
	public String getScanName()
	{
		if (dataSource.hasMetadata()) return dataSource.getScanName();
		return "";
	}


	@Override
	public String getSessionName()
	{
		if (dataSource.hasMetadata()) return dataSource.getSessionName();
		return "";
	}


	@Override
	public String getStartTime()
	{
		if (dataSource.hasMetadata()) return dataSource.getStartTime();
		return "";
	}


	@Override
	public String getTechniqueName()
	{
		if (dataSource.hasMetadata()) return dataSource.getTechniqueName();
		return "";
	}


	@Override
	public void discard()
	{
		//discard our reference to the datasource
		dataSource = null;
	}


	@Override
	public int scanSize()
	{
		// TODO Auto-generated method stub
		return spectrumLength;
	}


	public float energyPerChannel()
	{
		return maxEnergy / scanSize();
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
	

	public static List<AbstractDSP> getDataSourcePlugins()
	{
		return DSPLoader.getDSPs();
	}
	
}
