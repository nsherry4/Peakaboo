package org.peakaboo.framework.stratus.painters.checkbutton;

import java.awt.Graphics2D;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.AbstractButtonPainter;
import org.peakaboo.framework.stratus.theme.Theme;

public class CheckButtonPainter extends AbstractButtonPainter {

	private ButtonPalette palette;
	
	public CheckButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		this.radius /= 1.5f;
		palette = super.makePalette(null);
		palette.border = Stratus.lessTransparent(theme.getWidgetBorderAlpha());
	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		super.paint(g, object, width, height, palette);
	}
	
	@Override
    protected ButtonPalette makePalette(JComponent object) {
		
		
		ButtonPalette custom = new ButtonPalette(palette);
		Theme theme = getTheme();
		
		if (!Stratus.focusedWindow(object)) {
			custom.fill = theme.getControl();
			custom.border = theme.getWidgetBorderAlpha();
		} else if (!isDisabled() && isChecked(object)) {
			custom.fill = theme.getHighlight();
			custom.border = Stratus.darken(custom.fill, theme.borderStrength());
		} else if (isDisabled() && isChecked(object)) {
			custom.fill = theme.getControl();
			custom.border = Stratus.darken(custom.fill, theme.borderStrength());
		} else {
			custom.fill = theme.getRecessedControl();
			custom.border = theme.getWidgetBorderAlpha();
		}
		
		
		
		return custom;
    }
	
    protected boolean hasBorder() {
    	return true;
    }
        
	private boolean isChecked(JComponent object) {
		try {
			return ((JCheckBox)object).isSelected();
		} catch (ClassCastException e) {
			return false;
		}
	}

	
}
