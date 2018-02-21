package peakaboo.datasource.model.components.scandata.loaderqueue;

import scitypes.ISpectrum;
import scitypes.Spectrum;

public interface LoaderQueue {

	void submit(Spectrum s) throws InterruptedException;

	default void submit(float[] s) throws InterruptedException {
		submit(new ISpectrum(s));
	}

	void finish() throws InterruptedException;

}