package peakaboo.ui.swing.widgets.testing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class Picture extends JPanel{

	public Picture(Color c) {
		setPreferredSize(new Dimension(200, 200));
		Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 30);
		setOpaque(false);
		setBackground(c2);
		
	}

	
}
