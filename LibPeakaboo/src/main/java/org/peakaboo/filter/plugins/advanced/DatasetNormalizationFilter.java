package org.peakaboo.filter.plugins.advanced;

import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.AbstractSimpleFilter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;

public class DatasetNormalizationFilter extends AbstractSimpleFilter {

	private Parameter<Float> pHeight;
	
	@Override
	public String getFilterName() {
		return "Dataset Normalizer";
	}

	@Override
	public String getFilterDescription() {
		return "The " + getFilterName() + " scales each spectrum's intensity against the entire dataset based on the options selected";
	}

	@Override
	public FilterType getFilterType() {
		return FilterType.ADVANCED;
	}

	@Override
	public void initialize()
	{
		pHeight = new Parameter<>("Normalized Intensity", new RealStyle(), 10f, this::validate);
		addParameter(pHeight);
	}
	
	private boolean validate(Parameter<?> p)
	{
		float height = pHeight.getValue().floatValue();
		if (height < 1) return false;
		if (height > 1000000) return false;
		return true;
	}
	

	@Override
	public boolean canFilterSubset() {
		return false;
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "0.1";
	}

	@Override
	public String pluginUUID() {
		return "16ae4c64-95ae-469e-a584-b9613afd0452";
	}

	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, DataSet dataset) {
		// TODO Auto-generated method stub
		float max = dataset.getAnalysis().maximumIntensity();
		float height = pHeight.getValue();
		float ratio = max / height;
		if (ratio == 0f) return new ISpectrum(data.size());
		return SpectrumCalculations.divideBy(data, ratio);
		
	}

}
