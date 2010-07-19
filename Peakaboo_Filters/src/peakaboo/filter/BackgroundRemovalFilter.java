package peakaboo.filter;

import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public abstract class BackgroundRemovalFilter extends AbstractFilter
{

	protected enum backgroundParams
	{
		PERCENT, PREVIEW
	}
	
	public BackgroundRemovalFilter()
	{
		parameters.put(backgroundParams.PERCENT, new Parameter(ValueType.INTEGER, "Percent to Remove", 90));
		parameters.put(backgroundParams.PREVIEW, new Parameter(ValueType.BOOLEAN, "Preview Only", false));
	}
	
	@Override
	public FilterType getFilterType()
	{
		return FilterType.BACKGROUND;
	}
	
	@Override
	public final boolean validateParameters()
	{

		int percent;

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		percent = getParameter(backgroundParams.PERCENT).intValue();
		
		if (percent > 100 || percent < 0) return false;

		return validateCustomParameters();
		
	}
	
	public abstract boolean validateCustomParameters();
	
	protected abstract Spectrum getBackground(Spectrum data, int percent);
	
	private final Spectrum getBackground(Spectrum data)
	{
		int percent = getParameter(backgroundParams.PERCENT).intValue();
		
		return getBackground(data, percent);
		
	}
	
	@Override
	public final Spectrum filterApplyTo(Spectrum data, boolean cache)
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
