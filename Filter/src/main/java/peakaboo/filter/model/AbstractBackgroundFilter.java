package peakaboo.filter.model;

import autodialog.model.Parameter;
import autodialog.model.style.styles.BooleanStyle;
import autodialog.model.style.styles.IntegerStyle;
import autodialog.model.style.styles.SeparatorStyle;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public abstract class AbstractBackgroundFilter extends AbstractFilter
{

	private Parameter<Integer> percent;
	private Parameter<Boolean> preview;
	private Parameter<Integer> stopindex;
	private Parameter<Integer> startindex;
	private Parameter<Boolean> partial;

	
	public AbstractBackgroundFilter()
	{
		
		percent = new Parameter<>("Percent to Remove", new IntegerStyle(), 90);
		preview = new Parameter<>("Preview Only", new BooleanStyle(), Boolean.FALSE);
		
		Parameter<?> sep1 = new Parameter<>(null, new SeparatorStyle(), null);
		
		partial = new Parameter<>("Apply to Subset", new BooleanStyle(), Boolean.FALSE);
		startindex = new Parameter<>("Start Index", new IntegerStyle(), 0);
		stopindex = new Parameter<>("Stop Index", new IntegerStyle(), 0);
		
		
		Parameter<?> sep2 = new Parameter<>(null, new SeparatorStyle(), null);
		
		
		startindex.setEnabled(false);
		stopindex.setEnabled(false);
		
		addParameter(percent, preview, sep1, partial, startindex, stopindex, sep2);
		
	}
	
	@Override
	public Filter.FilterType getFilterType()
	{
		return Filter.FilterType.BACKGROUND;
	}
	
	@Override
	public final boolean validateParameters()
	{

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		if (percent.getValue() > 100 || percent.getValue() < 0) return false;
		

		if (startindex.getValue() < 0) return false;
		if (stopindex.getValue() < startindex.getValue()) return false;

		
		startindex.setEnabled(partial.getValue());
		stopindex.setEnabled(partial.getValue());
		
		return validateCustomParameters();
		
	}
	
	public abstract boolean validateCustomParameters();
	
	protected abstract ReadOnlySpectrum getBackground(ReadOnlySpectrum data, int percent);
	
	private final ReadOnlySpectrum getBackground(ReadOnlySpectrum data)
	{
		int start = startindex.getValue();
		int stop = stopindex.getValue();
		if (stop >= data.size()) stop = data.size() - 1;
		if (start >= data.size()) start = data.size() - 1;
		
		boolean usePartial = partial.getValue();
		
		if (usePartial) {
			
			ReadOnlySpectrum partial = data.subSpectrum(start, stop);
			Spectrum result = new ISpectrum(data.size(), 0f);
			partial = getBackground(partial, percent.getValue());
			
			for (int i = 0; i < partial.size(); i++)
			{
				result.set(i + start, partial.get(i));
			}
			
			return result;
			
		} else {
			return getBackground(data, percent.getValue());
		}
		
	}
	
	@Override
	protected final ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, boolean cache)
	{
		if (!preview.getValue() == true) {

			ReadOnlySpectrum background = getBackground(data);
			return SpectrumCalculations.subtractLists(data, background);
		}

		if (cache) setPreviewCache(data);
		return data;
	}
	
	@Override
	public final PlotPainter getPainter()
	{
		if (!preview.getValue() == true) return null;

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
