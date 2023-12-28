package org.peakaboo.dataset.source.model.internal;

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

/**
 * @author maxweld
 * 
 */
public class EmptyDataSource implements DataSource, FileFormat {

	// Data Source //
	
	@Override
	public Optional<Metadata> getMetadata() {
		return Optional.empty();
	}


	@Override
	public FileFormatCompatibility compatibility(List<DataInputAdapter> filenames) {
		return FileFormatCompatibility.NO;
	}

	@Override
	public List<String> getFileExtensions() {
		return Collections.emptyList();
	}


	@Override
	public void read(DataSourceContext ctx) throws DataSourceReadException, IOException, InterruptedException {
		throw new UnsupportedOperationException();
	}


	
	// DSScanData //
	


	@Override
	public String getFormatName() {
		return "Empty Format";
	}

	@Override
	public String getFormatDescription() {
		return "Empty Format Description";
	}

	
	@Override
	public Optional<DataSize> getDataSize() {
		return Optional.empty();
	}

	@Override
	public FileFormat getFileFormat() {
		return this;
	}

	@Override
	public void setInteraction(Interaction interaction) {
		//NOOP
	}
	
	@Override
	public Interaction getInteraction() {
		return null;
	}

	@Override
	public ScanData getScanData() {
		return new SimpleScanData("");
	}

	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return Optional.empty();
	}

	@Override
	public Optional<Group> getParameters(List<DataInputAdapter> paths) {
		return Optional.empty();
	}

	
}
