package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import org.peakaboo.framework.cyclops.visualization.Surface.Dash;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.PlotPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * Draws horizontal grid lines based on a TickFormatter.
 */
public class GridlinePainter extends PlotPainter {

	public static enum Orientation {
		VERTICAL,
		HORIZONTAL
	}
	public static record Config(
			Orientation orientation, 
			TickFormatter tick,
			PaletteColour major, 
			PaletteColour minor,
			Dash dash
		) {
		public Config(Orientation orientation, TickFormatter tick) {
			this(orientation, tick, new PaletteColour(0x28000000), new PaletteColour(0x10000000), null);
		}
		public Config(Orientation orientation, TickFormatter tick, Dash dash) {
			this(orientation, tick, new PaletteColour(0x28000000), new PaletteColour(0x10000000), dash);
		}
	};
	
	public Config config;
	public GridlinePainter(Config config) {
		this.config = config;
	}

	@Override
	public void drawElement(PainterData p) {

		p.context.save();

		// Set the line dash
		p.context.setDashedLine(config.dash);
		
		if (config.orientation == Orientation.HORIZONTAL) {
			for (var mark : config.tick.getTickMarks(p, p.plotSize.y, true)) {
				var yPos = (1f - mark.position()) * p.plotSize.y;
				if (mark.minor()) {
					p.context.setSource(config.minor);
				} else {
					p.context.setSource(config.major);
				}
				TickMarkAxisPainter.drawTickLine(p.context, 0, yPos, p.plotSize.x, yPos);
			}
		} else {
			for (var mark : config.tick.getTickMarks(p, p.plotSize.x, true)) {
				var xPos = mark.position() * p.plotSize.x;
				if (mark.minor()) {
					p.context.setSource(config.minor);
				} else {
					p.context.setSource(config.major);
				}
				TickMarkAxisPainter.drawTickLine(p.context, xPos, 0, xPos, p.plotSize.y);
			}
		}
		
		// Unset the line dash manually. The line stroke is not included in the save/restore process, which
		// is too big of an issue for me to get into right now.
		p.context.setDashedLine(null);


		p.context.restore();

	}

}
