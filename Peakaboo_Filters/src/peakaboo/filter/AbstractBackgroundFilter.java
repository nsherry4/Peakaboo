package peakaboo.filter;

import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public abstract class AbstractBackgroundFilter extends AbstractFilter
{

	private final int PERCENT 		= getNextParameterIndex();
	private final int PREVIEW 		= getNextParameterIndex();
	private final int STARTINDEX 	= getNextParameterIndex();
	private final int STOPINDEX		= getNextParameterIndex();
	private final int PARTIALFILTER = getNextParameterIndex();
	private final int SEP1 			= getNextParameterIndex();
	private final int SEP2 			= getNextParameterIndex();

	
	public AbstractBackgroundFilter()
	{
		
		
		
		addParameter(PERCENT, new Parameter(ValueType.INTEGER, "Percent to Remove", 90));
		addParameter(PREVIEW, new Parameter(ValueType.BOOLEAN, "Preview Only", false));
		
		addParameter(SEP1, new Parameter(ValueType.SEPARATOR, null, null));
		
		addParameter(STOPINDEX, new Parameter(ValueType.INTEGER, "Stop Index", 0));
		addParameter(STARTINDEX, new Parameter(ValueType.INTEGER, "Start Index", 0));
		addParameter(PARTIALFILTER, new Parameter(ValueType.BOOLEAN, "Apply to Subset", false));
		
		addParameter(SEP2, new Parameter(ValueType.SEPARATOR, null, null));
		
		
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
