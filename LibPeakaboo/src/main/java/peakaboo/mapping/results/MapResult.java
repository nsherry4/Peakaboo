package peakaboo.mapping.results;



import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.curvefit.peak.transition.TransitionSeries;


/**
 * 
 * This class stores the data associated with a single map of a scan. Each map is associated with a single
 * {@link TransitionSeries}, and can be marked as visible or invisible. When generating or displaying data for
 * more than one TransitionSeries, it may be desirable to use a {@link MapResultSet} instead of managing the
 * MapResult objects manually.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class MapResult implements Cloneable
{

	private Spectrum data;
	public TransitionSeries	transitionSeries;


	public MapResult(TransitionSeries ts, int mapSize)
	{
		this.data = new ISpectrum(mapSize, 0.0f);
		transitionSeries = ts;
	}
	
	protected MapResult(TransitionSeries ts, Spectrum data)
	{
		this.transitionSeries = ts;
		this.data = data;
	}
	
	
	public ReadOnlySpectrum getData(CalibrationProfile profile) {
		return profile.calibrateMap(data, transitionSeries);
	}
	

	@Override
	public MapResult clone()
	{
		return new MapResult(transitionSeries, data);
	}
	

	@Override
	public String toString()
	{
		return transitionSeries.getElement().name() + " (" + transitionSeries.getShell() + ")";
	}

	public int size() {
		return data.size();
	}

	void setData(int index, float intensity) {
		data.set(index, intensity);
	}

}
