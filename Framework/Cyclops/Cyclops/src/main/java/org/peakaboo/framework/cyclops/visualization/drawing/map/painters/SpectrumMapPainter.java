package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;

import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.palette.Palette;

public abstract class SpectrumMapPainter extends MapPainter
{

	protected Spectrum data;
	
	protected SpectrumMapPainter(Palette colourRule, Spectrum data)
	{
		super(colourRule);
		this.data = data;
	}

	public void setData(Spectrum data)
	{
		this.data = data;
	}

}
