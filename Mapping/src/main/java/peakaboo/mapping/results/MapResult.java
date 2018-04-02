package peakaboo.mapping.results;



import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ISpectrum;
import scitypes.Spectrum;

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

	public Spectrum		data;
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

	@Override
	public MapResult clone()
	{
		return new MapResult(transitionSeries, data);
	}
	

	@Override
	public String toString()
	{
		return transitionSeries.element.name() + " (" + transitionSeries.type + ")";
	}

}
