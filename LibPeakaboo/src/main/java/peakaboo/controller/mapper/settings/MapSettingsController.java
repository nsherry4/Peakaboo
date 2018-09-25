package peakaboo.controller.mapper.settings;



import eventful.EventfulType;
import peakaboo.controller.mapper.MappingController;



public class MapSettingsController extends EventfulType<String>
{
		
	//components
	private MapFittingSettings 	mapFittings;	
	private AreaSelection areaSelection;
	private PointsSelection pointsSelection;
	private MapViewSettings viewSettings;
	
	
	
	public MapSettingsController(MappingController map, MapViewSettings copyViewSettings)
	{
		
		mapFittings = new MapFittingSettings(map);
		mapFittings.addListener(this::updateListeners);
		
		//create selection models and pass their events along
		areaSelection = new AreaSelection(map);
		areaSelection.addListener(this::updateListeners);
		
		pointsSelection = new PointsSelection(map);
		pointsSelection.addListener(this::updateListeners);
		
		if (copyViewSettings == null) {
			viewSettings = new MapViewSettings(map);
		} else {
			viewSettings = new MapViewSettings(map, copyViewSettings);
		}
		viewSettings.addListener(this::updateListeners);
		
		

	}


	public AreaSelection getAreaSelection() {
		return areaSelection;
	}

	

	public PointsSelection getPointsSelection() {
		return pointsSelection;
	}


	public MapFittingSettings getMapFittings() {
		return mapFittings;
	}


	public MapViewSettings getView() {
		return viewSettings;
	}


	
	

	
	









}
