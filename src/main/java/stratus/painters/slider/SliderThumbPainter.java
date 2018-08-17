package stratus.painters.slider;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import stratus.Stratus;
import stratus.Stratus.ButtonState;
import stratus.painters.ButtonPainter;
import stratus.painters.ButtonPainter.ButtonPalette;
import stratus.theme.Theme;

public class SliderThumbPainter extends ButtonPainter {

	private ButtonPalette palette;
	
	public SliderThumbPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		palette = super.makePalette(null);
		palette.bevel = new Color(0x00ffffff, true);
		palette.shadow = new Color(0x00ffffff, true);
		palette.border = Stratus.lessTransparent(palette.border);
		palette.fillArray = new Color[] {Stratus.lighten(palette.fillArray[0]), palette.fillArray[0], palette.fillArray[1]};
		palette.fillPoints = new float[] {0, 0.2f, 1f};
	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		radius = width;
		super.paint(g, object, width, height, palette);		
	}
	
	protected ButtonPalette makePalette(JComponent object) {
		return palette;
	}
	
}
