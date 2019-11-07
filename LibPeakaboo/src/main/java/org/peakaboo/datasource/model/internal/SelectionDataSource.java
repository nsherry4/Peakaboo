package org.peakaboo.datasource.model.internal;

import java.nio.file.Path;
import java.util.ArrayList;
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
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.IntPair;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;

/**
 * Represents a random selection of data points from another DataSource
 * @author NAS
 *
 */
public class SelectionDataSource implements SubsetDataSource, ScanData, DataSize {

	private DataSource source;
	private List<Integer> selectedIndexes;
	private DataSourceAnalysis analysis;
	
	private Coord<Integer> dimensions;
	private Coord<Integer> offset;
	private GridPerspective<Integer> sourceGrid, selectionGrid;
	
	/**
	 * Creates a new, derived data source from an existing data source
	 * @param source The datasource to derive from
	 * @param dimensions the dimensions of the data in the source datasource
	 * @param selectedIndexes The points in the original datasource to select
	 */
	public SelectionDataSource(DataSource source, Coord<Integer> dimensions, List<Integer> selectedIndexes) {
		this.source = source;
		this.selectedIndexes = new ArrayList<>(selectedIndexes);
		
		//we don't reanalyze in the constructor for performance reasons
		this.analysis = new DataSourceAnalysis();
		this.analysis.init(source.getScanData().getAnalysis().channelsPerScan());
		
		sourceGrid = new GridPerspective<Integer>(dimensions.x, dimensions.y, 0);
		int minx = dimensions.x;
		int miny = dimensions.y;
		int maxx = 0;
		int maxy = 0;
		for (int i : selectedIndexes) {
			IntPair coord = sourceGrid.getXYFromIndex(i);
			if (coord.first < minx) { minx = coord.first; }
			if (coord.second < miny) { miny = coord.second; }
			if (coord.first > maxx) { maxx = coord.first; }
			if (coord.second > maxy) { maxy = coord.second; }
		}
		
		this.dimensions = new Coord<>(maxx-minx+1, maxy-miny+1);
		offset = new Coord<>(minx, miny);
		selectionGrid = new GridPerspective<Integer>(this.dimensions.x, this.dimensions.y, 0);
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
		return Optional.of(this);
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

	@Override
	public Coord<Integer> getDataDimensions() {
		return dimensions;
	}

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		int sourceIndex = getOriginalIndex(index);
		IntPair sourceCoord = sourceGrid.getXYFromIndex(sourceIndex);
		
		//IntPair pair = selectionGrid.getXYFromIndex(index);
		//return new Coord<>(pair.first, pair.second);

		//IntPair sourceCoords = sourceGrid.getXYFromIndex(index);
		int x = sourceCoord.first - offset.x;
		int y = sourceCoord.second - offset.y;
		if (x < 0 || x >= dimensions.x || y < 0 || y >= dimensions.y) {
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds in a dataset of dimension " + dimensions);
		}
		return new Coord<Integer>(x, y);
	}
	
	
	
}
