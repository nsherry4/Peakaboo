package peakaboo.controller.mapper.mapdisplay;

public enum MapDisplayMode {
	COMPOSITE
	{
		@Override
		public String toString() { return "Composite"; }
	},
	OVERLAY
	{
		@Override
		public String toString() { return "Overlay"; }
	},
	RATIO
	{
		@Override
		public String toString() { return "Ratio"; }
	},
}