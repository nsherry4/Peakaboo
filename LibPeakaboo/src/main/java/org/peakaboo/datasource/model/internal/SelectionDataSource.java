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
import org.peakaboo.framework.cyclops.util.Mutable;

/**
 * Represents a random selection of data points from another DataSource
 * @author NAS
 *
 */
public class SelectionDataSource implements SubsetDataSource, ScanData, DataSize {

	private DataSource source;
	private Coord<Integer> sourceDimensions;
	private List<Integer> selectedIndexes;
	private DataSourceAnalysis analysis;
	
	private Coord<Integer> dimensions;
	private Coord<Integer> offset;
	private GridPerspective<Integer> sourceGrid;
	private Mutable<Boolean> rectangular = new Mutable<>(null);
	
	/**
	 * Creates a new, derived data source from an existing data source
	 * @param source The datasource to derive from
	 * @param dimensions the dimensions of the data in the source datasource
	 * @param selectedIndexes The points in the original datasource to select
	 */
	public SelectionDataSource(DataSource source, Coord<Integer> dimensions, List<Integer> selectedIndexes) {
		this.source = source;
		this.selectedIndexes = new ArrayList<>(selectedIndexes);
		this.sourceDimensions = dimensions;
		
		//we don't reanalyze in the constructor for performance reasons
		this.analysis = new DataSourceAnalysis();
		this.analysis.init(source.getScanData().getAnalysis().channelsPerScan());
		
		sourceGrid = new GridPerspective<>(dimensions.x, dimensions.y, 0);
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
		
		int x = sourceCoord.first - offset.x;
		int y = sourceCoord.second - offset.y;
		if (x < 0 || x >= dimensions.x || y < 0 || y >= dimensions.y) {
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds in a dataset of dimension " + dimensions);
		}
		return new Coord<>(x, y);
	}
	

	@Override
	public boolean isRectangular() {
		if (rectangular.get() == null) {
			rectangular.set(calcRectangular());
		}
		return rectangular.get();
	}
	
	private boolean calcRectangular() {
		//we need to use the real dimensions here because non-spacial 
		//map modes will have to translate back to actual map points
		//before we can deal with it as a rectangular area of spectra
		GridPerspective<Float> grid = new GridPerspective<>(
				sourceDimensions.x, 
				sourceDimensions.y, 
				0f);
		int minx = grid.width;
		int miny = grid.height;
		int maxx = 0;
		int maxy = 0;
		boolean[] selected = new boolean[grid.size()];
		for (int i : selectedIndexes) {
			selected[i] = true;
			IntPair coord = grid.getXYFromIndex(i);
			minx = Math.min(minx, coord.first);
			miny = Math.min(miny, coord.second);
			maxx = Math.max(maxx, coord.first);
			maxy = Math.max(maxy, coord.second);
		}
		
		for (int x = minx; x <= maxx; x++) {
			for (int y = miny; y <= maxy; y++) {
				int index = grid.getIndexFromXY(x, y);
				if (!selected[index]) {
					return false;
				}
			}
		}

		return true;
	}

	
	
}
