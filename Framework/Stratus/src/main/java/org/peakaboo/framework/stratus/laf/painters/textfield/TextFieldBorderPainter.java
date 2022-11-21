package org.peakaboo.framework.stratus.laf.painters.textfield;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.laf.painters.StatefulPainter;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class TextFieldBorderPainter extends StatefulPainter {

	public TextFieldBorderPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		//EMPTY, SEE BACKGROUND PAINTER
	}

}
