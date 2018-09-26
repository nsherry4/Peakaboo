package peakaboo.display.map.modes;

import cyclops.visualization.palette.PaletteColour;

public enum OverlayColour {
	
	RED {
		@Override
		public int toARGB(){return 0xffcc0000;}
		@Override
		public String toString(){return "Red";}
	},
	GREEN {
		@Override
		public int toARGB(){return 0xff73d216;}
		@Override
		public String toString(){return "Green";}
	},
	BLUE {
		@Override
		public int toARGB(){return 0xff3465a4;}
		@Override
		public String toString(){return "Blue";}
	},
	YELLOW {
		@Override
		public int toARGB(){return 0xffedd400;}
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
	
	