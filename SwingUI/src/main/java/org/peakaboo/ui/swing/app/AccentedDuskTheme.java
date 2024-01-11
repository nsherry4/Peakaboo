package org.peakaboo.ui.swing.app;

import java.awt.Color;

import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.peakaboo.framework.stratus.laf.theme.DuskTheme;

public class AccentedDuskTheme extends DuskTheme {

	public static DualLinkedHashBidiMap<String, Color> accentColours = new DualLinkedHashBidiMap<String, Color>(){{
		
		put("Red", new Color(0xc94939));
		put("Orange", new Color(0xd96221));
		put("Yellow", new Color(0xd5a42a));
		
		put("Olive", new Color(0x829843));
		put("Green", new Color(0x2c8c46));
		put("Teal", new Color(0x3e8889));
		
		put("Blue", new Color(0x3584e4));
		put("Purple", new Color(0x6652b3));

		put("Pink", new Color(0xba5eb1));

	}};
	
	public AccentedDuskTheme(Color accent) {
		super();
		this.highlight = accent;
	}
	
}
