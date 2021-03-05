package org.peakaboo.controller.mapper.fitting.modes;

import java.util.Collections;
import java.util.List;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.eventful.IEventful;
import org.peakaboo.mapping.filter.model.AreaMap;

public interface ModeController extends IEventful {

	/**
	 * Returns a list of all transition series included in this map mode
	 */
	List<ITransitionSeries> getAll();

	/**
	 * Returns a list of all visible transition series included in this map mode
	 */
	List<ITransitionSeries> getVisible();

	/**
	 * Set visibility of all {@link ITransitionSeries}
	 */
	void setAllVisible(boolean visible);

	/**
	 * Returns if this TransitionSeries is enabled, but returning false regardless
	 * of setting if the {@link MapFittingController} shows it has not visible false
	 */
	boolean getVisibility(ITransitionSeries ts);

	void setVisibility(ITransitionSeries ts, boolean visible);

	/**
	 * Convenience method for {@link #getData()}.{@link MapModeData#getSize()
	 * getSize()}. Implementations may wish to calculate this separately if getting
	 * the data will take significantly longer than calculating the size, and they
	 * are able to reliably determine the size without calculating the data first.
	 */
	Coord<Integer> getSize();

	String longTitle();

	MapModeData getData();
	
	MappingController getMap();

	/**
	 * Indicates if the current mode is a spatial mode that maps individual pixels
	 * to points on a map
	 */
	boolean isSpatial();

	/**
	 * Indicates if the current mode is able to be translated back to the original
	 * spectra and replotted. This should always be true if
	 * {@link ModeController#isSpatial()} is true.
	 */
	boolean isTranslatableToSpatial();

	/**
	 * Given a list of selected points on this map mode, translate the points back
	 * to spacial indices representing the spectra that generated those points. This
	 * should only ever be called for map modes where isTranslatable is true
	 */
	public List<Integer> translateSelectionToSpatial(List<Integer> points);

	/**
	 * Indicates if the current mode's values can be compared to one another to
	 * guage how closely related they are. This is used for things like selection by
	 * similarity and is basically the same thing as in comparison-based sorting
	 */
	public abstract boolean isComparable();
	
	
	
	default Spectrum sumGivenMaps(List<ITransitionSeries> list) {
		MappingController map = getMap();
		int y = map.getFiltering().getFilteredDataHeight();
		int x = map.getFiltering().getFilteredDataWidth();
		
		//filter the maps
		Iterable<AreaMap> filtereds = map.getFiltering().getAreaMaps(list);
		
		//merge the maps into a single composite map
		if (!filtereds.iterator().hasNext()) {
			return new ISpectrum(x * y);
		}
		return AreaMap.sumSpectrum(filtereds);
	}
	
	default Spectrum sumVisibleMaps() {	
		return sumGivenMaps(getVisible());
	}
	
	default Spectrum sumSingleMap(ITransitionSeries ts) {
		return sumGivenMaps(Collections.singletonList(ts));
	}

}