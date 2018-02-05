package peakaboo.controller.mapper.mapdisplay;



import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.mapping.colours.OverlayColour;
import scitypes.Coord;
import scitypes.Pair;
import scitypes.Spectrum;



public interface IMapDisplayController
{

	MapScaleMode getMapScaleMode();
	void setMapScaleMode(MapScaleMode mode);


	MapDisplayMode getMapDisplayMode();
	void setMapDisplayMode(MapDisplayMode mode);


	void invalidateInterpolation();


	String getIntensityMeasurementAtPoint(final Coord<Integer> mapCoord);


	Spectrum getCompositeMapData();
	Map<OverlayColour, Spectrum> getOverlayMapData();
	Pair<Spectrum, Spectrum> getRatioMapData();


	/**
	 * Write the results of calling the object-scoped variable/function valueAtCoord.f(coord)
	 * with each coordinate on the map out to the provided outputstream
	 * @param os outputstream to write to
	 */
	void mapAsCSV(OutputStream os);


	Coord<Integer> getDragStart();
	void setDragStart(Coord<Integer> dragStart);
	Coord<Integer> getDragEnd();
	void setDragEnd(Coord<Integer> dragEnd);


	boolean hasBoundingRegion();
	void setHasBoundingRegion(boolean hasBoundingRegion);


	String mapShortTitle(List<TransitionSeries> list);
	String mapLongTitle(List<TransitionSeries> list);
	String mapShortTitle();
	String mapLongTitle();


	List<TransitionSeries> getAllTransitionSeries();
	List<TransitionSeries> getVisibleTransitionSeries();
	List<TransitionSeries> getTransitionSeriesForRatioSide(final int side);
	
	Spectrum sumGivenTransitionSeriesMaps(List<TransitionSeries> list);
	Spectrum getMapForTransitionSeries(TransitionSeries ts);
	Spectrum sumVisibleTransitionSeriesMaps();
	Spectrum sumAllTransitionSeriesMaps();

	
	OverlayColour getOverlayColour(TransitionSeries ts);
	void setOverlayColour(TransitionSeries ts, OverlayColour c);
	Collection<OverlayColour> getOverlayColourValues();
	Set<TransitionSeries> getOverlayColourKeys();
	
	int getRatioSide(TransitionSeries ts);
	void setRatioSide(TransitionSeries ts, int side);
	
	boolean getTransitionSeriesVisibility(TransitionSeries ts);
	void setTransitionSeriesVisibility(TransitionSeries ts, boolean visible);

	
	

}