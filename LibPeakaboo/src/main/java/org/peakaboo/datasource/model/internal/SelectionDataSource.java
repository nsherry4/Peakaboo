package org.peakaboo.datasource.model.internal;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.components.scandata.analysis.Analysis;
import org.peakaboo.datasource.model.components.scandata.analysis.DataSourceAnalysis;

import cyclops.ReadOnlySpectrum;
import net.sciencestudio.autodialog.model.Group;

/**
 * Represents a random selection of data points from another DataSource
 * @author NAS
 *
 */
public class SelectionDataSource implements SubsetDataSource, ScanData {

	private DataSource source;
	private List<Integer> selectedIndexes;
	private DataSourceAnalysis analysis;
	
	public SelectionDataSource(DataSource source, List<Integer> selectedIndexes) {
		this.source = source;
		this.selectedIndexes = selectedIndexes;
		
		//we don't reanalyze in the constructor for performance reasons
		this.analysis = new DataSourceAnalysis();
		this.analysis.init(source.getScanData().getAnalysis().channelsPerScan());
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
	public float minEnergy() {
		return source.getScanData().minEnergy();
	}
	

	@Override
	public String datasetName() {
		return "Subset of " + source.getScanData().datasetName();
	}

	@Override
	public Optional<Metadata> getMetadata() {
		return source.getMetadata();
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
	public void read(List<Path> files) throws Exception {
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

	@Override
	public Optional<Group> getParameters(List<Path> paths) {
		return Optional.empty();
	}

	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

	@Override
	public void reanalyze() {
		for (int i = 0; i < scanCount(); i++) {
			this.reanalyze(i);
		}
	}

	@Override
	public void reanalyze(int i) {
		this.analysis.process(get(i));
	}
	
	
	
}
