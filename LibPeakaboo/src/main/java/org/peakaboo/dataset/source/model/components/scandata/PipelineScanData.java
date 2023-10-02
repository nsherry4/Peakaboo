package org.peakaboo.dataset.source.model.components.scandata;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.CombinedAnalysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.plural.pipeline.Pipeline;
import org.peakaboo.framework.plural.pipeline.RunToCompletionStage;
import org.peakaboo.framework.plural.pipeline.Stage;
import org.peakaboo.framework.plural.pipeline.ThreadedStage;
import org.peakaboo.framework.scratch.single.Compressed;

public class PipelineScanData extends AbstractScanData {

	private static record CompressedEntry(int index, Compressed<Spectrum> compressed) {};
	
	
	private Pipeline<ScanEntry, Void> pipeline;
	
	//We do a separate analysis of each thread, then we merge the results at the end
	private List<Analysis> analyses = new ArrayList<>();
	private ThreadLocal<Analysis> localanalysis = ThreadLocal.withInitial(() -> {
		var a = new DataSourceAnalysis();
		analyses.add(a);
		return a;
	});
	private Analysis analysis;
	
	
	public PipelineScanData(String name) {
		this(name, null);
	}
	
	public PipelineScanData(String name, Consumer<Spectrum> preprocessor) {
		super(name);
				
		Stage<ScanEntry, ScanEntry> sAnalysis = RunToCompletionStage.visit("Analysis", e -> localanalysis.get().process(e.spectrum()));
		
		Stage<ScanEntry, CompressedEntry> sCompression = RunToCompletionStage.of(
				"Compression",
				e -> new CompressedEntry(
						e.index(), 
						Compressed.create(e.spectrum(), spectra.getEncoder())
				)
		);
		
		Stage<CompressedEntry, Void> sStore = RunToCompletionStage.sink("Store", e -> {
			spectra.setCompressed(e.index(), e.compressed());
		});
		
		Stage<ScanEntry, ScanEntry> sPreprocessor;
		if (preprocessor != null) {
			sPreprocessor = ThreadedStage.visit("Preprocessor", (int)(Plural.cores()*1.5), e -> preprocessor.accept(e.spectrum()));
		} else  {
			sPreprocessor = ThreadedStage.noop("Preprocessor", (int)(Plural.cores()*1.5));
		}
		
		
		pipeline = sPreprocessor.then(sAnalysis).then(sCompression).then(sStore);

		
	}
	
	public void submit(int index, Spectrum s) throws InterruptedException {
		pipeline.accept(new ScanEntry(index, s));
	}
	
	public void finish() {
		pipeline.finish();
		this.analysis = new CombinedAnalysis(analyses);
	}

	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

}
