package org.peakaboo.datasource.model.components.scandata.loaderqueue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasource.model.components.scandata.SimpleScanData;
import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;

import cyclops.Spectrum;


public class SimpleLoaderQueue implements LoaderQueue {
	
	class SpectrumIndex {
		public Spectrum spectrum;
		public int index;
	}
	
	private LinkedBlockingQueue<SpectrumIndex> queue;
	private Thread thread;
	private Analysis analysis;
	
	public SimpleLoaderQueue(SimpleScanData data, Analysis analysis) {
		this(data, analysis, 1000);
	}
	public SimpleLoaderQueue(SimpleScanData data, Analysis analysis, int depth) {
		this.analysis = analysis;
		
		queue = new LinkedBlockingQueue<>(depth);
		thread = new Thread(() -> {
			while(true) {
				try {
					SpectrumIndex struct = queue.take();
					if (struct.spectrum != null) {
						this.analysis.process(struct.spectrum);
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

}



