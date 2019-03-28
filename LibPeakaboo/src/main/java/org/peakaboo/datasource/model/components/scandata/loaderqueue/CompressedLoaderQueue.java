package org.peakaboo.datasource.model.components.scandata.loaderqueue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooConfiguration;
import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasource.model.components.scandata.SimpleScanData;
import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.single.Compressed;

/**
 * CompressedLoaderQueue moves the compression into the submit call, 
 * meaning that it's not performed on the queue thread.
 * @author NAS
 *
 */
public class CompressedLoaderQueue implements LoaderQueue {

	class SpectrumIndex {
		public Compressed<Spectrum> spectrum;
		public int index;
	}
	
	private LinkedBlockingQueue<SpectrumIndex> queue;
	private Thread thread;
	private ScratchEncoder<Spectrum> encoder;
	private Analysis analysis;
	
	public CompressedLoaderQueue(SimpleScanData data, Analysis analysis) {
		this(data, analysis, 1000);
	}
	public CompressedLoaderQueue(SimpleScanData data, Analysis analysis, int depth) {
		this.encoder = PeakabooConfiguration.spectrumEncoder;
		this.analysis = analysis;
		
		queue = new LinkedBlockingQueue<>(depth);
		thread = new Thread(() -> {
			while(true) {
				try {
					SpectrumIndex struct = queue.take();
					if (struct.spectrum != null) {
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
		thread.setName("CompressedLoaderQueue");
		thread.start();

	}
	
	@Override
	public void submit(Spectrum s) throws InterruptedException {
		submit(-1, s);
	}
	
	@Override
	public void submit(int index, Spectrum s) throws InterruptedException {
		SpectrumIndex struct = new SpectrumIndex();
		struct.index = index;
		this.analysis.process(s);
		struct.spectrum = Compressed.create(s, this.encoder);
		queue.put(struct);
	}
	
	@Override
	public void finish() throws InterruptedException {
		queue.put(new SpectrumIndex());
		thread.join();
	}

	
}


