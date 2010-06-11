package peakaboo.controller.mapper;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Functional;
import peakaboo.datatypes.functional.stock.Functions;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.mapping.MapResultSet;
import peakaboo.mapping.colours.OverlayColor;

public class SingleMapModel {

	//public List<List<Double>> summedVisibleMaps;
	public List<Pair<TransitionSeries, Spectrum>> resultantData;
	
	private MapResultSet mapResults;
	
	public Map<TransitionSeries, Integer> ratioSide;
	public Map<TransitionSeries, OverlayColor> overlayColour;
	public Map<TransitionSeries, Boolean> visible;
	
	public MapScaleMode mapScaleMode = MapScaleMode.VISIBLE_ELEMENTS;
	
	public MapDisplayMode displayMode;
	
	public SingleMapModel(MapResultSet originalData){
		
		resultantData = null;
		
		this.mapResults = originalData;
		
		displayMode = MapDisplayMode.COMPOSITE;
		
		ratioSide = DataTypeFactory.<TransitionSeries, Integer>map();
		overlayColour = DataTypeFactory.<TransitionSeries, OverlayColor>map();
		visible = DataTypeFactory.<TransitionSeries, Boolean>map();
		
		for (TransitionSeries ts : originalData.getAllTransitionSeries())
		{
			ratioSide.put(ts, 1);
			overlayColour.put(ts, OverlayColor.RED);
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
	
	public Spectrum sumGivenTransitionSeriesMaps(List<TransitionSeries> list)
	{
		return mapResults.sumGivenTransitionSeriesMaps(list);
	}
	
	public Spectrum getMapForTransitionSeries(TransitionSeries ts)
	{
		List<TransitionSeries> tss = DataTypeFactory.<TransitionSeries>list();
		tss.add(ts);
		return mapResults.sumGivenTransitionSeriesMaps(tss);
	}
	
	public Spectrum sumVisibleTransitionSeriesMaps()
	{	
		return mapResults.sumGivenTransitionSeriesMaps(getVisibleTransitionSeries());
	}
	
	public Spectrum sumAllTransitionSeriesMaps()
	{		
		return mapResults.sumGivenTransitionSeriesMaps(visible.keySet());
	}
	
	public String mapShortTitle(){ return getShortDatasetTitle(getVisibleTransitionSeries()); }
	
	public String mapLongTitle(){ return getDatasetTitle(getVisibleTransitionSeries()); }
	

	public String mapShortTitle(List<TransitionSeries> list){ return getShortDatasetTitle(list); }
	
	public String mapLongTitle(List<TransitionSeries> list){ return getDatasetTitle(list); }
	

	private String getDatasetTitle(List<TransitionSeries> list)
	{
		String separator = ", ";
		
		List<String> elementNames = Functional.map(list, new Function1<TransitionSeries, String>() {
			
			public String f(TransitionSeries ts) {
				return ts.toElementString();
			}
		});

		String title = Functional.foldr(elementNames, Functions.concat(", "));
		
		if (title == null) return "-";
		return title;
		
	}
	

	private String getShortDatasetTitle(List<TransitionSeries> list)
	{
		String separator = ", ";
		
		
		List<String> elementNames = Functional.map(list, new Function1<TransitionSeries, String>() {
			
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
	
	
	public List<Pair<TransitionSeries, Spectrum>> getTransitionSeriesForColour(final OverlayColor c)
	{
		return Functional.filter(
				resultantData,
				new Function1<Pair<TransitionSeries, Spectrum>, Boolean>() {

					@Override
					public Boolean f(Pair<TransitionSeries, Spectrum> element)
					{
						return c.equals(overlayColour.get(element.first)) && visible.get(element.first);
					}
				});
	}
	
	public List<TransitionSeries> getTransitionSeriesForRatioSide(final int side)
	{
		return Functional.filter(
				getVisibleTransitionSeries(),
				new Function1<TransitionSeries, Boolean>() {

					@Override
					public Boolean f(TransitionSeries element)
					{
						Integer thisSide = ratioSide.get(element);
						return thisSide == side;
					}
				});
	}
	
	public void discard()
	{
		resultantData.clear();
		ratioSide.clear();
		overlayColour.clear();
		visible.clear();
		
		mapResults = null;
	}
	
}
