package org.peakaboo.framework.stratus.laf.painters;

import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;

public class EmptyPainter implements Painter<JComponent> {

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		// NOOP
	}

}
