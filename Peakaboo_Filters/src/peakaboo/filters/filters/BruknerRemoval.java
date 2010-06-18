package peakaboo.filters.filters;



import peakaboo.calculations.Background;
import peakaboo.calculations.SpectrumCalculations;
import peakaboo.datatypes.Spectrum;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.drawing.plot.painters.SpectrumPainter;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class BruknerRemoval extends AbstractFilter
{

	private final int	WIDTH	= 0;
	private final int	ITERATIONS = 1;
	private final int	PERCENT = 2;
	private final int	PREVIEW	= 3;


	public BruknerRemoval()
	{
		super();
		parameters.add(WIDTH, new Parameter<Integer>(ValueType.INTEGER, "Width of Fitting", 100));
		parameters.add(ITERATIONS, new Parameter<Integer>(ValueType.INTEGER, "Iterations", 10));
		parameters.add(PERCENT, new Parameter<Integer>(ValueType.INTEGER, "Percent to Remove", 90));
		parameters.add(PREVIEW, new Parameter<Boolean>(ValueType.BOOLEAN, "Preview Only", false));
	}


	@Override
	public String getFilterName()
	{
		return "Brukner";
	}



	private Spectrum getBackground(Spectrum data)
	{
	
		int percent = this.<Integer>getParameterValue(PERCENT);
		int windowSize = this.<Integer>getParameterValue(WIDTH);
		int iterations = this.<Integer>getParameterValue(ITERATIONS);
		
		return SpectrumCalculations.multiplyBy(  Background.removeBackgroundBrukner(data, windowSize, iterations), (percent/100.0f));

	}


	@Override
	public FilterType getFilterType()
	{
		return FilterType.BACKGROUND;
	}


	@Override
	public boolean validateParameters()
	{

		int width, iterations, percent;

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		width = this.<Integer>getParameterValue(WIDTH);
		iterations = this.<Integer>getParameterValue(ITERATIONS);
		percent = this.<Integer>getParameterValue(PERCENT);
		
		
		if (width > 400 || width < 10) return false;
		if (iterations > 50 || iterations < 0) return false;
		if (percent > 100 || percent < 0) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " Filter attempts to determine which portion of the signal is background and remove it. It accomplished this by attempting to fit a series of parabolas or higher order single-term curves under the data, with a curve centred at each point, and attempting to make each curve as tall as possible while still staying completely under the spectrum. The union of these curves is calculated and subtracted from the original data.";
	}


	@Override
	public PlotPainter getPainter()
	{
		
		if (!this.<Boolean>getParameterValue(PREVIEW) == true) return null;
		
		
		return new SpectrumPainter(getBackground(previewCache)) {

			@Override
			public void drawElement(PainterData p)
			{
				traceData(p);
				p.context.setSource(0.36f, 0.21f, 0.4f);
				p.context.stroke();

			}
		};

	}



	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		if (!this.<Boolean>getParameterValue(PREVIEW) == true) {

			Spectrum background = getBackground(data);
			return SpectrumCalculations.subtractLists(data, background);
		}

		if (cache) setPreviewCache(data);
		return data;
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}

}
