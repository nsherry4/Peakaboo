package org.peakaboo.controller.plotter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormatCompatibility;
import org.peakaboo.dataset.source.model.components.interaction.Interaction;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.model.components.scandata.SimpleScanData;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * Minimal DataSource for integration testing. Data is added programmatically
 * in the constructor; {@link #read} is a no-op.
 */
public class SyntheticDataSource implements DataSource, FileFormat {

	private final SimpleScanData scanData;

	public SyntheticDataSource(SimpleScanData scanData) {
		this.scanData = scanData;
	}

	/**
	 * Creates a SyntheticDataSource with a Gaussian peak at the Fe K-alpha
	 * position (~6.4 keV) using a 0–20 keV / 2048-channel calibration.
	 * Channel ~655 corresponds to 6.4 keV in that calibration.
	 */
	public static SyntheticDataSource createWithFeKPeak(int numScans, int channels, float peakAmplitude) {
		SimpleScanData data = new SimpleScanData("Synthetic Fe K");
		data.setMinEnergy(0f);
		data.setMaxEnergy(20f);

		// Fe K-alpha ~6.4 keV in 0-20 keV / 2048 channels => channel ~655
		float peakCentre = 6.4f / 20f * channels;
		float sigma = 8f; // spread in channels

		for (int s = 0; s < numScans; s++) {
			Spectrum spectrum = new ArraySpectrum(channels);
			for (int ch = 0; ch < channels; ch++) {
				float background = 5f;
				float gaussian = (float) (peakAmplitude * Math.exp(-0.5 * Math.pow((ch - peakCentre) / sigma, 2)));
				spectrum.set(ch, background + gaussian);
			}
			data.add(spectrum);
		}

		return new SyntheticDataSource(data);
	}

	// DataSource

	@Override
	public void read(DataSourceContext ctx) throws DataSourceReadException, IOException, InterruptedException {
		// no-op — data is added in constructor
	}

	@Override
	public Optional<Metadata> getMetadata() {
		return Optional.empty();
	}

	@Override
	public Optional<DataSize> getDataSize() {
		return Optional.empty();
	}

	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return Optional.empty();
	}

	@Override
	public FileFormat getFileFormat() {
		return this;
	}

	@Override
	public ScanData getScanData() {
		return scanData;
	}

	@Override
	public Interaction getInteraction() {
		return null;
	}

	@Override
	public void setInteraction(Interaction interaction) {
		// no-op
	}

	@Override
	public Optional<Group> getParameters(List<DataInputAdapter> paths) {
		return Optional.empty();
	}

	// FileFormat

	@Override
	public FileFormatCompatibility compatibility(List<DataInputAdapter> filenames) {
		return FileFormatCompatibility.NO;
	}

	@Override
	public List<String> getFileExtensions() {
		return Collections.emptyList();
	}

	@Override
	public String getFormatName() {
		return "Synthetic Test Format";
	}

	@Override
	public String getFormatDescription() {
		return "Synthetic data source for integration tests";
	}

}
