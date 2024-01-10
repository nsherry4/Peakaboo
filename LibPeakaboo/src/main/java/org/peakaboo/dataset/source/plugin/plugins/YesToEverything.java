package org.peakaboo.dataset.source.plugin.plugins;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormatCompatibility;
import org.peakaboo.dataset.source.model.components.interaction.Interaction;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.framework.autodialog.model.Group;

/**
 * This data source says yes to any file it's asked to open
 */
public class YesToEverything implements DataSourcePlugin {

	private Interaction interaction;
	
	private static final String NAME = "Yes To Anything";
	private static final String DESC = "This data source says yes to any file it's asked to open";
	
	@Override
	public void read(DataSourceContext ctx) throws DataSourceReadException, IOException, InterruptedException {
	}

	@Override
	public Optional<Group> getParameters(List<DataInputAdapter> datafiles) throws DataSourceReadException, IOException {
		return Optional.empty();
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
		return new FileFormat() {
			
			@Override
			public String getFormatName() {
				// TODO Auto-generated method stub
				return NAME;
			}
			
			@Override
			public String getFormatDescription() {
				return DESC;
			}
			
			@Override
			public List<String> getFileExtensions() {
				return List.of(".txt", ".dat", ".hdf5", ".hdf", ".h5", ".csv", ".tsv");
			}
			
			@Override
			public FileFormatCompatibility compatibility(List<DataInputAdapter> filenames) throws IOException {
				return FileFormatCompatibility.MAYBE_BY_FILENAME;
			}
		};
	}

	@Override
	public ScanData getScanData() {
		return null;
	}

	@Override
	public Interaction getInteraction() {
		return this.interaction;
	}

	@Override
	public void setInteraction(Interaction interaction) {
		this.interaction = interaction;
	}

	@Override
	public String pluginName() {
		return NAME;
	}

	@Override
	public String pluginDescription() {
		return DESC;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "37b931e0-872b-494e-b168-a0003e108a76";
	}

}
