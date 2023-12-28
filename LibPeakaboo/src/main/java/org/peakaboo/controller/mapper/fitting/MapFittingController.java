package org.peakaboo.controller.mapper.fitting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.ModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.display.map.modes.MapModeData.CoordInfo;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.eventful.EventfulListener;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;
import org.peakaboo.mapping.filter.model.AreaMap;




public class MapFittingController extends EventfulType<MapUpdateType> {
	
	private MappingController map;
	
	private EventfulCache<MapModeData> mapModeData;
	
	
	//TODO: should this be in MapSettingsController?
	private MapScaleMode mapScaleMode;
	
	
	private Map<String, ModeController> modeControllers = new LinkedHashMap<>();
	private String displayMode;
	
	
	public MapFittingController(MappingController map){
		this.map = map;
		
		mapScaleMode = MapScaleMode.ABSOLUTE;
				
		EventfulListener modeListener = () -> updateListeners(MapUpdateType.DATA_OPTIONS);

		displayMode = ModeControllerRegistry.get().defaultType();
		for (var type : ModeControllerRegistry.get().typeNames()) {
			var modeController = ModeControllerRegistry.get().create(type, map);
			modeController.addListener(modeListener);
			modeControllers.put(type, modeController);
		}
				
		mapModeData = new EventfulNullableCache<>(this::calcMapModeData);
		map.addListener(t -> {
			if (t == null) {
				return;
			}
			switch (t) {
				case SELECTION:
					break;
				
				case DATA:
				case DATA_OPTIONS:
				case DATA_SIZE:
				case FILTER:
				case UI_OPTIONS:
					mapModeData.invalidate();			
			}
		});
		

		
	}
	
	
	public Optional<ModeController> getModeController(String mode) {
		if (!modeControllers.containsKey(mode)) {
			return Optional.empty();
		} else {
			return Optional.of(modeControllers.get(mode));
		}
	}
	
	public MapScaleMode getMapScaleMode() {
		return this.mapScaleMode;
	}


	public void setMapScaleMode(MapScaleMode mode) {
		this.mapScaleMode = mode;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public String getMapDisplayMode() {
		return this.displayMode;
	}

	public MapModeData getMapModeData() {
		return this.mapModeData.getValue();
	}


	public void setMapDisplayMode(String mode) {		
		this.displayMode = mode;
		this.mapModeData.invalidate();
		updateListeners(MapUpdateType.DATA_OPTIONS);
	}
	
	public ModeController getActiveMode() {
		return modeControllers.get(displayMode);
	}
	
	private MapModeData calcMapModeData() {
		return getActiveMode().getData();
	}

	

	/*
	 * POST FILTERING
	 */
	public Optional<CoordInfo> getInfoAtPoint(Coord<Integer> coord) {
		if (mapModeData == null) {
			return Optional.empty();
		}
		return mapModeData.getValue().getCoordInfo(coord);
	}
	
	

	


	
	
	public String mapAsCSV()
	{
		StringBuilder sb = new StringBuilder();

		MapModeData data = calcMapModeData();		

		Coord<Integer> size = data.getSize();
		for (int y = 0; y < size.y; y++) {
			if (y != 0) sb.append("\n");
			
			for (int x = 0; x < size.x; x++) {
				if (x != 0) sb.append(", ");
				sb.append(data.getValueAtCoord(new Coord<Integer>(x, y)));
			}
		}
			
		return sb.toString();

	}

	






	



	


	


	
	

	


	public String mapLongTitle(){ 
		return getActiveMode().longTitle();
	}	


	public synchronized Spectrum sumAllTransitionSeriesMaps() {		
		
		AreaMap sum = map.getFiltering().getSummedMap();

		//When there are no maps, the sum will be null
		if (sum == null) {
			int y = map.getFiltering().getFilteredDataHeight();
			int x = map.getFiltering().getFilteredDataWidth();
			return new ArraySpectrum(x * y);
		}
		
		return new ArraySpectrum(sum.getData());
		
	}



	

	

	


	/**
	 * Indicates if this TransitionSeries is enabled, or disabled (due to a lack of calibration, for example)
	 */
	public boolean getTransitionSeriesEnabled(ITransitionSeries ts) {
		if (getDetectorProfile().isEmpty()) {
			return true;
		}
		return getDetectorProfile().contains(ts);
	}
	


	public DetectorProfile getDetectorProfile() {
		return map.rawDataController.getDetectorProfile();
	}
		
	public List<ITransitionSeries> getAllTransitionSeries() {
		return getActiveMode().getAll();
	}

	
}
