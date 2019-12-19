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
		palette = super.makePalette(null);
		palette.border = Stratus.lessTransparent(theme.getWidgetBorderAlpha());
		palette.fillArray = new Color[] {Stratus.lighten(palette.fillArray[0]), palette.fillArray[0], palette.fillArray[1]};
		palette.fillPoints = new float[] {0, 0.2f, 1f};
		
		live = super.makePalette(null);
		live.border = Stratus.darken(getTheme().getHighlight(), getTheme().borderStrength());
		live.dash = getTheme().getHighlightText();
		live.bevel = Stratus.lighten(getTheme().getHighlight(), 0.1f);
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
			custom.fillTop = theme.getControl();
			custom.fillBottom = theme.getControl();
    		custom.fillArray = new Color[] {custom.fillTop, custom.fillBottom};
    		custom.fillPoints = new float[] {0, 1f};
		} else if (isLive(object)) {
			custom.fillTop = Stratus.lighten(theme.getHighlight(), getTheme().widgetCurve());
			custom.fillBottom = Stratus.darken(theme.getHighlight(), getTheme().widgetCurve());
    		custom.fillArray = new Color[] {custom.fillTop, custom.fillBottom};
    		custom.fillPoints = new float[] {0, 1f};
		}
		
		return custom;
    }
	
	
	@Override
    protected Paint bevelPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
		return isLive(object) ? live.bevel : palette.bevel;
    }
    
	@Override
    protected Paint mainPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
    	return new LinearGradientPaint(0, pad, 0, height-pad, palette.fillPoints, palette.fillArray);
    }
    
	@Override
    protected Paint borderPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
		return isLive(object) ? live.border : palette.border;
    }
    
	@Override
    protected Paint dashPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
		return isLive(object) ? live.dash : palette.dash;
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
