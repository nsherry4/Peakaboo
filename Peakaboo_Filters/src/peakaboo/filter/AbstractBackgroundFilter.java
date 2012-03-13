package peakaboo.filter;

import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public abstract class AbstractBackgroundFilter extends AbstractFilter
{

	private int PERCENT;
	private int PREVIEW;
	private int STARTINDEX;
	private int STOPINDEX;
	private int PARTIALFILTER;

	
	public AbstractBackgroundFilter()
	{
		
		
		
		PERCENT = addParameter(new Parameter("Percent to Remove", ValueType.INTEGER, 90));
		PREVIEW = addParameter(new Parameter("Preview Only", ValueType.BOOLEAN, false));
		
		addParameter(new Parameter(null, ValueType.SEPARATOR, null));
		
		STOPINDEX = addParameter(new Parameter("Stop Index", ValueType.INTEGER, 0));
		STARTINDEX = addParameter(new Parameter("Start Index", ValueType.INTEGER, 0));
		PARTIALFILTER = addParameter(new Parameter("Apply to Subset", ValueType.BOOLEAN, false));
		
		addParameter(new Parameter(null, ValueType.SEPARATOR, null));
		
		
		getParameter(STARTINDEX).enabled = false;
		getParameter(STOPINDEX).enabled = false;
		
	}
	
	@Override
	public FilterType getFilterType()
	{
		return FilterType.BACKGROUND;
	}
	
	@Override
	public final boolean validateParameters()
	{

		int percent, start, stop;

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		percent = getParameter(PERCENT).intValue();
		if (percent > 100 || percent < 0) return false;
		
		start = getParameter(STARTINDEX).intValue();
		stop = getParameter(STOPINDEX).intValue();
		if (start < 0) return false;
		if (stop < start) return false;

		
		getParameter(STARTINDEX).enabled = getParameter(PARTIALFILTER).boolValue();
		getParameter(STOPINDEX).enabled = getParameter(PARTIALFILTER).boolValue();
		
		return validateCustomParameters();
		
	}
	
	public abstract boolean validateCustomParameters();
	
	protected abstract Spectrum getBackground(Spectrum data, int percent);
	
	private final Spectrum getBackground(Spectrum data)
	{
		int percent = getParameter(PERCENT).intValue();
		int start = getParameter(STARTINDEX).intValue();
		int stop = getParameter(STOPINDEX).intValue();
		if (stop >= data.size()) stop = data.size() - 1;
		if (start >= data.size()) start = data.size() - 1;
		
		boolean usePartial = getParameter(PARTIALFILTER).boolValue();
		
		if (usePartial) {
			
			Spectrum partial = data.subSpectrum(start, stop);
			Spectrum result = new Spectrum(data.size(), 0f);
			partial = getBackground(partial, percent);
			
			for (int i = 0; i < partial.size(); i++)
			{
				result.set(i + start, partial.get(i));
			}
			
			return result;
			
		} else {
			return getBackground(data, percent);
		}
		
	}
	
	@Override
	protected final Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		if (!getParameter(PREVIEW).boolValue() == true) {

			Spectrum background = getBackground(data);
			return SpectrumCalculations.subtractLists(data, background);
		}

		if (cache) setPreviewCache(data);
		return data;
	}
	
	@Override
	public final PlotPainter getPainter()
	{
		if (!getParameter(PREVIEW).boolValue() == true) return null;

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
	
}
