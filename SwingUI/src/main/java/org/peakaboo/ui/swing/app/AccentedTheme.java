package org.peakaboo.ui.swing.app;

import java.awt.Color;

import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.peakaboo.framework.stratus.laf.theme.BrightTheme;

public class AccentedTheme extends BrightTheme {

	public static DualLinkedHashBidiMap<String, Color> accentColours = new DualLinkedHashBidiMap<String, Color>(){{
		put("Blue", PALETTE.getColour("Blue", "3"));
		//put("DarkBlue", new Color(0x4D6787));
		put("Teal", new Color(0x3e8889));
		put("Green", PALETTE.getColour("Green", "5"));
		put("Purple", new Color(0x6250a6));
		put("Pink", new Color(0xba5eb1));
		put("Orange", PALETTE.getColour("Orange", "4"));
		
	}};

	
	public AccentedTheme(Color accent) {
		super();
		this.highlight = accent;
	}
	
}
