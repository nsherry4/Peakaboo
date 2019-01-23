package org.peakaboo.datasource.model.components.scandata;


import java.util.Iterator;

import cyclops.ReadOnlySpectrum;



public interface ScanData extends Iterable<ReadOnlySpectrum>
{

	/**
	 * Retrieves the values from the scan at the given index
	 * 
	 * @param index
	 *            the scan number to retrieve
	 */
	ReadOnlySpectrum get(int index) throws IndexOutOfBoundsException;

	
	default Iterator<ReadOnlySpectrum> iterator() {
		return new Iterator<ReadOnlySpectrum>() {

			int next = 0;
			
			@Override
			public boolean hasNext() {
				return scanCount() > next;
			}

			@Override
			public ReadOnlySpectrum next() {
				return get(next++);
			}
		};
	}
	
	
	/**
	 * Returns the number of scans in this data set.
	 */
	int scanCount();

	

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
	
	

	

}
