package peakaboo.datasource.internal;

import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.interaction.DataSourceInteraction;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import peakaboo.datasource.components.scandata.ScanData;
import scitypes.Spectrum;

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
	
	@Override
	public void setInteraction(DataSourceInteraction interaction) {
		this.backer.setInteraction(interaction);
	}
	@Override
	public DataSourceInteraction getInteraction() {
		return this.backer.getInteraction();
	}

	
	public DataSourceMetadata getMetadata() {
		return backer.getMetadata();
	}

	public boolean hasMetadata() {
		return backer.hasMetadata();
	}

	public DataSourceDimensions getDimensions() {
		return backer.getDimensions();
	}

	public boolean hasDimensions() {
		return backer.hasDimensions();
	}

	public DataSourceFileFormat getFileFormat() {
		return backer.getFileFormat();
	}

	public void read(String filename) throws Exception {
		backer.read(filename);
	}

	public void read(List<String> filenames) throws Exception {
		backer.read(filenames);
	}

	@Override
	public ScanData getScanData() {
		return backer.getScanData();
	}
	
	
	
}
