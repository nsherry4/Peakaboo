package peakaboo.filter.plugins.noise;



import JSci.maths.polynomials.RealPolynomial;
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
		Parameter<?> sep = new Parameter<>(null, new SeparatorStyle(), 0);
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
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
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
		return SavitskyGolayFilter(
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


	/**
	 * Savitsky-Golay filter is like a moving average, but with higher order polynomials. <br>
	 * <br>
	 * Regular moving average can be seen as calculating a line which is fitted to the data points in
	 * the moving average window, and then taking the value of the line at the centre-point (where the data
	 * point being averaged is)<br>
	 * <br>
	 * This routine fits a higher order polynomial to the data in the averaging window, and determines the
	 * value of the centre-point just as before.<br>
	 * <br>
	 * This has the advantage of preserving the shapes of peaks much better, because lower points on either
	 * side don't result in the average-line being lowered, but can be represented much more accurately by
	 * using higher order polynomials such as a parabola, which won't truncate the peak in nearly as drastic a
	 * way.
	 * 
	 * @param data the data to be smoothed
	 * @param order the power/order of the polynomial to fit to each section of the  data
	 * @param reach the distance from the centrepoint to the edge of the data being considered in a fitting
	 * @return a Savitsky-Golay smoothed data set.
	 */
	public static Spectrum SavitskyGolayFilter(ReadOnlySpectrum data, int order, int reach, float min, float max)
	{

		
		Spectrum result = new ISpectrum(data.size());

		RealPolynomial soln;

		double[] allDataAsArray = new double[data.size()];
		double[] indexAsArray = new double[reach * 2];
		double[][] dataAsArray = new double[2][reach * 2];
		
		
		for (int i = 0; i < data.size(); i++) {
			allDataAsArray[i] = data.get(i);
		}
		for (int i = 0; i < indexAsArray.length; i++) {
			indexAsArray[i] = i;
		}
		
		


		int subStart, subStop;
		
		boolean needsCustomArray = false;
		int customArraySize;
		for (int i = 0; i < data.size(); i++) {

			if (data.get(i) < min || data.get(i) > max)
			{
				result.set(i, data.get(i));
			}
			else
			{
				// exact same as in last loop
				subStart = i - reach;
				subStop = i + reach + 1;

				if (subStart < 0) 
				{
					subStart = 0;
					needsCustomArray = true;
				}
				if (subStop >= data.size()) 
				{
					subStop = data.size() - 1;
					needsCustomArray = true;
				}

				// pack the data into an array
				if (needsCustomArray)
				{
					customArraySize = subStop - subStart + 1;
					dataAsArray = new double[2][customArraySize];
					System.arraycopy(indexAsArray, 0, dataAsArray[0], 0, subStop - subStart - 1);
					
					
					if (customArraySize == reach*2) needsCustomArray = false;
					
					
				}

				//System.arraycopy(indexAsArray, 0, dataAsArray[0], 0, subStop - subStart - 1);
				System.arraycopy(allDataAsArray, subStart, dataAsArray[1], 0, subStop - subStart - 1);
			
				
				soln = JSci.maths.LinearMath.leastSquaresFit(order, dataAsArray);

				result.set(i, (float)Math.max(soln.map(reach), 0.0));
			}
			
		}


		return result;
	}
	
	
}
