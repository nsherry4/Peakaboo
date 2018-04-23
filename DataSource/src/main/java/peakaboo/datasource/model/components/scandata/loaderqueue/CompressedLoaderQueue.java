package peakaboo.datasource.model.components.scandata.loaderqueue;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.encoders.CompoundEncoder;
import net.sciencestudio.scratch.encoders.compressors.Compressors;
import net.sciencestudio.scratch.encoders.serializers.Serializers;
import net.sciencestudio.scratch.single.Compressed;
import peakaboo.common.PeakabooLog;
import peakaboo.datasource.model.components.scandata.SimpleScanData;
import scitypes.ISpectrum;
import scitypes.Spectrum;

public class CompressedLoaderQueue implements LoaderQueue {

	class SpectrumIndex {
		public Compressed<Spectrum> spectrum;
		public int index;
	}
	
	private LinkedBlockingQueue<SpectrumIndex> queue;
	private Thread thread;
	private SimpleScanData data;
	private ScratchEncoder<Spectrum> encoder;
	
	public CompressedLoaderQueue(SimpleScanData data) {
		this(data, 1000);
	}
	public CompressedLoaderQueue(SimpleScanData data, int depth) {
		this.data = data;
		this.encoder = new CompoundEncoder<>(Serializers.fst(ISpectrum.class), Compressors.lz4fast());
		
		queue = new LinkedBlockingQueue<>(depth);
		thread = new Thread(() -> {
			while(true) {
				try {
					SpectrumIndex struct = queue.take();
					if (struct.spectrum != null) {
						if (struct.index == -1) {
							data.add(struct.spectrum.get());
						} else {
							data.set(struct.index, struct.spectrum.get());
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
		struct.spectrum = Compressed.create(s, this.encoder);
		queue.put(struct);
	}
	
	@Override
	public void finish() throws InterruptedException {
		queue.put(new SpectrumIndex());
		thread.join();
	}
	
}


