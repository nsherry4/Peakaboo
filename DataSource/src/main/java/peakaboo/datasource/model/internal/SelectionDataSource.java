package peakaboo.datasource.model.internal;

import java.io.File;
import java.util.List;

import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.interaction.Interaction;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;
import scitypes.ReadOnlySpectrum;

/**
 * Represents a random selection of data points from another DataSource
 * @author NAS
 *
 */
public class SelectionDataSource implements SubsetDataSource, ScanData {

	private DataSource source;
	private List<Integer> selectedIndexes;
	
	public SelectionDataSource(DataSource source, List<Integer> selectedIndexes) {
		this.source = source;
		this.selectedIndexes = selectedIndexes;
	}
	
	public boolean isContiguous() {
		return false;
	}
	
	@Override
	public ReadOnlySpectrum get(int index) throws IndexOutOfBoundsException {
		return source.getScanData().get(getOriginalIndex(index));
	}

	@Override
	public int scanCount() {
		return selectedIndexes.size();
	}

	@Override
	public String scanName(int index) {
		return source.getScanData().scanName(getOriginalIndex(index));
	}

	@Override
	public float maxEnergy() {
		return source.getScanData().maxEnergy();
	}

	@Override
	public String datasetName() {
		return source.getScanData().datasetName() + " Subset";
	}

	@Override
	public Metadata getMetadata() {
		return source.getMetadata();
	}

	@Override
	public DataSize getDataSize() {
		return null;
	}

	@Override
	public PhysicalSize getPhysicalSize() {
		return null;
	}

	@Override
	public FileFormat getFileFormat() {
		return source.getFileFormat();
	}

	@Override
	public void setInteraction(Interaction interaction) {
		throw new UnsupportedOperationException("Cannot set interaction in derived DataSource");
	}

	@Override
	public Interaction getInteraction() {
		return source.getInteraction();
	}

	@Override
	public ScanData getScanData() {
		return this;
	}

	@Override
	public void read(File file) throws Exception {
		throw new UnsupportedOperationException("Cannot read in derived DataSource");
	}

	@Override
	public void read(List<File> files) throws Exception {
		throw new UnsupportedOperationException("Cannot read in derived DataSource");
	}

	
	@Override
	public int getOriginalIndex(int index) {
		return selectedIndexes.get(index);
	}

	@Override
	public int getUpdatedIndex(int originalIndex) {
		return selectedIndexes.indexOf(originalIndex);
	}

}
