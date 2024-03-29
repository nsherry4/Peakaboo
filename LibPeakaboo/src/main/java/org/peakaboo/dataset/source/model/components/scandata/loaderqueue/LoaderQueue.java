package org.peakaboo.dataset.source.model.components.scandata.loaderqueue;

import java.util.function.Consumer;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public interface LoaderQueue {

	void submit(Spectrum s) throws InterruptedException;
	void submit(int index, Spectrum s) throws InterruptedException;
	void setPreprocessor(Consumer<Spectrum> preprocessor);
	
	default void submit(float[] s) throws InterruptedException {
		submit(new ArraySpectrum(s));
	}

	default void submit(int index, float[] s) throws InterruptedException {
		submit(index, new ArraySpectrum(s));
	}
	
	/**
	 * Marks this queue as closed and blocks waiting for the processing thread to complete.
	 * @throws InterruptedException
	 */
	void finish() throws InterruptedException;

}

