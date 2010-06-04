package peakaboo.controller.mapper;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import peakaboo.calculations.functional.Function1;
import peakaboo.calculations.functional.Functional;
import peakaboo.calculations.functional.stock.Functions;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.mapping.MapResultSet;

public class SingleMapModel {

	//public List<List<Double>> summedVisibleMaps;
	public List<Pair<TransitionSeries, List<Double>>> resultantData;
	
	private MapResultSet mapResults;
	
	public Map<TransitionSeries, Integer> ratioSide;
	public Map<TransitionSeries, Color> overlayColour;
	public Map<TransitionSeries, Boolean> visible;
	
	public MapScaleMode mapScaleMode = MapScaleMode.VISIBLE_ELEMENTS;
	
	public MapDisplayMode displayMode;
	
	public SingleMapModel(MapResultSet originalData){
		
		resultantData = null;
		
		this.mapResults = originalData;
		
		displayMode = MapDisplayMode.COMPOSITE;
		
		ratioSide = DataTypeFactory.<TransitionSeries, Integer>map();
		overlayColour = DataTypeFactory.<TransitionSeries, Color>map();
		visible = DataTypeFactory.<TransitionSeries, Boolean>map();
		
		for (TransitionSeries ts : originalData.getAllTransitionSeries())
		{
			ratioSide.put(ts, 1);
			overlayColour.put(ts, Color.black);
			visible.put(ts, true);
		}
		
	}

	
	public List<TransitionSeries> getAllTransitionSeries()
	{
		
		List<TransitionSeries> tsList = Functional.filter(visible.keySet(), Functions.<TransitionSeries>bTrue());
		
		Collections.sort(tsList);
		
		return tsList;
	}
	
	public List<TransitionSeries> getVisibleTransitionSeries()
	{
		return Functional.filter(getAllTransitionSeries(), new Function1<TransitionSeries, Boolean>() {
			
			@Override
			public Boolean f(TransitionSeries element) {
				return visible.get(element);
			}
		});
	}
	
	public List<Double> sumGivenTransitionSeriesMaps(List<TransitionSeries> list)
	{
		return mapResults.sumGivenTransitionSeriesMaps(list);
	}
	
	public List<Double> getMapForTransitionSeries(TransitionSeries ts)
	{
		List<TransitionSeries> tss = DataTypeFactory.<TransitionSeries>list();
		tss.add(ts);
		return mapResults.sumGivenTransitionSeriesMaps(tss);
	}
	
	public List<Double> sumVisibleTransitionSeriesMaps()
	{	
		return mapResults.sumGivenTransitionSeriesMaps(getVisibleTransitionSeries());
	}
	
	public List<Double> sumAllTransitionSeriesMaps()
	{		
		return mapResults.sumGivenTransitionSeriesMaps(visible.keySet());
	}
	
	public String mapShortTitle(){ return getShortDatasetTitle(null); }
	
	public String mapLongTitle(){ return getDatasetTitle(null); }
	
	

	private String getDatasetTitle(String separator)
	{
		if (separator == null) separator = ", ";
		
		List<String> elementNames = Functional.map(getVisibleTransitionSeries(), new Function1<TransitionSeries, String>() {
			
			public String f(TransitionSeries ts) {
				return ts.toElementString();
			}
		});

		String title = Functional.foldr(elementNames, Functions.concat(", "));
		
		if (title == null) return "-";
		return title;
		
	}
	

	private String getShortDatasetTitle(String separator)
	{
		if (separator == null) separator = ", ";
		
		
		List<String> elementNames = Functional.map(getVisibleTransitionSeries(), new Function1<TransitionSeries, String>() {
			
			public String f(TransitionSeries ts) {
				return ts.element.toString();
			}
		});
		
		//trim out the duplicated
		elementNames = Functional.unique(elementNames);

		String title = Functional.foldr(elementNames, Functions.concat(", "));
		
		if (title == null) return "-";
		return title;
		
	}
	
	
	public List<Pair<TransitionSeries, List<Double>>> getTransitionSeriesForColour(final Color c)
	{
		return Functional.filter(
				resultantData,
				new Function1<Pair<TransitionSeries, List<Double>>, Boolean>() {

					@Override
					public Boolean f(Pair<TransitionSeries, List<Double>> element)
					{
						return c.equals(overlayColour.get(element.first));
					}
				});
	}
}
