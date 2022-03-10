package org.peakaboo.display.map.modes.overlay;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

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
	},
	ORANGE {
		@Override
		public int toARGB(){return 0xffFF8000;}
		@Override
		public String toString(){return "Orange";}
	},
	PURPLE {
		@Override
		public int toARGB(){return 0xff9000FF;}
		@Override
		public String toString(){return "Purple";}
	},
	PINK {
		@Override
		public int toARGB(){return 0xffFF0090;}
		@Override
		public String toString(){return "Pink";}
	},
	WHITE {
		@Override
		public int toARGB(){return 0xffFFFFFF;}
		@Override
		public String toString(){return "White";}
	};
	
	
	public int toARGB() {
		return 0;
	}
	
	public PaletteColour toColour() {
		return new PaletteColour(toARGB());
	}
	
}
	
	