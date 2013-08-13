package peakaboo.filter;

import javax.swing.JSeparator;

import autodialog.model.Parameter;
import autodialog.view.editors.BooleanEditor;
import autodialog.view.editors.DummyEditor;
import autodialog.view.editors.IntegerEditor;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.plot.painters.PlotPainter;
import scidraw.drawing.plot.painters.SpectrumPainter;
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
		
		percent = new Parameter<>("Percent to Remove", new IntegerEditor(), 90);
		preview = new Parameter<>("Preview Only", new BooleanEditor(), Boolean.FALSE);
		
		Parameter<?> sep1 = new Parameter<>(null, new DummyEditor(new JSeparator()), null);
		
		partial = new Parameter<>("Apply to Subset", new BooleanEditor(), Boolean.FALSE);
		stopindex = new Parameter<>("Stop Index", new IntegerEditor(), 0);
		startindex = new Parameter<>("Start Index", new IntegerEditor(), 0);
		
		
		Parameter<?> sep2 = new Parameter<>(null, new DummyEditor(new JSeparator()), null);
		
		
		startindex.setEnabled(false);
		stopindex.setEnabled(false);
		
		addParameter(percent, preview, sep1, partial, stopindex, startindex, sep2);
		
	}
	
	@Override
	public FilterType getFilterType()
	{
		return FilterType.BACKGROUND;
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
	
	protected abstract Spectrum getBackground(Spectrum data, int percent);
	
	private final Spectrum getBackground(Spectrum data)
	{
		int start = startindex.getValue();
		int stop = stopindex.getValue();
		if (stop >= data.size()) stop = data.size() - 1;
		if (start >= data.size()) start = data.size() - 1;
		
		boolean usePartial = partial.getValue();
		
		if (usePartial) {
			
			Spectrum partial = data.subSpectrum(start, stop);
			Spectrum result = new Spectrum(data.size(), 0f);
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
	protected final Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		if (!preview.getValue() == true) {

			Spectrum background = getBackground(data);
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
