package org.peakaboo.filter.plugins.background;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.dataset.StandardDataSet;
import org.peakaboo.dataset.io.PathDataInputAdapter;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.DataSource.DataSourceContext;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.plugin.plugins.PlainText;
import org.peakaboo.filter.model.AbstractBackgroundFilter;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.FileNameStyle;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class SpectrumBackgroundFilter extends AbstractBackgroundFilter {

	private Parameter<String> spectrumFile;
	private String loadedFile = null;
	private SpectrumView spectrum;
		
	@Override
	public String getFilterName() {
		return "Spectrum Subtraction";
	}

	@Override
	public String getFilterDescription() {
		return "Loads a background curve from another dataset and subtracts the other dataset's average spectrum from each spectrum";
	}

	@Override
	public void initialize() {
		FileFormat format = new PlainText().getFileFormat();
		spectrumFile = new Parameter<>("Plaintext Dataset", new FileNameStyle(format.getFormatName(), format.getFileExtensions()), "");
		addParameter(spectrumFile);
	}

	@Override
	public boolean canFilterSubset() {
		return false;
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "0.1";
	}

	@Override
	public String getFilterUUID() {
		return "33ed30e7-65fb-4972-80e8-95dce2811b05";
	}

	@Override
	protected SpectrumView getBackground(SpectrumView data, Optional<FilterContext> ctx, int percent) {
		if (loadedFile == null || spectrum == null) {
			return new ArraySpectrum(data.size());
		} else {
			//load it lazily on first use
			loadBackground(data);
			return SpectrumCalculations.multiplyBy(spectrum, percent/100.0f);
		}
	}
	
	private synchronized void loadBackground(SpectrumView data) {
		
		if (loadedFile == null) { return; }
		if (loadedFile.equals(spectrumFile.getValue())) { return; }

			
		//If they haven't given a file yet
		if (spectrumFile.getValue().length() == 0) {
			spectrum = new ArraySpectrum(data.size());
			return;
		}
		
		try {
			//try loading the file
			DataSource source = new PlainText();
			Path path = new File(spectrumFile.getValue()).toPath();
			var ctx = new DataSourceContext(List.of(new PathDataInputAdapter(path)));
			source.read(ctx);
			DataSet bgDataSet = new StandardDataSet(source);
			spectrum = bgDataSet.getAnalysis().averagePlot();
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load background from dataset", e);
			spectrum = new ArraySpectrum(data.size());
		}
		//now that we've loaded it (or failed), set the loadedFile so we don't constantly re-load it
		loadedFile = spectrumFile.getValue();

		
	}

}
