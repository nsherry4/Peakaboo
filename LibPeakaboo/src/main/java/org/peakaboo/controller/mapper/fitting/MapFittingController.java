package org.peakaboo.controller.mapper.fitting;

import java.util.List;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.CompositeModeController;
import org.peakaboo.controller.mapper.fitting.modes.CorrelationModeController;
import org.peakaboo.controller.mapper.fitting.modes.ModeController;
import org.peakaboo.controller.mapper.fitting.modes.OverlayModeController;
import org.peakaboo.controller.mapper.fitting.modes.RatioModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.eventful.EventfulListener;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;
import org.peakaboo.mapping.filter.model.AreaMap;




public class MapFittingController extends EventfulType<MapUpdateType> {
	
	private MappingController map;
	
	private EventfulCache<MapModeData> mapModeData;
	
	private RatioModeController ratio;
	private OverlayModeController overlay;
	private CompositeModeController composite;
	private CorrelationModeController correlation;
	
	
	private MapModes displayMode;
	//TODO: should this be in MapSettingsController?
	private MapScaleMode mapScaleMode;
	
	
	
	
	public MapFittingController(MappingController map){
		this.map = map;
		
		displayMode = MapModes.COMPOSITE;
		mapScaleMode = MapScaleMode.ABSOLUTE;
				
		EventfulListener modeListener = () -> updateListeners(MapUpdateType.DATA_OPTIONS);
		
		ratio = new RatioModeController(map);
		ratio.addListener(modeListener);
		
		overlay = new OverlayModeController(map);
		overlay.addListener(modeListener);
		
		composite = new CompositeModeController(map);
		composite.addListener(modeListener);
		
		correlation = new CorrelationModeController(map);
		correlation.addListener(modeListener);
		
		mapModeData = new EventfulNullableCache<>(this::calcMapModeData);
		map.addListener(t -> {
			if (t == null) {
				return;
			}
			switch (t) {
				case AREA_SELECTION:
				case POINT_SELECTION:
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
	
	

	public MapScaleMode getMapScaleMode()
	{
		return this.mapScaleMode;
	}


	public void setMapScaleMode(MapScaleMode mode)
	{
		this.mapScaleMode = mode;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public MapModes getMapDisplayMode()
	{
		return this.displayMode;
	}

	public MapModeData getMapModeData() {
		return this.mapModeData.getValue();
	}


	public void setMapDisplayMode(MapModes mode)
	{		
		this.displayMode = mode;
		this.mapModeData.invalidate();
		updateListeners(MapUpdateType.DATA_OPTIONS);
	}
	
	public ModeController getActiveMode() {
		switch (getMapDisplayMode()) {
		case COMPOSITE:
			return this.composite;
		case OVERLAY:
			return this.overlay;
		case RATIO:
			return this.ratio;
		case CORRELATION:
			return this.correlation;
		}
		return null;
	}
	
	private MapModeData calcMapModeData() {
		return getActiveMode().getData();
	}

	

	/*
	 * POST FILTERING
	 */
	public String getInfoAtPoint(Coord<Integer> coord) {
		if (mapModeData == null) {
			return "";
		}
		return mapModeData.getValue().getInfoAtCoord(coord);
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
			return new ISpectrum(x * y);
		}
		
		return new ISpectrum(sum.getData());
		
	}



	

	

	


	/**
	 * Indicates if this TransitionSeries is enabled, or disabled (due to a lack of calibration, for example)
	 */
	public boolean getTransitionSeriesEnabled(ITransitionSeries ts) {
		if (getCalibrationProfile().isEmpty()) {
			return true;
		}
		return getCalibrationProfile().contains(ts);
	}
	


	public CalibrationProfile getCalibrationProfile() {
		return map.rawDataController.getCalibrationProfile();
	}



	
	public CorrelationModeController correlationMode() {
		return correlation;
	}

	public CompositeModeController compositeMode() {
		return composite;
	}
	
	public OverlayModeController overlayMode() {
		return overlay;
	}
	
	public RatioModeController ratioMode() {
		return ratio;
	}

	public List<ITransitionSeries> getAllTransitionSeries() {
		return getActiveMode().getAll();
	}

	
}
