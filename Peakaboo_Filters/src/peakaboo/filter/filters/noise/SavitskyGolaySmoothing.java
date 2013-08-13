package peakaboo.filter.filters.noise;



import javax.swing.JSeparator;

import autodialog.model.Parameter;
import autodialog.view.editors.BooleanEditor;
import autodialog.view.editors.DummyEditor;
import autodialog.view.editors.IntegerEditor;
import autodialog.view.editors.DoubleEditor;
import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import scitypes.Spectrum;

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
	private Parameter<Double> max;


	public SavitskyGolaySmoothing()
	{

		super();

	}
	
	
	@Override
	public void initialize()
	{
		reach = new Parameter<>("Reach of Polynomial (2n+1)", new IntegerEditor(), 7);
		order = new Parameter<>("Polynomial Order", new IntegerEditor(), 5);
		Parameter<?> sep = new Parameter<>(null, new DummyEditor(new JSeparator()), null);
		ignore = new Parameter<>("Only Smooth Weak Signal", new BooleanEditor(), false);
		max = new Parameter<>("Smoothing Cutoff: (counts)", new DoubleEditor(), 4.0);
		max.setEnabled(false);
		
		addParameter(reach, order, sep, ignore, max);
				
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

		
		// reach shouldn't be any larger than about 30, or else we start to distort the data more than we
		// would like
		if (reach.getValue() > 30 || reach.getValue() < 1) return false;

		// a 0th order polynomial isn't going to be terribly useful, and this algorithm starts to get a little
		// wonky when it goes over 10
		if (order.getValue() > 10 || order.getValue() < 1) return false;

		// polynomial of order k needs at least k+1 data points in set.
		if (order.getValue() >= reach.getValue() * 2 + 1) return false;

		max.setEnabled(ignore.getValue());
		
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
