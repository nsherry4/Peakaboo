package peakaboo.filter.plugins.noise;

import java.util.HashMap;
import java.util.Map;

import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.BooleanStyle;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import net.sciencestudio.autodialog.model.style.editors.RealStyle;
import net.sciencestudio.autodialog.model.style.editors.SeparatorStyle;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

public class ConvolvingSavitskyGolay extends AbstractSimpleFilter {

	private Parameter<Integer> reach;
	private Parameter<Integer> order;
	private Parameter<Boolean> ignore;
	private Parameter<Float> max;
	

	
	private static Map<String, float[]> coeffLookup = new HashMap<>();
	static {
		coeffLookup.put("2:2", new float[]{17, 12, -3});
		coeffLookup.put("3:2", new float[]{17, 12, -3});
		coeffLookup.put("2:3", new float[]{7, 6, 3, -2});
		coeffLookup.put("3:3", new float[]{7, 6, 3, -2});
		coeffLookup.put("2:4", new float[]{59, 54, 39, 14, -21});
		coeffLookup.put("3:4", new float[]{59, 54, 39, 14, -21});
		
		coeffLookup.put("4:3", new float[]{131, 75, -30, 5});
		coeffLookup.put("5:3", new float[]{131, 75, -30, 5});
		coeffLookup.put("4:4", new float[]{179, 135, 30, -55, 15});
		coeffLookup.put("5:4", new float[]{179, 135, 30, -55, 15});
	}
	
	@Override
	public void initialize()
	{
		
		reach = new Parameter<>("Reach of Polynomial (2n+1)", new IntegerStyle(), 4, this::validate);
		order = new Parameter<>("Polynomial Order", new IntegerStyle(), 3, this::validate);
		Parameter<?> sep = new Parameter<>(null, new SeparatorStyle(), 0);
		ignore = new Parameter<>("Only Smooth Weak Signal", new BooleanStyle(), false, this::validate);
		max = new Parameter<>("Smoothing Cutoff: (counts)", new RealStyle(), 4.0f, this::validate);
		max.setEnabled(false);
		ignore.getValueHook().addListener(b -> {
			max.setEnabled(b);
		});
		
		addParameter(reach, order, sep, ignore, max);
				
	}
	
	private String getFitString() {
		return order.getValue() + ":" + reach.getValue();
	}
	
	private float[] getCoeffs() {
		if (coeffLookup.containsKey(getFitString())) {
			return coeffLookup.get(getFitString());
		} else {
			return null;
		}
	}

	private boolean validate(Parameter<?> p)
	{
	
		//don't validate any combo we don't have fittings for
		if (getCoeffs() == null) {
			return false;
		}
		
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
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data) {
		return FastSavitskyGolayFilter(data, order.getValue(), reach.getValue(), 0f, ignore.getValue() ? max.getValue() : Float.MAX_VALUE);
	}

	@Override
	public String getFilterName() {
		return "Savitsky-Golay";
	}

	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to remove noise by fitting a polynomial to each point p0 and its surrounding points p0-n..p0+n, and then taking the value of the polynomial at point p0. For performance reasons, this filter's parameters are somewhat constrained.";
	}

	@Override
	public FilterType getFilterType() {
		return FilterType.NOISE;
	}


	@Override
	public boolean canFilterSubset() {
		return true;
	}
	

	public Spectrum FastSavitskyGolayFilter(ReadOnlySpectrum data, int order, int reach, float min, float max) {

		float[] coefs = getCoeffs();
		
		Spectrum out = new ISpectrum(data.size());
		
		for (int i = 0; i < data.size(); i++) {
			
			
			if (data.get(i) < min || data.get(i) > max)
			{
				out.set(i, data.get(i));
			}
			else
			{
				float sum = 0;
				float normalize = 0;
				for (int j = -reach; j <= reach; j++) {
					float coef = coefs[Math.abs(j)];
					int di = i+j;
					if (di < 0 || di >= data.size()) continue;
					sum += coef * data.get(di);
					normalize += coef;
				}
				
				out.set(i, sum / normalize);
			}
		}
		
		return out;
		
	}

}
