package org.peakaboo.datasource.model.components.scandata;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.components.scandata.analysis.CombinedAnalysis;
import org.peakaboo.datasource.model.components.scandata.analysis.DataSourceAnalysis;
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
				
		Stage<ScanEntry, ScanEntry> sAnalysis = ThreadedStage.visit("Analysis", 2, e -> localanalysis.get().process(e.spectrum()));
		
		Stage<ScanEntry, CompressedEntry> sCompression = ThreadedStage.of(
				"Compression", 2,
				e -> new CompressedEntry(
						e.index(), 
						Compressed.create(e.spectrum(), spectra.getEncoder())
				)
		);
		
		Stage<CompressedEntry, Void> sStore = ThreadedStage.sink("Store", 1, e -> {
			int index = e.index();
			if (index == -1) {
				spectra.addCompressed(e.compressed());
			} else {
				spectra.setCompressed(e.index(), e.compressed());
			}
		});
		
		if (preprocessor != null) {
			Stage<ScanEntry, ScanEntry> sPreprocessor = ThreadedStage.visit("Preprocessor", 2, e -> preprocessor.accept(e.spectrum()));
			pipeline = sPreprocessor.then(sAnalysis).then(sCompression).then(sStore);
		} else {
			pipeline = sAnalysis.then(sCompression).then(sStore);	
		}
		
		
	}
	
	public void submit(int index, Spectrum s) throws InterruptedException {
		pipeline.accept(new ScanEntry(index, s));
	}
	
	public void submit(Spectrum s) throws InterruptedException {
		pipeline.accept(new ScanEntry(-1, s));
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
