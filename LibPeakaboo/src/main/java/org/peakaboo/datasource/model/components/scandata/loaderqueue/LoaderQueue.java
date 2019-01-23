package peakaboo.datasource.model.components.scandata.loaderqueue;

import cyclops.ISpectrum;
import cyclops.Spectrum;

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

