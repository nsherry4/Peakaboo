package peakaboo.datasource.model.components.scandata;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import scitypes.ISpectrum;
import scitypes.Spectrum;
import scratch.ScratchEncoder;
import scratch.encoders.CompoundEncoder;
import scratch.encoders.KryoSerializingEncoder;
import scratch.encoders.LZ4CompressionEncoder;
import scratch.single.Compressed;

public class CompressedLoaderQueue implements LoaderQueue {

	private LinkedBlockingQueue<Optional<Compressed<Spectrum>>> queue;
	private Thread thread;
	private SimpleScanData data;
	private ScratchEncoder<Spectrum> encoder;
	
	public CompressedLoaderQueue(SimpleScanData data) {
		this(data, 1000);
	}
	public CompressedLoaderQueue(SimpleScanData data, int depth) {
		this.data = data;
		this.encoder = new CompoundEncoder<>(new KryoSerializingEncoder<>(ISpectrum.class, float[].class), new LZ4CompressionEncoder());
		
		queue = new LinkedBlockingQueue<>(depth);
		thread = new Thread(() -> {
			while(true) {
				try {
					Optional<Compressed<Spectrum>> option = queue.take();
					if (option.isPresent()) {
						data.add(option.get().get()); 
					} else {
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
					return;
				}
			}
		});
		thread.start();

	}
	
	public void submit(Spectrum s) throws InterruptedException {
		if (queue != null) {
			queue.put(Optional.of(Compressed.create(s, this.encoder)));
		} else {
			data.add(s);
		}
	}
	
	public void finish() throws InterruptedException {
		if (queue != null) {
			queue.put(Optional.ofNullable(null));
			thread.join();
		}
	}
	
}
