package org.peakaboo.dataset.source.model.components.scandata;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.peakaboo.app.Settings;
import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.DummyAnalysis;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.plural.pipeline.Pipeline;
import org.peakaboo.framework.plural.pipeline.Stage;
import org.peakaboo.framework.plural.pipeline.ThreadedStage;
import org.peakaboo.framework.scratch.single.Compressed;

public class PipelineScanData extends AbstractScanData {

	private static record CompressedEntry(int index, Compressed<Spectrum> compressed) {};
	
	
	private Pipeline<ScanEntry, Void> pipeline;
	
	//We do a separate analysis of each thread, then we merge the results at the end
	private List<Analysis> analyses = new ArrayList<>();
	private ThreadLocal<Analysis> localanalysis = ThreadLocal.withInitial(() -> {
		synchronized(analyses) {
			var a = new DataSourceAnalysis();
			analyses.add(a);
			return a;
		}
	});
	private Analysis analysis;
	
	
	public PipelineScanData(String name) {
		this(name, null);
	}
	
	public PipelineScanData(String name, Consumer<Spectrum> preprocessor) {
		super(name);
		int cores = Settings.getThreadCount();
		
		Stage<ScanEntry, Void> processor = ThreadedStage.of("Processing Scans", cores, scan -> {
			
			Spectrum spectrum = scan.spectrum();
			int index = scan.index();
			
			if (preprocessor != null) {
				preprocessor.accept(spectrum);
			}
			
			localanalysis.get().process(spectrum);
			
			CompressedEntry compressed = new CompressedEntry(
				index, 
				Compressed.create(spectrum, spectra.getEncoder())
			);
			
			spectra.setCompressed(index, compressed.compressed());
			
			return null;
			
		});
			
		pipeline = new Pipeline<ScanEntry, Void>(processor);

	}
	
	public void submit(int index, Spectrum s) throws InterruptedException {
		pipeline.accept(new SimpleScanEntry(index, s));
	}
	
	public void submit(ScanEntry scan) {
		pipeline.accept(scan);
	}
	
	public void finish() {
		pipeline.finish();
		this.analysis = DataSourceAnalysis.merge(analyses);
	}

	public void abort() {
		pipeline.abort();
		// Ideally this will never be queried, but better to not leave this as null
		this.analysis = new DummyAnalysis();
		
	}
	
	
	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

}
