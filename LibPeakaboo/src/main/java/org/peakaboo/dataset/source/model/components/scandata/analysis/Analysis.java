package org.peakaboo.dataset.source.model.components.scandata.analysis;

import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

/**
 * Peakaboo derives a number of measurements from a Data Set which are not
 * directly provided by the DataSource. This includes things like the average
 * scan. This is separated from the DataSet for composability and simplicity 
 * @author NAS
 *
 */
public interface Analysis {

	
	/**
	 * Accepts a new scan and it's index (position in the data). Calculates any 
	 * incremental metrics it may wish to do. 
	 * @param spectrum
	 */
	void process(ReadOnlySpectrum spectrum);
	
	
	

	
	/**
	 * Produces a single scan/list containing the average value for each channel
	 * 
	 * @return average scan
	 */
	ReadOnlySpectrum averagePlot();


	/**
	 * Produces a single scan/list containing the most intense values for each channel
	 * 
	 * @return the top signal-per-channel scan
	 */
	ReadOnlySpectrum maximumPlot();

	/**
	 * Produces a single scan/list containing the sum value for each channel
	 * 
	 * @return summed scan
	 */
	ReadOnlySpectrum summedPlot();
	
	/**
	 * Reports the number of scans seen by this Analysis component
	 */
	int scanCount();
	
	/**
	 * Returns the size of a single scan
	 * 
	 * @return size of a scan
	 */
	int channelsPerScan();
	

	/**
	 * Calculates the maximum single-channel intensity across all scans
	 * 
	 * @return the maximum intensity
	 */
	float maximumIntensity();
	
}
