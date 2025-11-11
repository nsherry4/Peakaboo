package org.peakaboo.controller.mapper.rawdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.internal.SelectionDataSource;
import org.peakaboo.framework.accent.numeric.Bounds;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.SISize;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.mapping.rawmap.RawMapSet;


/**
 * The RawDataController represents the data originally fed into the map
 * component from the plot after mapping has occurred and a {@link RawMapSet}
 * has been generated.
 */
public class RawDataController extends EventfulType<MapUpdateType> {

	RawDataModel mapModel;
		
	public RawDataController() {
		mapModel = new RawDataModel();
	}
	

	

	/**
	 * Sets the map's data model.
	 */
	public void setMapData(
			RawMapSet data,
			DataSet sourceDataset,
			String datasetName,
			List<Integer> badPoints,
			DetectorProfile detectorProfile		
	) {
	
		
		mapModel.mapResults = data;
		mapModel.sourceDataset = sourceDataset;
		mapModel.datasetTitle = datasetName;
		mapModel.badPoints = badPoints;
		
		if (sourceDataset.hasGenuineDataSize()) {
			mapModel.originalDimensionsProvided = true;
			mapModel.originalDimensions = sourceDataset.getDataSize().getDataDimensions();
		} else {
			mapModel.originalDimensionsProvided = false;
			mapModel.originalDimensions = null;
		}
		
		Optional<PhysicalSize> physical = sourceDataset.getPhysicalSize();
		if (physical.isPresent()) {
			mapModel.realDimensions = physical.get().getPhysicalDimensions();
			mapModel.realDimensionsUnits = physical.get().getPhysicalUnit();
		} else {
			mapModel.realDimensions = null;
			mapModel.realDimensionsUnits = null;
		}
		
		mapModel.detectorProfile = detectorProfile;

		updateListeners(MapUpdateType.DATA);

	}
	
	public int getMapSize() {
		return mapModel.mapSize();
	}


	
	
	
	public Coord<Integer> getOriginalDataDimensions() {
		return mapModel.originalDimensions;
	}
	
	public boolean hasOriginalDataDimensions() {
		return mapModel.originalDimensionsProvided;
	}
	
	public int getOriginalDataHeight() {
		return mapModel.originalDimensions.y;
	}
	public int getOriginalDataWidth() {
		return mapModel.originalDimensions.x;
	}
	
	
	
	/**
	 * Returns a list of indexes (referring to points in this map) which are invalid
	 * in the underlying source dataset (eg: the corners in a replotted circular selection)
	 */
	public List<Integer> getInvalidPoints() {
		List<Integer> invalidPoints = new ArrayList<>();
		//TODO: Hack
		DataSource ds = mapModel.sourceDataset.getDataSource();
		if (ds instanceof SelectionDataSource) {
			SelectionDataSource sds = (SelectionDataSource) ds;
			Coord<Integer> dims = sds.getDataDimensions();
			int max = dims.x * dims.y;
			for (int i = 0; i < max; i++) {
				if (!sds.isValidPoint(i)) { invalidPoints.add(i); }
			}
		}
		return invalidPoints;
	}

	/**
	 * Returns a list of indexes (referring not to points in this map but rather to
	 * <i>valid</i> points in this map) which the user has marked as bad. Note that
	 * these will not be usable as coordinates just by knowing a map's dimensions,
	 * since the map may be non-rectangular
	 */
	public List<Integer> getBadIndices() {
		return new ArrayList<>(mapModel.badPoints);
	}


	public String getDatasetTitle() {
		return mapModel.datasetTitle;
	}


	public void setDatasetTitle(String name) {
		mapModel.datasetTitle = name;
	}


	public Coord<Bounds<Number>> getRealDimensions() {
		return mapModel.realDimensions;
	}
	
	
	public SISize getRealDimensionUnits() {
		return mapModel.realDimensionsUnits;
	}
	

	public RawMapSet getMapResultSet() {
		return mapModel.mapResults;
	}

	public DetectorProfile getDetectorProfile() {
		return mapModel.detectorProfile;
	}


	/**
	 * Indicates if the data points in this map can be reliably mapped back to the correct spectra
	 */
	public boolean areAllPointsValid() {
		return mapModel.mapResults.areAllPointsValid();
	}
		
}
