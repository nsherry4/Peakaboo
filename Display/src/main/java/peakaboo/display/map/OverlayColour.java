package peakaboo.display.map;

import java.awt.Color;

	public enum OverlayColour {
		
		RED {
			@Override
			public Color toColor(){return Color.decode("#cc0000");}
			@Override
			public String toString(){return "Red";}
		},
		GREEN {
			@Override
			public Color toColor(){return Color.decode("#73d216");}
			@Override
			public String toString(){return "Green";}
		},
		BLUE {
			@Override
			public Color toColor(){return Color.decode("#3465a4");}
			@Override
			public String toString(){return "Blue";}
		},
		YELLOW {
			@Override
			public Color toColor(){return Color.decode("#edd400");}
			@Override
			public String toString(){return "Yellow";}
		};
		
		
		public Color toColor()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	