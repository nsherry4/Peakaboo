package org.peakaboo.datasource.model.components.scandata.loaderqueue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.datasource.model.components.scandata.ScanData.ScanEntry;
import org.peakaboo.datasource.model.components.scandata.SimpleScanData;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;


public class SimpleLoaderQueue implements LoaderQueue {
	
	
	private LinkedBlockingQueue<ScanEntry> queue;
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
					ScanEntry struct = queue.take();
					Spectrum spectrum = struct.spectrum();
					int index = struct.index();
					
					if (spectrum != null) {
						if (preprocessor != null) {
							preprocessor.accept(spectrum);
						}
						if (index == -1) {
							data.add(spectrum);
						} else {
							data.set(index, spectrum);
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
		ScanEntry struct = new ScanEntry(index, s);
		queue.put(struct);
	}
	
	@Override
	public void submit(Spectrum s) throws InterruptedException {
		submit(-1, s);
	}
	
	
	@Override
	public void finish() throws InterruptedException {
		if (queue != null) {
			ScanEntry struct = new ScanEntry(-1, null);
			queue.put(struct);
			thread.join();
		}
	}
	
	@Override
	public void setPreprocessor(Consumer<Spectrum> preprocessor) {
		this.preprocessor = preprocessor;
	}

}



