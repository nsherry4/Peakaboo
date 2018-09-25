package peakaboo.controller.mapper.settings;

public enum RatioColour
{

	RED {
		
		@Override
		public int toARGB()
		{
			return OverlayColour.RED.toARGB();
		}
	},
	BLUE {
		
		@Override
		public int toARGB()
		{
			return OverlayColour.BLUE.toARGB();
		}
	};


	public int toARGB()
	{
		return 0;
	}

}
