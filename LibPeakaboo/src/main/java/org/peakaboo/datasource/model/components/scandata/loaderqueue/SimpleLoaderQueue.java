package org.peakaboo.datasource.model.components.scandata.loaderqueue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.datasource.model.components.scandata.SimpleScanData;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;


public class SimpleLoaderQueue implements LoaderQueue {
	
	class SpectrumIndex {
		public Spectrum spectrum;
		public int index;
	}
	
	private LinkedBlockingQueue<SpectrumIndex> queue;
	private Thread thread;
	private Consumer<Spectrum> preprocessor = null;
	
	public SimpleLoaderQueue(SimpleScanData data) {
		this(data, 1000);
	}
	public SimpleLoaderQueue(SimpleScanData data, int depth) {
		
		queue = new LinkedBlockingQueue<>(depth);
		thread = new Thread(() -> {
			while(true) {
				try {
					SpectrumIndex struct = queue.take();
					if (struct.spectrum != null) {
						if (preprocessor != null) {
							preprocessor.accept(struct.spectrum);
						}
						if (struct.index == -1) {
							data.add(struct.spectrum);
						} else {
							data.set(struct.index, struct.spectrum);
						}
					} else {
						return;
					}
				} catch (InterruptedException e) {
					PeakabooLog.get().log(Level.SEVERE, "Exception while processing LoaderQueue Spectrum entries", e);
					Thread.currentThread().interrupt();
					return;
				}
			}
		});
		thread.setName("SimpleLoaderQueue");
		thread.start();

	}
	
	@Override
	public void submit(int index, Spectrum s) throws InterruptedException {
		SpectrumIndex struct = new SpectrumIndex();
		struct.index = index;
		struct.spectrum = s;
		queue.put(struct);
	}
	
	@Override
	public void submit(Spectrum s) throws InterruptedException {
		submit(-1, s);
	}
	
	
	@Override
	public void finish() throws InterruptedException {
		if (queue != null) {
			SpectrumIndex struct = new SpectrumIndex();
			queue.put(struct);
			thread.join();
		}
	}
	
	@Override
	public void setPreprocessor(Consumer<Spectrum> preprocessor) {
		this.preprocessor = preprocessor;
	}

}



