package stratus.painters.textfield;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import stratus.Stratus;
import stratus.Stratus.ButtonState;
import stratus.painters.StatefulPainter;
import stratus.theme.Theme;

public class TextFieldBorderPainter extends StatefulPainter {

	protected int margin = 2;
	protected float radius = Stratus.borderRadius;
	protected float[] points = new float[] {0f, 0.25f};
	
	public TextFieldBorderPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		//EMPTY, SEE BACKGROUND PAINTER
	}

}
