package peakaboo.datasource.internal;

import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.interaction.DataSourceInteraction;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
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

	

	public Spectrum get(int index) throws IndexOutOfBoundsException {
		return backer.get(index);
	}

	public int scanCount() {
		return backer.scanCount();
	}

	public DataSourceMetadata getMetadata() {
		return backer.getMetadata();
	}

	public List<String> scanNames() {
		return backer.scanNames();
	}

	public boolean hasMetadata() {
		return backer.hasMetadata();
	}

	public float maxEnergy() {
		return backer.maxEnergy();
	}

	public String datasetName() {
		return backer.datasetName();
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
	
	
	
}
