package peakaboo.filter.filters.mathematical;


import autodialog.model.Parameter;
import autodialog.view.editors.DoubleEditor;
import peakaboo.filter.AbstractSimpleFilter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class Subtraction extends AbstractSimpleFilter
{

	private Parameter<Double> amount;
	
	@Override
	public void initialize()
	{
		amount = new Parameter<>("Amount to Subtract", new DoubleEditor(), 1.0);
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return SpectrumCalculations.subtractFromList(data, amount.getValue().floatValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " filter subtracts a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Subtract";
	}


	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.MATHEMATICAL;
	}

	@Override
	public boolean validateParameters()
	{
		return true;
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
