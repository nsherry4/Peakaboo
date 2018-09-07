package peakaboo.controller.mapper.settings;


public enum OverlayColour {
	
	RED {
		@Override
		public int toRGB(){return 0xcc0000;}
		@Override
		public String toString(){return "Red";}
	},
	GREEN {
		@Override
		public int toRGB(){return 0x73d216;}
		@Override
		public String toString(){return "Green";}
	},
	BLUE {
		@Override
		public int toRGB(){return 0x3465a4;}
		@Override
		public String toString(){return "Blue";}
	},
	YELLOW {
		@Override
		public int toRGB(){return 0xedd400;}
		@Override
		public String toString(){return "Yellow";}
	};
	
	
	public int toRGB()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
}
	
	