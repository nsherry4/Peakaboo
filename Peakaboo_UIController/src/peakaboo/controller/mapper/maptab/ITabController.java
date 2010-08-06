package peakaboo.controller.mapper.maptab;



import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.mapping.colours.OverlayColour;
import scitypes.Coord;
import scitypes.Spectrum;
import fava.datatypes.Pair;



public interface ITabController
{

	public MapScaleMode getMapScaleMode();
	public void setMapScaleMode(MapScaleMode mode);


	public MapDisplayMode getMapDisplayMode();
	public void setMapDisplayMode(MapDisplayMode mode);


	public void invalidateInterpolation();


	public String getIntensityMeasurementAtPoint(final Coord<Integer> mapCoord);


	public Spectrum getCompositeMapData();
	public Map<OverlayColour, Spectrum> getOverlayMapData();
	public Pair<Spectrum, Spectrum> getRatioMapData();


	/**
	 * Write the results of calling the object-scoped variable/function valueAtCoord.f(coord)
	 * with each coordinate on the map out to the provided outputstream
	 * @param os outputstream to write to
	 */
	public void mapAsCSV(OutputStream os);


	public Coord<Integer> getDragStart();
	public void setDragStart(Coord<Integer> dragStart);
	public Coord<Integer> getDragEnd();
	public void setDragEnd(Coord<Integer> dragEnd);


	public boolean hasBoundingRegion();
	public void setHasBoundingRegion(boolean hasBoundingRegion);


	public String mapShortTitle(List<TransitionSeries> list);
	public String mapLongTitle(List<TransitionSeries> list);
	public String mapShortTitle();
	public String mapLongTitle();


	public List<TransitionSeries> getAllTransitionSeries();
	public List<TransitionSeries> getVisibleTransitionSeries();
	public List<TransitionSeries> getTransitionSeriesForRatioSide(final int side);
	
	public Spectrum sumGivenTransitionSeriesMaps(List<TransitionSeries> list);
	public Spectrum getMapForTransitionSeries(TransitionSeries ts);
	public Spectrum sumVisibleTransitionSeriesMaps();
	public Spectrum sumAllTransitionSeriesMaps();

	
	public OverlayColour getOverlayColour(TransitionSeries ts);
	public void setOverlayColour(TransitionSeries ts, OverlayColour c);
	public Collection<OverlayColour> getOverlayColourValues();
	public Set<TransitionSeries> getOverlayColourKeys();
	
	public int getRatioSide(TransitionSeries ts);
	public void setRatioSide(TransitionSeries ts, int side);
	
	public boolean getTransitionSeriesVisibility(TransitionSeries ts);
	public void setTransitionSeriesVisibility(TransitionSeries ts, boolean visible);

	
	

}