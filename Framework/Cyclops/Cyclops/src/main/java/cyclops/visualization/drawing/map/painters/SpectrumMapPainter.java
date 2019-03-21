package cyclops.visualization.drawing.map.painters;

import java.util.List;

import cyclops.Spectrum;
import cyclops.visualization.palette.palettes.AbstractPalette;

public abstract class SpectrumMapPainter extends MapPainter
{

	protected Spectrum data;
	
	public SpectrumMapPainter(AbstractPalette colourRule, Spectrum data)
	{
		super(colourRule);
		this.data = data;
	}
	
	public SpectrumMapPainter(List<AbstractPalette> colourRules, Spectrum data)
	{
		super(colourRules);
		this.data = data;
	}
	
	public void setData(Spectrum data)
	{
		this.data = data;
	}

}
