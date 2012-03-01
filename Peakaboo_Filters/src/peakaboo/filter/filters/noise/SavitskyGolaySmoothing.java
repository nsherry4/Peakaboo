package peakaboo.filter.filters.noise;



import bolt.plugin.Plugin;
import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Savitsky-Golay Smoothing functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

@Plugin
public final class SavitskyGolaySmoothing extends AbstractSimpleFilter
{

	private final int	REACH	= getNextParameterIndex();
	private final int	ORDER	= getNextParameterIndex();
	private final int	IGNORE	= getNextParameterIndex();
	private final int	MAX		= getNextParameterIndex();
	private final int	SEP 	= getNextParameterIndex();


	public SavitskyGolaySmoothing()
	{

		super();

	}
	
	
	@Override
	public void initialize()
	{
		addParameter(REACH, new Parameter(ValueType.INTEGER, "Reach of Polynomial (2n+1)", 7));
		addParameter(ORDER, new Parameter(ValueType.INTEGER, "Polynomial Order", 5));
		addParameter(SEP, new Parameter(ValueType.SEPARATOR, null, null));
		addParameter(IGNORE, new Parameter(ValueType.BOOLEAN, "Only Smooth Weak Signal", false));
		addParameter(MAX, new Parameter(ValueType.REAL, "Smoothing Cutoff: (counts)", 4.0));
		
		getParameter(MAX).enabled = false;		
	}
	
	@Override
	public String getFilterName()
	{

		return "Savitsky-Golay";
	}


	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
	}



	@Override
	public boolean validateParameters()
	{

		int reach, order;
		
		// reach shouldn't be any larger than about 30, or else we start to distort the data more than we
		// would like
		reach = getParameter(REACH).intValue();
		if (reach > 30 || reach < 1) return false;

		// a 0th order polynomial isn't going to be terribly useful, and this algorithm starts to get a little
		// wonky
		// when it goes over 10
		order = getParameter(ORDER).intValue();
		if (order > 10 || order < 1) return false;

		// polynomial of order k needs at least k+1 data points in set.
		if (order >= reach * 2 + 1) return false;

		getParameter(MAX).enabled = getParameter(IGNORE).boolValue();
		
		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to remove noise by fitting a polynomial to each point p0 and its surrounding points p0-n..p0+n, and then taking the value of the polynomial at point p0. A moving average may be considered a special case of this filter with a polynomial of order 1.";
	}


	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return Noise.SavitskyGolayFilter(
			data, 
			getParameter(ORDER).intValue(), 
			getParameter(REACH).intValue(),
			0.0f,
			(getParameter(IGNORE).boolValue()) ? getParameter(MAX).realValue() : Float.MAX_VALUE
			
		);
	}

	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	@Override
	public boolean canFilterSubset()
	{
		return true;
	}

	@Override
	public boolean showSaveLoad()
	{
		return false;
	}
}
