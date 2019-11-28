package org.peakaboo.framework.stratus.painters.textfield;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.StatefulPainter;
import org.peakaboo.framework.stratus.theme.Theme;

public class TextFieldBorderPainter extends StatefulPainter {

	protected int margin = 2;
	protected float radius = 0;
	protected float[] points = new float[] {0f, 0.25f};
	
	public TextFieldBorderPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		this.radius = theme.borderRadius();
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		//EMPTY, SEE BACKGROUND PAINTER
	}

}
