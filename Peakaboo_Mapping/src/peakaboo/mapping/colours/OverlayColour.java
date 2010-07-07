package peakaboo.mapping.colours;

import java.awt.Color;

	public enum OverlayColour {
		
		RED {
			@Override
			public Color toColor(){return new Color(0.64f, 0.00f, 0.00f);}
		},
		GREEN {
			@Override
			public Color toColor(){return new Color(0.35f, 0.69f, 0.03f);}
		},
		BLUE {
			@Override
			public Color toColor(){return new Color(0.07f, 0.16f, 0.30f);}
		};
		
		
		public Color toColor()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	