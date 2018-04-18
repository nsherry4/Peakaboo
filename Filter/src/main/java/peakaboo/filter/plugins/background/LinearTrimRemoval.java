package peakaboo.filter.plugins.background;



import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import peakaboo.filter.model.AbstractBackgroundFilter;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class LinearTrimRemoval extends AbstractBackgroundFilter
{

	private Parameter<Integer> width;
	private Parameter<Integer> iterations;


	public LinearTrimRemoval()
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
		iterations = new Parameter<>("Iterations", new IntegerStyle(), 2, this::validate);
		width = new Parameter<>("Width of Fitting", new IntegerStyle(), 100, this::validate);
		
		addParameter(iterations, width);
	}

	@Override
	public String getFilterName()
	{
		return "Linear Trim";
	}


	@Override
	protected ReadOnlySpectrum getBackground(ReadOnlySpectrum data, int percent)
	{

		return SpectrumCalculations.multiplyBy(
				calcBackgroundLinearTrim(data, width.getValue(), iterations.getValue()), (percent/100.0f)
			);		
	}
	

	private boolean validate(Parameter<?> p)
	{
		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		if (width.getValue() > 400 || width.getValue() < 10) return false;
		if (iterations.getValue() > 20 || iterations.getValue() <= 0) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to determine which portion of the signal is background and remove it. It does this by examining all pairs of points which are n channels apart (ie (1, 10), (2, 11) where n = 10). For each pair of points, any signal which exceeds a straight line connecting the two points is removed.";
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
	 * The Linear Trim background removal algorithm works by defining a sequence of line segments between
	 * each pair of data points m point apart. (ie (1,5), (2,6), (3, 7) if m = 4) with the height of each
	 * end of the line segment being the height of the signal at that point. Any values in the source data
	 * between those two points which exceed the height of the line segment are cropped.  
	 * @param scan the source data to calculate the background of
	 * @param lineSize the length of the line segments to be generated (m, from the description above)
	 * @param iterations the number of iterations of this algorithm to run.
	 * @return
	 */
	public static Spectrum calcBackgroundLinearTrim(final ReadOnlySpectrum scan, int lineSize, int iterations)
	{
		
		
		Spectrum result1 = new ISpectrum(scan);
		Spectrum result2 = new ISpectrum(scan.size());
		Spectrum temp;
		Spectrum lineSegment = new ISpectrum(scan.size());
		
		for (int i = 0; i < iterations; i++)
		{
			result2.copy(result1);
			calcBackgroundLinearTrimIteration(result1, result2, lineSegment, lineSize);
			temp = result2;
			result2 = result1;
			result1 = temp;
		}
		
		return result1;
		
	}
	
	/**
	 * Runs an individual iteration of the Linear Trim background removal technique
	 * @param scan the source data to from which we calculate the background
	 * @param target the target to which we write the results
	 * @param lineSegment a {@link Spectrum} in which we can store the height/intensity values of a line segment.
	 * @param lineSize the length of the line segments
	 * @return
	 */
	private static Spectrum calcBackgroundLinearTrimIteration(final Spectrum scan, final Spectrum target, final Spectrum lineSegment, int lineSize)
	{
		int first = -lineSize+1;
		int last = 0;
		
		int boundedFirst, boundedLast;

		while(first < scan.size()-1)
		{
			
			boundedFirst = Math.max(first, 0);
			boundedLast = Math.min(last, scan.size()-1);
			
			linearTrimLinearSegment(lineSegment, scan.get(boundedFirst), scan.get(boundedLast), boundedFirst, boundedLast);
			
			linearTrimCommitLinearSegment(target, lineSegment, boundedFirst, boundedLast);
			
			first++;
			last++;
			
			
		}
		
		
		return target;
		
		
	}
	
	
	/**
	 * Generate a line segment from the given parameters
	 * @param target the {@link Spectrum} that we write the line segment to
	 * @param start the start value (ie height, intensity) for this line segment
	 * @param stop the stop value (ie height, intensity) for this line segment
	 * @param startIndex the index where the line segment begins
	 * @param stopIndex the index where the line segment ends
	 * @return target
	 */
	private static Spectrum linearTrimLinearSegment(Spectrum target, float start, float stop, int startIndex, int stopIndex)
	{
	
		float span = (stopIndex) - startIndex;
		float delta = stop - start;
		float percent;
		
		for (int i = startIndex; i <= stopIndex; i++)
		{
			percent = (i-startIndex) / span;
			target.set(i, start + delta*percent);
		}
		
		return target;
		
	}
	
	/**
	 * Takes a data {@link Spectrum} and a Spectrum containing a line segment, and sets the values in the data spectrum to the values
	 * in the line segment spectrum if the line segment value is lower, but >=0
	 * @param data the Spectrum containing the data
	 * @param lineSegment the Spectrum containing the line segment
	 * @param startIndex the index to start at
	 * @param stopIndex the index to stop at
	 * @return
	 */
	private static Spectrum linearTrimCommitLinearSegment(Spectrum data, Spectrum lineSegment, int startIndex, int stopIndex)
	{
		float datapoint;
		float linepoint;
		
		for (int i = startIndex; i <= stopIndex; i++)
		{
			datapoint = data.get(i);
			linepoint = lineSegment.get(i);
			
			if (datapoint > linepoint && linepoint >= 0)
			{
				data.set(i, linepoint);
			}
		}
		return data;
	}
	
	
}
