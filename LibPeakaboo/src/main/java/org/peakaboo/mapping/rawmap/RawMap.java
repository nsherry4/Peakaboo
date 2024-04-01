package org.peakaboo.mapping.rawmap;



import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;


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
		this.data = new ArraySpectrum(mapSize, 0.0f);
		transitionSeries = ts;
	}
	
	public RawMap(ITransitionSeries ts, Spectrum data) {
		this.transitionSeries = ts;
		this.data = data;
	}
	
	
	public SpectrumView getData(DetectorProfile profile) {
		return profile.calibrateMap(data, transitionSeries);
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
