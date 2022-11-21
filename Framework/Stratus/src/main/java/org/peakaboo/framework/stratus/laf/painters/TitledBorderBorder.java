package org.peakaboo.framework.stratus.laf.painters;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class TitledBorderBorder extends AbstractBorder implements Themed {

	private float radius = 0f;
	private Theme theme;
	
	public TitledBorderBorder(Theme theme) {
		this.theme = theme;
		this.radius = theme.borderRadius() * 1.5f;
	}
	
	@Override
	public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
		
		Graphics2D g = (Graphics2D) gr;
		
		
		Shape border = new RoundRectangle2D.Float(x, y, width, height, radius, radius);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		g.setColor(StratusColour.lighten(getTheme().getWidgetBorder()));
		g.setStroke(new BasicStroke(1));
		g.draw(border);
	}
	
	@Override
	public Insets getBorderInsets(Component c) {
		return super.getBorderInsets(c);
	}

	@Override
	public Theme getTheme() {
		return theme;
	}

	
}
