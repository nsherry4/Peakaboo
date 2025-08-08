package org.peakaboo.dataset.source.model.components.scandata;


import java.util.Iterator;
import java.util.NoSuchElementException;

import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;



public interface ScanData extends Iterable<SpectrumView>, AutoCloseable {
	
	/**
	 * Retrieves the values from the scan at the given index
	 * 
	 * @param index
	 *            the scan number to retrieve
	 */
	SpectrumView get(int index) throws IndexOutOfBoundsException;

	
	default Iterator<SpectrumView> iterator() {
		return new Iterator<SpectrumView>() {

			int next = 0;
			
			@Override
			public boolean hasNext() {
				return scanCount() > next;
			}

			@Override
			public SpectrumView next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return get(next++);
			}
		};
	}
	
	
	/**
	 * Returns the number of scans in this data set.
	 */
	int scanCount();
	
	
	default boolean isEmpty() {
		return scanCount() == 0;
	}

	

	/**
	 * Returns the names of all scans, eg ["Scan 1", "Scan 2", ...]
	 */
	String scanName(int index);


	/**
	 * Returns the energy value for the last channel in this scan.
	 */
	float maxEnergy();

	/**
	 * Returns the energy value for the first channel
	 */
	float minEnergy();


	/**
	 * Returns a nice, human readable name for this data set. Depending 
	 * on the data stored in the file, it could be the name of the data
	 * file, the folder the set of files were found it, a name specified 
	 * within the file itself, etc...
	 */
	String datasetName();
	
	
	Analysis getAnalysis();
	

	/**
	 * Finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @param start the index from which to start searching
	 * @return the index of the first non-null scan
	 */
	default int firstNonNullScanIndex(int start) {
		for (int i = start; i < scanCount(); i++) {
			if (get(i) != null) {
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * Finds the first non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 10-50
	 * @return the index of the first non-null scan, or -1 if no such scans exist
	 */
	default int firstNonNullScanIndex() {
		return firstNonNullScanIndex(0);
	}
	

	/**
	 * Finds the last non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 1-45 where 50 scans are expected
	 * @param upto the maximum index to consider
	 * @return the index of the last non-null scan
	 */
	default int lastNonNullScanIndex(int upto) {
		upto = Math.min(upto, scanCount()-1);
		
		for (int i = upto; i >= 0; i--) {
			if (get(i) != null) {
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * Finds the last non-null scan. This is useful in situations where a partial data set is read, containing, for example, scans 1-45 where 50 scans are expected
	 * @return the index of the last non-null scan, or -1 if no such scans exist
	 */
	default int lastNonNullScanIndex() {
		return lastNonNullScanIndex(scanCount()-1);
	}

	

	
	
	
	

}
