package org.peakaboo.datasource.model.components.scandata.loaderqueue;

import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Spectrum;

public interface LoaderQueue {

	void submit(Spectrum s) throws InterruptedException;
	void submit(int index, Spectrum s) throws InterruptedException;

	default void submit(float[] s) throws InterruptedException {
		submit(new ISpectrum(s));
	}

	default void submit(int index, float[] s) throws InterruptedException {
		submit(index, new ISpectrum(s));
	}
	
	/**
	 * Marks this queue as closed and blocks waiting for the processing thread to complete.
	 * @throws InterruptedException
	 */
	void finish() throws InterruptedException;

}

