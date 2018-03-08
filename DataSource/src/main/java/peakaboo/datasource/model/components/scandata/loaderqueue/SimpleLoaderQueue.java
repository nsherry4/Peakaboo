package peakaboo.datasource.model.components.scandata.loaderqueue;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.datasource.model.components.scandata.SimpleScanData;
import scitypes.Spectrum;

public class SimpleLoaderQueue implements LoaderQueue {
	
	private LinkedBlockingQueue<Optional<Spectrum>> queue;
	private Thread thread;
	private SimpleScanData data;
	public SimpleLoaderQueue(SimpleScanData data) {
		this(data, 1000);
	}
	public SimpleLoaderQueue(SimpleScanData data, int depth) {
		this.data = data;
		
		queue = new LinkedBlockingQueue<>(depth);
		thread = new Thread(() -> {
			while(true) {
				try {
					Optional<Spectrum> option = queue.take();
					if (option.isPresent()) {
						data.add(option.get()); 
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
		thread.start();

	}
	
	@Override
	public void submit(Spectrum s) throws InterruptedException {
		if (queue != null) {
			queue.put(Optional.of(s));
		} else {
			data.add(s);
		}
	}
	
	
	@Override
	public void finish() throws InterruptedException {
		if (queue != null) {
			queue.put(Optional.ofNullable(null));
			thread.join();
		}
	}
}
