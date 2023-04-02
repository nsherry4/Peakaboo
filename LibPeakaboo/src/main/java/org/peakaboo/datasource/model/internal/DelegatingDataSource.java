package org.peakaboo.datasource.model.internal;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.datafile.DataFile;
import org.peakaboo.framework.autodialog.model.Group;

public class DelegatingDataSource implements DataSource {

	private DataSource backer;
	
	public DelegatingDataSource() {
		this(new EmptyDataSource());
	}
	
	public DelegatingDataSource(DataSource backer) {
		this.backer = backer;
	}
	
	public void setDataSource(DataSource backer) {
		this.backer = backer;
	}
	
	public DataSource getDataSource() {
		return backer;
	}
	
	@Override
	public void setInteraction(Interaction interaction) {
		this.backer.setInteraction(interaction);
	}
	@Override
	public Interaction getInteraction() {
		return this.backer.getInteraction();
	}

	
	public Optional<Metadata> getMetadata() {
		return backer.getMetadata();
	}

	public Optional<DataSize> getDataSize() {
		return backer.getDataSize();
	}

	public FileFormat getFileFormat() {
		return backer.getFileFormat();
	}


	public void read(List<DataFile> files) throws DataSourceReadException, IOException, InterruptedException {
		backer.read(files);
	}

	@Override
	public ScanData getScanData() {
		return backer.getScanData();
	}

	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return backer.getPhysicalSize();
	}
	
	@Override
	public Optional<Group> getParameters(List<DataFile> paths) throws DataSourceReadException, IOException {
		return backer.getParameters(paths);
	}
	
}
