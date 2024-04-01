package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;

import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;

public abstract class SpectrumMapPainter extends MapPainter
{

	protected Spectrum data;
	
	protected SpectrumMapPainter(AbstractPalette colourRule, Spectrum data)
	{
		super(colourRule);
		this.data = data;
	}
	
	protected SpectrumMapPainter(List<AbstractPalette> colourRules, Spectrum data)
	{
		super(colourRules);
		this.data = data;
	}
	
	public void setData(Spectrum data)
	{
		this.data = data;
	}

}
