package stratus.painters.slider;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import stratus.Stratus;
import stratus.Stratus.ButtonState;
import stratus.painters.ButtonPainter;
import stratus.theme.Theme;

public class SliderThumbPainter extends ButtonPainter {

	public SliderThumbPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		borderColor = Stratus.darken(getTheme().getWidgetBorder(), 0.15f);
	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height) {
		radius = width;
		super.paint(g, object, width, height);		
	}
	
}
