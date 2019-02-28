package org.peakaboo.dataset;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.dataset.DatasetReadResult.ReadStatus;
import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.datasize.DummyDataSize;
import org.peakaboo.datasource.model.components.interaction.CallbackInteraction;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.DummyScanData;
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.datasource.model.internal.SubsetDataSource;

import cyclops.Coord;
import cyclops.ReadOnlySpectrum;
import net.sciencestudio.bolt.plugin.core.AlphaNumericComparitor;
import plural.executor.AbstractExecutor;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;



/**
 * Given a DataSource, a DataSet  is intended to layer features on top of a 
 * DataSource in order to keep the DataSource implementation simple. It is 
 * also intended to present a more programmer-friendly interface to the rest 
 * of Peakaboo, whereas a DataSource is intended to be more user-friendly to 
 * the DataSource implementer. It is not intended to store user settings or 
 * overrides.
 * 
 * @author Nathaniel Sherry, 2009-2018
 */
public class StandardDataSet implements DataSet
{


	protected DataSource			dataSource;

	//Data sources are not guaranteed to be fast at retrieving 
	//information about scans, so we store the physical coordinates
	//here
	protected List<Coord<Number>>	realCoords;
	

	public StandardDataSet()
	{
		super();
	}
	
	
	public StandardDataSet(DataSource ds) {
		this(ds, null, null);
	}
	
	public StandardDataSet(DataSource ds, AbstractExecutor progress, Supplier<Boolean> isAborted)
	{
		super();
		
		readDataSource(ds, progress, isAborted);
		dataSource = ds;
		
	}



	/**
	 * Reads the list of filenames as a {@link DataSource}
	 * @param paths the files to read as a {@link DataSource}
	 * @return {@link ExecutorSet} which, when completed, returns a Boolean indicating success
	 */
	public ExecutorSet<DatasetReadResult> TASK_readFileListAsDataset(final List<Path> paths, final DataSource dataSource)
	{

		// sort the filenames alphanumerically. Files like "point2" should appear before "point10"
		Comparator<String> comparitor = new AlphaNumericComparitor(); 
		paths.sort((a, b) -> comparitor.compare(a.toString(), b.toString()));
		
		
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
					
					long t1 = System.currentTimeMillis();
					PeakabooLog.get().log(Level.INFO, "Starting Data Set Open");
					
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
					Consumer<Integer> readScans = (Integer count) -> {
						reading.workUnitCompleted(count);
					};
					
	

					dataSource.setInteraction(new CallbackInteraction(gotScanCount, readScans, isAborted));
					dataSource.read(paths);	
	
					
					
	
					
					if (isAborted.get())
					{
						aborted();
						return new DatasetReadResult(ReadStatus.CANCELLED);
					}
					
					
					scanCount = dataSource.getScanData().scanCount();
					if (scanCount == 0) return new DatasetReadResult(ReadStatus.FAILED, "Did not find any data in file(s)");
					gotScanCount.accept(scanCount);
					reading.advanceState();
					
					applying.advanceState();
					//now that we have the datasource, read it
					readDataSource(dataSource, applying, isAborted);
					
					
					if (isAborted.get())
					{
						aborted();
						return new DatasetReadResult(ReadStatus.CANCELLED);
					}
					
					
					//we're done
					applying.workUnitCompleted();
					applying.advanceState();
					
					long t2 = System.currentTimeMillis();
					PeakabooLog.get().log(Level.INFO, "Opened a " + dataSource.getFileFormat().getFormatName() + " Data Set in " + ((t2-t1)/1000) + " Seconds");
					
					return new DatasetReadResult(ReadStatus.SUCCESS);
					
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return new DatasetReadResult(e);
				} catch (Throwable e) {
					return new DatasetReadResult(e);
				}
				
			}
			
		};
		
		tasklist.addExecutor(opening, "Opening Data Set");
		tasklist.addExecutor(reading, "Reading Scans");
		tasklist.addExecutor(applying, "Calculating Values");
				
		return tasklist;

	}


	

	
	
	private void readDataSource(DataSource ds, AbstractExecutor applying, Supplier<Boolean> isAborted)
	{
		
		if (ds == null || ds.getScanData().scanCount() == 0) return;

		boolean hasRealSize = ds.getPhysicalSize().isPresent();
		boolean isSubset = ds instanceof SubsetDataSource;
			
		//go over each scan, calculating the average, max10th and max value
		int updateInterval = Math.min(Math.max(ds.getScanData().scanCount()/100, 20), 1000);
		int gcInterval = 5000;
		applying.setWorkUnits(ds.getScanData().scanCount());
		
		if (hasRealSize) {
			realCoords = new ArrayList<Coord<Number>>();
		}
		
		if (hasRealSize || isSubset) {
			for (int i = 0; i < ds.getScanData().scanCount(); i++) {
				
				if (isSubset) {
					//subset data sources need to re-perform their analysis on the subset
					if (ds instanceof SubsetDataSource) {
						SubsetDataSource sds = (SubsetDataSource) ds;
						sds.reanalyze(i);
					}
				}
				
				if (hasRealSize) {
					//read the real coordinates for this scan
					realCoords.add(ds.getPhysicalSize().get().getPhysicalCoordinatesAtIndex(i));
				}
	
				if (i % updateInterval == 0) {
					if (applying != null) applying.workUnitCompleted(updateInterval);
					if (isAborted != null && isAborted.get()) return;
				}
				if (i % gcInterval == 0) {
					System.gc();
				}
				
			}
			System.gc();
		}
			
		
		this.dataSource = ds;
		

	}
	

	@Override
	public Optional<Metadata> getMetadata() {
		return dataSource.getMetadata();
	}


	@Override
	public boolean hasGenuineScanData()
	{
		return dataSource.getScanData().scanCount() > 0;
	}


	
	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return dataSource.getPhysicalSize();
	}

	
	@Override
	public DataSize getDataSize() {
		if (dataSource.getDataSize().isPresent()) {
			return dataSource.getDataSize().get();
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
	public DataSource getDataSource()
	{
		return dataSource;
	}
	


	@Override
	public ScanData getScanData() {
		if (dataSource.getScanData() != null) {
			return dataSource.getScanData();
		} else {
			return new DummyScanData();
		}
	}


	@Override
	public boolean hasGenuineDataSize() {
		return getDataSource().getDataSize().isPresent();
	}


	@Override
	public Analysis getAnalysis() {
		return dataSource.getScanData().getAnalysis();
	}


	
}

