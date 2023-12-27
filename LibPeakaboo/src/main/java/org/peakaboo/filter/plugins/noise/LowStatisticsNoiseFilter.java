package org.peakaboo.filter.plugins.noise;

import java.util.Optional;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class LowStatisticsNoiseFilter extends AbstractFilter {

	Parameter<Integer> pWindowSize;
	Parameter<Float> pMaxSignal, pCentrepointFactor, pMaxSlope;


	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String getFilterUUID() {
		return "c47f0fa9-ce68-4224-b190-2ee452049ee1";
	}
	
	@Override
	// TODO: Technically, the window size should be a multiple of the FWHM here,
	// but we don't have access to that information. Maybe..?
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx) {
		Spectrum out = new ISpectrum(data.size());
		for (int i = 0; i < data.size(); i++) {
			out.set(i, filterChannel(i, data));
		}
		return out;
	}
	
	private float filterChannel(int i, ReadOnlySpectrum data) {
		int window = pWindowSize.getValue();
		float maxSignal = pMaxSignal.getValue();
		float centrepointFactor = pCentrepointFactor.getValue();
		float maxSlope = pMaxSlope.getValue();
		
		while (true) {
			if (window == 0) {
				return data.get(i);
			}
			
			int lWindow = leftWindowSize(window, i);
			int rWindow = rightWindowSize(window, i, data.size());
			
			float lsum = sumWindow(i-lWindow, i-1, data);
			float rsum = sumWindow(i+1, i+rWindow, data);
			float sum = lsum + data.get(i) + rsum;
			
			boolean belowMax = sum < maxSignal;
			boolean othersNotDominating = sum < centrepointFactor * Math.sqrt(data.get(i));
			boolean goodSlope = (1f/maxSlope) <= ((rsum+1f)/(lsum+1f)) && ((rsum+1f)/(lsum+1f)) <= maxSlope; 
			
			if (belowMax || (othersNotDominating && goodSlope)) {
				return sum / (lWindow + 1 + rWindow);
			}
			
			
			window--;
		}
		
	}
	
	
	private float sumWindow(int start, int stop, ReadOnlySpectrum data) {
		float sum = 0;
		for (int i = start; i <= stop; i++) {
			sum += data.get(i);
		}
		return sum;
	}
	
	private int leftWindowSize(int requestedSize, int channel) {
		if (channel < requestedSize) {
			return channel;
		}
		return requestedSize;
	}
	
	private int rightWindowSize(int requestedSize, int channel, int dataWidth) {
		int limit = channel + requestedSize;
		if (limit >= dataWidth) {
			return dataWidth-channel-1;
		}
		return requestedSize;
	}

	@Override
	public String getFilterName() {
		return "Low-Statistics";
	}

	@Override
	public String getFilterDescription() {
		return "Smooths signal per-channel by shrinking a moving-average window until either: <ul><li>The signal in the window is less than Max Signal</li><li>Both of the following are true:<ul><li>The signal in the window is less than Threshold x sqrt(centerpoint)</li><li>The slopes of the left vs. right windows is less than Max Slope</li></ul></li></ul>";
	}

	@Override
	public FilterDescriptor getFilterDescriptor() {
		return FilterDescriptor.SMOOTHING;
	}

	@Override
	public void initialize() {
		pWindowSize = new Parameter<>("Half-Window Size", new IntegerStyle(), 3, this::validate);
		pMaxSignal = new Parameter<>("Max Signal in Window", new RealStyle(), 25f, this::validate);
		pCentrepointFactor = new Parameter<>("Centrepoint Threshold", new RealStyle(), 75f, this::validate);
		pMaxSlope = new Parameter<>("Maximum Slope", new RealStyle(), 1.3f, this::validate);
		
		addParameter(pWindowSize, pMaxSignal, pCentrepointFactor, pMaxSlope);
		
	}
	
	private boolean validate(Object o) {
		if (pWindowSize.getValue() < 0) { return false; }
		if (pWindowSize.getValue() > 10) { return false; }
		
		if (pMaxSignal.getValue() < 1) { return false; }
				
		if (pCentrepointFactor.getValue() < 10) { return false; }
		if (pCentrepointFactor.getValue() > 200) { return false; }
		
		if (pMaxSlope.getValue() < 0.1) { return false; }
		if (pMaxSlope.getValue() > 5.0) { return false; }
		
		return true;
	}
	

	@Override
	public boolean canFilterSubset() {
		return true;
	}

}
