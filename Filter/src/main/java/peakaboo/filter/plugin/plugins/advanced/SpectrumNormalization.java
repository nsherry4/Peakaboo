package peakaboo.filter.plugin.plugins.advanced;

import autodialog.model.Parameter;
import autodialog.model.style.editors.IntegerStyle;
import autodialog.model.style.editors.RealStyle;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.SpectrumCalculations;


public class SpectrumNormalization extends AbstractSimpleFilter
{
	
	private Parameter<Integer> pChannel;
	private Parameter<Float> pHeight;

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize()
	{
		pChannel = new Parameter<>("Channel", new IntegerStyle(), 1, this::validate);
		addParameter(pChannel);
		pHeight = new Parameter<>("Intensity", new RealStyle(), 10f, this::validate);
		addParameter(pHeight);
	}
	
	@Override
	public boolean canFilterSubset()
	{
		return false;
	}

	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{

		int channel = pChannel.getValue()+1;
		float height = pHeight.getValue().floatValue();
		
		if (channel >= data.size()) return data;
		
		float ratio = data.get(channel) / height;
		if (ratio == 0f) return new ISpectrum(data.size());
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

	private boolean validate(Parameter<?> p)
	{
		
		int channel = pChannel.getValue();
		float height = pHeight.getValue().floatValue();
		
		if (channel < 1) return false;
		if (height < 1) return false;
		if (height > 1000000) return false;
		
		return true;
	}

}
