package org.peakaboo.framework.stratus.painters.checkbutton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.AbstractButtonPainter;
import org.peakaboo.framework.stratus.painters.AbstractButtonPainter.ButtonPalette;
import org.peakaboo.framework.stratus.theme.Theme;

public class CheckButtonPainter extends AbstractButtonPainter {

	private ButtonPalette palette, live;
	
	public CheckButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		this.radius /= 1.5f;
		palette = super.makePalette(null);
		palette.border = Stratus.lessTransparent(theme.getWidgetBorderAlpha());

		live = super.makePalette(null);
		live.border = Stratus.darken(getTheme().getHighlight(), getTheme().borderStrength());
		live.selection = getTheme().getHighlightText();
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
		} else if (isLive(object)) {
			custom.fill = theme.getHighlight();
		}
		
		return custom;
    }
	
    protected boolean hasBorder() {
    	return true;
    }
    
	    
	@Override
    protected Paint mainPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
    	return palette.fill;
    }
    
	@Override
    protected Paint borderPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
		return isLive(object) ? live.border : palette.border;
    }
    
	@Override
    protected Paint selectionPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
		return isLive(object) ? live.selection : palette.selection;
    }
    
	private boolean isChecked(JComponent object) {
		try {
			return ((JCheckBox)object).isSelected();
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	private boolean isLive(JComponent object) {
		return (!isDisabled() && Stratus.focusedWindow(object) && isChecked(object));
	}
	
}
