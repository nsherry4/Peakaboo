package org.peakaboo.mapping.rawmap;



import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;


/**
 * 
 * This class stores the data associated with a single map of a scan. Each map is associated with a single
 * {@link ITransitionSeries}, and can be marked as visible or invisible. When generating or displaying data for
 * more than one TransitionSeries, it may be desirable to use a {@link RawMapSet} instead of managing the
 * MapResult objects manually.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class RawMap implements Cloneable {

	private Spectrum data;
	public ITransitionSeries	transitionSeries;


	public RawMap(ITransitionSeries ts, int mapSize) {
		this.data = new ISpectrum(mapSize, 0.0f);
		transitionSeries = ts;
	}
	
	public RawMap(ITransitionSeries ts, Spectrum data) {
		this.transitionSeries = ts;
		this.data = data;
	}
	
	
	public ReadOnlySpectrum getData(CalibrationProfile profile) {
		return profile.calibrateMap(data, transitionSeries);
	}
	

	@Override
	public RawMap clone() {
		return new RawMap(transitionSeries, data);
	}
	

	@Override
	public String toString() {
		return transitionSeries.getElement().name() + " (" + transitionSeries.getShell() + ")";
	}

	public int size() {
		return data.size();
	}

	void setData(int index, float intensity) {
		data.set(index, intensity);
	}

}
