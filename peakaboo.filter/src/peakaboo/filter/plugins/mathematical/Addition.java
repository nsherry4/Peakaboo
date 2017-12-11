package peakaboo.filter.plugins.mathematical;

import autodialog.model.Parameter;
import autodialog.view.editors.DoubleEditor;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.Filter;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



public class Addition extends AbstractSimpleFilter
{

	private Parameter<Double> amount;

	@Override
	public void initialize()
	{
		amount = new Parameter<>("Amount to Add", new DoubleEditor(), 1.0);
	}
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		return SpectrumCalculations.subtractFromList(data, 0.0f-amount.getValue().floatValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " filter adds a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Add";
	}


	@Override
	public Filter.FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return Filter.FilterType.MATHEMATICAL;
	}


	@Override
	public boolean validateParameters()
	{
		// TODO Auto-generated method stub
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
