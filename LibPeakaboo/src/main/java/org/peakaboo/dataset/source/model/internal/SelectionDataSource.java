package org.peakaboo.dataset.source.model.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.interaction.Interaction;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.IntPair;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.util.Mutable;

/**
 * Represents a random selection of data points from another DataSource
 * @author NAS
 *
 */
public class SelectionDataSource implements SubsetDataSource, ScanData, DataSize {

	private DataSource backer;
	private Coord<Integer> backingDimensions;
	private List<Integer> backingIndexes, mapIndexes;
	private GridPerspective<Integer> backerGrid;
	private DataSourceAnalysis analysis;
	
	private Coord<Integer> derivedDimensions;
	private Coord<Integer> offset;
	private GridPerspective<Integer> derivedGrid;
	// 1d list the size of the derived dimensions, each entry stores the offset into
	// backingIndexes this entry maps to, or -1 if this entry has no backing value
	// (and is thus invalid)
	private List<Integer> derivedIndexLookup;
	private Mutable<Boolean> rectangular = new Mutable<>(null);
	
	/**
	 * Creates a new, derived data source from an existing data source
	 * @param source The datasource to derive from
	 * @param dimensions the dimensions of the data in the source datasource
	 * @param selectedIndexes The points in the original datasource to select
	 */
	public SelectionDataSource(DataSource source, Coord<Integer> dimensions, List<Integer> selectedIndexes) {
		this.backer = source;
		this.mapIndexes = new ArrayList<>(selectedIndexes);
		this.mapIndexes.sort(Integer::compare);
		this.backingDimensions = dimensions;
		
		// if the backer is another SubsetDataSource controller, we need to translate
		// the points from map indexes (which may contain invalid points that shouldn't
		// be counded) to subset indexes
		if (backer instanceof SelectionDataSource) {
			SelectionDataSource selbacker = (SelectionDataSource) backer;
			this.backingIndexes = this.mapIndexes.stream().map(i -> selbacker.getBackingIndexForDerivedPoint(i)).collect(Collectors.toList());
		} else {
			this.backingIndexes = new ArrayList<>(mapIndexes);
		}
		
		//we don't reanalyze in the constructor for performance reasons
		this.analysis = new DataSourceAnalysis();
		this.analysis.init(backer.getScanData().getAnalysis().channelsPerScan());
		
		
		backerGrid = new GridPerspective<>(backingDimensions.x, backingDimensions.y, 0);
		int minx = backingDimensions.x;
		int miny = backingDimensions.y;
		int maxx = 0;
		int maxy = 0;
		/*
		 * we use mapIndexes here because we don't want indexes shifted due to missing
		 * (invalid) points, we want to calculate the offset against the real backing
		 * map
		 */
		for (int i : this.mapIndexes) {
			IntPair coord = backerGrid.getXYFromIndex(i);
			if (coord.first < minx) { minx = coord.first; }
			if (coord.second < miny) { miny = coord.second; }
			if (coord.first > maxx) { maxx = coord.first; }
			if (coord.second > maxy) { maxy = coord.second; }
		}
		
		this.derivedDimensions = new Coord<>(maxx-minx+1, maxy-miny+1);
		offset = new Coord<>(minx, miny);
		this.derivedGrid = new GridPerspective<>(this.derivedDimensions.x, this.derivedDimensions.y, 0);
		
		derivedIndexLookup = new ArrayList<>();
		for (int i = 0; i < derivedGrid.size(); i++) {
			derivedIndexLookup.add(-1);
		}
		for (int i = 0; i < this.scanCount(); i++) {
			Coord<Integer> derivedCoord = getDataCoordinatesAtIndex(i);
			int derivedIndex = derivedGrid.getIndexFromXY(derivedCoord);
			derivedIndexLookup.set(derivedIndex, i);
		}
		
	}

	
	@Override
	public SpectrumView get(int index) throws IndexOutOfBoundsException {
		return backer.getScanData().get(getBackingIndex(index));
	}

	@Override
	public int scanCount() {
		return backingIndexes.size();
	}

	@Override
	public String scanName(int index) {
		return backer.getScanData().scanName(getBackingIndex(index));
	}

	@Override
	public float maxEnergy() {
		return backer.getScanData().maxEnergy();
	}
	
