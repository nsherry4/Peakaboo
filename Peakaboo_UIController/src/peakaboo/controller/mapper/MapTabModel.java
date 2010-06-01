package peakaboo.controller.mapper;

import java.util.List;

import peakaboo.mapping.MapResultSet;

public class MapTabModel {

	public List<Double> summedVisibleMaps;
	public List<Double> interpolatedData;
	
	public MapResultSet mapResults;
	
	public MapTabModel(MapResultSet originalData){
		
		interpolatedData = null;
		
		this.mapResults = originalData;
				
	}
	
	public String mapShortTitle(){ return mapResults.getShortDatasetTitle(null); }
	public String mapLongTitle(){ return mapResults.getDatasetTitle(null); }
	
	
}
