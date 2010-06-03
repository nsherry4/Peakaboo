package peakaboo.controller.mapper;

public enum MapDisplayMode {
	COMPOSITE
	{
		public String toString() { return "Composite"; }
	},
	OVERLAY
	{
		public String toString() { return "Overlay"; }
	},
	RATIO
	{
		public String toString() { return "Ratio"; }
	},
}