	@Override
	public float minEnergy() {
		return backer.getScanData().minEnergy();
	}
	

	@Override
	public String datasetName() {
		return "Subset of " + backer.getScanData().datasetName();
	}

	@Override
	public Optional<Metadata> getMetadata() {
		return backer.getMetadata();
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
		return backer.getFileFormat();
	}

	@Override
	public void setInteraction(Interaction interaction) {
		throw new UnsupportedOperationException("Cannot set interaction in derived DataSource");
	}

	@Override
	public Interaction getInteraction() {
		return backer.getInteraction();
	}

	@Override
	public ScanData getScanData() {
		return this;
	}


	@Override
	public void read(DataSourceContext ctx) throws DataSourceReadException, IOException, InterruptedException {
		throw new UnsupportedOperationException("Cannot read in derived DataSource");
	}

	
	/**
	 * Given an index into this DataSource's data, return the index from the backer
	 * data
	 */
	@Override
	public int getBackingIndex(int index) {
		return backingIndexes.get(index);
	}

	/**
	 * Given an index into the backing data, return the comparable index into this
	 * DataSource's data
	 */
	@Override
	public int getUpdatedIndex(int originalIndex) {
		return backingIndexes.indexOf(originalIndex);
	}

	@Override
	public Optional<Group> getParameters(List<DataInputAdapter> datafiles) {
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
		return derivedDimensions;
	}

	//Index here refers to position in this data source
	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		//index in backing datasource
		int backingIndex = getBackingIndex(index);
		int x, y;
		//TODO: should this check the backer's DataSize availability instead?
		if (backer instanceof SelectionDataSource) {
			SelectionDataSource selbacker = (SelectionDataSource) backer;
			Coord<Integer> backingCoord = selbacker.getDataCoordinatesAtIndex(backingIndex);
			x = backingCoord.x;
			y = backingCoord.y;
		} else {
			IntPair backingCoord = backerGrid.getXYFromIndex(backingIndex);
			x = backingCoord.first;
			y = backingCoord.second;
		}
		
		x = x - offset.x;
		y = y - offset.y;
		if (x < 0 || x >= derivedDimensions.x || y < 0 || y >= derivedDimensions.y) {
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds in a dataset of dimension " + derivedDimensions);
		}
		return new Coord<>(x, y);
	}
	
	/**
	 * Returns an int (representing the backing index) for the given x,y values
	 * representing the derived dimensions specified by this DataSource. If the x,y
	 * values do not translate back to a valid index (eg because the original
	 * selection was not rectangular), it returns Empty
	 */
	private int getBackingIndexForDerivedPoint(int x, int y) {
		if (!derivedGrid.boundsCheck(x, y)) {
			return -1;
		}
		Coord<Integer> coord = new Coord<>(x, y);
		int derivedIndex = derivedGrid.getIndexFromXY(coord);
		return getBackingIndexForDerivedPoint(derivedIndex);
	}
	
	
	/**
	 * translates a map index based on the dimensions of this datasource into an
	 * index into the backingIndexes array
	 */
	private int getBackingIndexForDerivedPoint(int derivedIndex) {
		return derivedIndexLookup.get(derivedIndex);
	}
	
	

	
	/**
	 * Indicates if this point is valid, meaning that it has a real value in the
	 * backing dataset. The x and y parameters refer to the coordinates of a point
	 * in the derived dimensions (not the backing dimensions) specified by this
	 * DataSource
	 */
	public boolean isValidPoint(int x, int y) {
		return getBackingIndexForDerivedPoint(x, y) != -1;
	}
	
	/**
	 * Indicates if the point is valid, meaning that it has a real value in the
	 * backing dataset. The index given here is in reference to the derived
	 * dimensions (not the backing dimensions) specified by this DataSource
	 */
	public boolean isValidPoint(int derivedIndex) {
		return getBackingIndexForDerivedPoint(derivedIndex) != -1;
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
				backingDimensions.x, 
				backingDimensions.y, 
				0f);
		int minx = grid.width;
		int miny = grid.height;
		int maxx = 0;
		int maxy = 0;
		boolean[] selected = new boolean[grid.size()];
		for (int i : backingIndexes) {
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
