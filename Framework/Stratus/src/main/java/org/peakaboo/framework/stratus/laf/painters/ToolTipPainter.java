package org.peakaboo.framework.stratus.laf.painters;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class ToolTipPainter extends SimpleThemed implements Painter<JComponent> {

	public ToolTipPainter(Theme theme) {
		super(theme);
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		g = Stratus.modernGraphicsSettings(g);
		
		g.setPaint(Color.BLACK);
		//Can't do radius, because sometimes tooltips are heavyweight object and therefore opaque
		g.fillRect(0, 0, width, height);
	}

}
