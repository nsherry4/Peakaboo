package peakaboo.filter;

import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public abstract class AbstractBackgroundFilter extends AbstractFilter
{

	protected enum backgroundParams
	{
		PERCENT, PREVIEW, STARTINDEX, STOPINDEX, PARTIALFILTER, SEP1, SEP2
	}
	
	public AbstractBackgroundFilter()
	{
		
		
		
		addParameter(backgroundParams.PERCENT, new Parameter(ValueType.INTEGER, "Percent to Remove", 90));
		addParameter(backgroundParams.PREVIEW, new Parameter(ValueType.BOOLEAN, "Preview Only", false));
		
		addParameter(backgroundParams.SEP1, new Parameter(ValueType.SEPARATOR, null, null));
		
		addParameter(backgroundParams.STOPINDEX, new Parameter(ValueType.INTEGER, "Stop Index", 0));
		addParameter(backgroundParams.STARTINDEX, new Parameter(ValueType.INTEGER, "Start Index", 0));
		addParameter(backgroundParams.PARTIALFILTER, new Parameter(ValueType.BOOLEAN, "Apply to Subset", false));
		
		addParameter(backgroundParams.SEP2, new Parameter(ValueType.SEPARATOR, null, null));
		
		
		getParameter(backgroundParams.STARTINDEX).enabled = false;
		getParameter(backgroundParams.STOPINDEX).enabled = false;
		
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
		percent = getParameter(backgroundParams.PERCENT).intValue();
		if (percent > 100 || percent < 0) return false;
		
		start = getParameter(backgroundParams.STARTINDEX).intValue();
		stop = getParameter(backgroundParams.STOPINDEX).intValue();
		if (start < 0) return false;
		if (stop < start) return false;

		
		getParameter(backgroundParams.STARTINDEX).enabled = getParameter(backgroundParams.PARTIALFILTER).boolValue();
		getParameter(backgroundParams.STOPINDEX).enabled = getParameter(backgroundParams.PARTIALFILTER).boolValue();
		
		return validateCustomParameters();
		
	}
	
	public abstract boolean validateCustomParameters();
	
	protected abstract Spectrum getBackground(Spectrum data, int percent);
	
	private final Spectrum getBackground(Spectrum data)
	{
		int percent = getParameter(backgroundParams.PERCENT).intValue();
		int start = getParameter(backgroundParams.STARTINDEX).intValue();
		int stop = getParameter(backgroundParams.STOPINDEX).intValue();
		if (stop >= data.size()) stop = data.size() - 1;
		if (start >= data.size()) start = data.size() - 1;
		
		boolean usePartial = getParameter(backgroundParams.PARTIALFILTER).boolValue();
		
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
		if (!getParameter(backgroundParams.PREVIEW).boolValue() == true) {

			Spectrum background = getBackground(data);
			return SpectrumCalculations.subtractLists(data, background);
		}

		if (cache) setPreviewCache(data);
		return data;
	}
	
	@Override
	public final PlotPainter getPainter()
	{
		if (!getParameter(backgroundParams.PREVIEW).boolValue() == true) return null;

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
