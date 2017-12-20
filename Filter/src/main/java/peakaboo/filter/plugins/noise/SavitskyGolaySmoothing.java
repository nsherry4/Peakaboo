package peakaboo.filter.plugins.noise;



import autodialog.model.Parameter;
import autodialog.model.style.editors.BooleanStyle;
import autodialog.model.style.editors.IntegerStyle;
import autodialog.model.style.editors.RealStyle;
import autodialog.model.style.editors.SeparatorStyle;
import peakaboo.calculations.Noise;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.Filter;
import scitypes.ReadOnlySpectrum;

/**
 * 
 * This class is a filter exposing the Savitsky-Golay Smoothing functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class SavitskyGolaySmoothing extends AbstractSimpleFilter
{


	private Parameter<Integer> reach;
	private Parameter<Integer> order;
	private Parameter<Boolean> ignore;
	private Parameter<Float> max;


	public SavitskyGolaySmoothing()
	{
		super();
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	
	@Override
	public void initialize()
	{
		reach = new Parameter<>("Reach of Polynomial (2n+1)", new IntegerStyle(), 7, this::validate);
		order = new Parameter<>("Polynomial Order", new IntegerStyle(), 5, this::validate);
		Parameter<?> sep = new Parameter<>(null, new SeparatorStyle(), null);
		ignore = new Parameter<>("Only Smooth Weak Signal", new BooleanStyle(), false, this::validate);
		max = new Parameter<>("Smoothing Cutoff: (counts)", new RealStyle(), 4.0f, this::validate);
		max.setEnabled(false);
		ignore.getValueHook().addListener(b -> {
			max.setEnabled(b);
		});
		
		addParameter(reach, order, sep, ignore, max);
				
	}
	
	@Override
	public String getFilterName()
	{

		return "Savitsky-Golay";
	}


	@Override
	public Filter.FilterType getFilterType()
	{

		return Filter.FilterType.NOISE;
	}



	private boolean validate(Parameter<?> p)
	{
		// reach shouldn't be any larger than about 30, or else we start to distort the data more than we
		// would like
		if (reach.getValue() > 30 || reach.getValue() < 1) return false;

		// a 0th order polynomial isn't going to be terribly useful, and this algorithm starts to get a little
		// wonky when it goes over 10
		if (order.getValue() > 10 || order.getValue() < 1) return false;

		// polynomial of order k needs at least k+1 data points in set.
		if (order.getValue() >= reach.getValue() * 2 + 1) return false;

		
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
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		return Noise.SavitskyGolayFilter(
			data, 
			order.getValue(), 
			reach.getValue(),
			0.0f,
			(ignore.getValue()) ? max.getValue().floatValue() : Float.MAX_VALUE
			
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

}
