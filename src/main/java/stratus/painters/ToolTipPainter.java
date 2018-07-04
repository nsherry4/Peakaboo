package stratus.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import stratus.theme.Theme;

public class ToolTipPainter extends SimpleThemed implements Painter<JComponent> {

	public ToolTipPainter(Theme theme) {
		super(theme);
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		//Can't do radius, because sometimes tooltips are heavyweight object and therefore opaque
		Shape area = new RoundRectangle2D.Float(0, 0, width, height, 0, 0);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		g.setPaint(Color.black);
		g.fill(area);
	}

}
