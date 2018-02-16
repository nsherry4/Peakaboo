package peakaboo.datasource.model.components.scandata;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.encoders.CompoundEncoder;
import net.sciencestudio.scratch.encoders.compressors.Compressors;
import net.sciencestudio.scratch.encoders.compressors.LZ4CompressionEncoder;
import net.sciencestudio.scratch.encoders.serializers.KryoSerializingEncoder;
import net.sciencestudio.scratch.encoders.serializers.Serializers;
import net.sciencestudio.scratch.single.Compressed;
import scitypes.ISpectrum;
import scitypes.Spectrum;

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
		this.encoder = new CompoundEncoder<>(Serializers.kryo(ISpectrum.class), Compressors.lz4());
		
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
