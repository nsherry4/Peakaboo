package peakaboo.filter.filters.advanced;

import peakaboo.filter.filters.AbstractSimpleFilter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;
import autodialog.model.Parameter;
import autodialog.view.editors.DoubleEditor;
import autodialog.view.editors.IntegerEditor;


public class SpectrumNormalization extends AbstractSimpleFilter
{
	
	private Parameter<Integer> pChannel;
	private Parameter<Double> pHeight;

	@Override
	public void initialize()
	{
		pChannel = new Parameter<>("Channel", new IntegerEditor(), 1);
		pHeight = new Parameter<>("Intensity", new DoubleEditor(), 10d);	
	}
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}

	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{

		int channel = pChannel.getValue()+1;
		float height = pHeight.getValue().floatValue();
		
		if (channel >= data.size()) return data;
		
		float ratio = data.get(channel) / height;
		if (ratio == 0f) return new Spectrum(data.size());
		return SpectrumCalculations.divideBy(data, ratio);
		
	}

	@Override
	public String getFilterDescription()
	{
		return "The " + getFilterName() + " scales each spectrum so that the intensity at a given channel is always the same.";
	}

	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Normalizer";
	}

	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.ADVANCED;
	}


	@Override
	public boolean pluginEnabled()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean validateParameters()
	{
		
		int channel = pChannel.getValue();
		float height = pHeight.getValue().floatValue();
		
		if (channel < 1) return false;
		if (height < 1) return false;
		if (height > 1000000) return false;
		
		return true;
	}

}
