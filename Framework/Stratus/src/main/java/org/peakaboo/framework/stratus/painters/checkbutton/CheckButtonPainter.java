package org.peakaboo.framework.stratus.painters.checkbutton;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.ButtonPainter;
import org.peakaboo.framework.stratus.theme.Theme;

public class CheckButtonPainter extends ButtonPainter {

	private ButtonPalette palette;
	
	public CheckButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		palette = super.makePalette(null);
		palette.border = Stratus.lessTransparent(theme.getWidgetBorderAlpha());
		palette.fillArray = new Color[] {Stratus.lighten(palette.fillArray[0]), palette.fillArray[0], palette.fillArray[1]};
		palette.fillPoints = new float[] {0, 0.2f, 1f};
	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		super.paint(g, object, width, height, palette);
	}
	
	@Override
    protected ButtonPalette makePalette(JComponent object) {
    	return palette;
    }
	
}
