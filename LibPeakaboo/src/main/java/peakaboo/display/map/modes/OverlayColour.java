package peakaboo.display.map.modes;

import cyclops.visualization.palette.PaletteColour;

public enum OverlayColour {
	
	RED {
		@Override
		public int toARGB(){return 0xffff0000;}
		@Override
		public String toString(){return "Red";}
	},
	GREEN {
		@Override
		public int toARGB(){return 0xff00FF48;}
		@Override
		public String toString(){return "Green";}
	},
	BLUE {
		@Override
		public int toARGB(){return 0xff1485CC;}
		@Override
		public String toString(){return "Blue";}
	},
	YELLOW {
		@Override
		public int toARGB(){return 0xffFFFC19;}
		@Override
		public String toString(){return "Yellow";}
	};
	
	
	public int toARGB()
	{
		return 0;
	}
	
	public PaletteColour toColour() {
		return new PaletteColour(toARGB());
	}
	
}
	
	