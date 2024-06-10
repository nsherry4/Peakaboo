package org.peakaboo.framework.cyclops.visualization.palette;

import org.peakaboo.framework.cyclops.visualization.palette.Gradient.Stop;

public class Gradients {

	private Gradients() {};
	
	
	public static final Gradient MONOCHROME = new Gradient(
			new Stop(0xff000000, 0f),
			new Stop(0xffffffff, 1f)
		);
	
	public static final Gradient INV_MONOCHROME = new Gradient(
			new Stop(0xffffffff, 0f),
			new Stop(0xff000000, 1f)
		);
	
	
	public static final Gradient THERMAL = new Gradient(
			new Stop(0xff10154D, 0f),
			new Stop(0xff0D47A1, 0.235f),
			new Stop(0xff388E3C, 0.392f),
			new Stop(0xffFBC02D, 0.607f),
			new Stop(0xffEF6C00, 0.764f),
			new Stop(0xffB71C1C, 1f)
		);
	
	
	public static final Gradient GEORGIA = new Gradient(
			new Stop(0xffFFFFFF, 0f),
			new Stop(0xffE6B350, 0.2f),
			new Stop(0xffF27C55, 0.4f),
			new Stop(0xffD94C58, 0.6f),
			new Stop(0xff993D6B, 0.8f),
			new Stop(0xff521C5E, 1f)
		);
	
	
	public static final Gradient RATIO_THERMAL = new Gradient(
			new Stop(0xff1485CC, 0f),
			new Stop(0xff000000, 0.5f),
			new Stop(0xffff0000, 1f)
		);
	
	
	public static final Gradient RATIO_MONOCHROME = new Gradient(
			new Stop(0xff000000, 0f),
			new Stop(0xffffffff, 1f)
		);
		
	
	// nice
	public static final Gradient THOUGHTFUL = new Gradient(
			new Stop(0xffecd078, 0f),
			new Stop(0xffd95b43, 0.333f),
			new Stop(0xffc02942, 0.666f),
			new Stop(0xff542437, 1f)
		);
	
	
	// nice
	public static final Gradient TERRA = new Gradient(
			new Stop(0xff031634, 0f),
			new Stop(0xff033649, 0.25f),
			new Stop(0xff036564, 0.50f),
			new Stop(0xffcdb380, 0.75f),
			new Stop(0xffe8ddcb, 1f)
		);
	
	
	//good on unsubtracted, okay on subtracted	
	public static final Gradient OLIVE = new Gradient(
			new Stop(0xff300018, 0f),
			new Stop(0xff5a3d31, 0.25f),
			new Stop(0xff837b47, 0.50f),
			new Stop(0xffadb85f, 0.57f),
			new Stop(0xffe5edb8, 1f)
		);
	
	
	public static final Gradient VINTAGE = new Gradient(
			new Stop(0xff8c2318, 0f),
			new Stop(0xff5e8c6a, 0.25f),
			new Stop(0xff88a65e, 0.50f),
			new Stop(0xffbfb35a, 0.75f),
			new Stop(0xfff2c45a, 1f)
		);
	
	
	public static final Gradient GOLDFISH = new Gradient(
			new Stop(0xff69d2e7, 0f),
			new Stop(0xffa7dbd8, 0.304f),
			new Stop(0xffe0e4cc, 0.548f),
			new Stop(0xffd9d6b8, 0.639f),
			new Stop(0xfff39449, 0.700f),
			new Stop(0xfffa6900, 0.882f),
			new Stop(0xffff4000, 1f)
		);
	
	
	// based off of "sugar is three"
	public static final Gradient SUGAR = new Gradient(
			new Stop(0xff2c8b9a, 0f),
			new Stop(0xff6ac3ae, 0.25f),
			new Stop(0xffd9c8a7, 0.44f),
			new Stop(0xfff9cdad, 0.63f),
			new Stop(0xfffc9d9a, 0.82f),
			new Stop(0xfffe4365, 0.94f),
			new Stop(0xfffe264d, 1f)
		);
	
	

	
	public static final Gradient BROWNSUGAR = new Gradient(
			new Stop(0xff490a3d, 0f),
			new Stop(0xffbd1550, 0.25f),
			new Stop(0xffe97f02, 0.50f),
			new Stop(0xfff8ca00, 0.74f),
			new Stop(0xff8a9b0f, 1f)
		);
	
	
	public static final Gradient BLACKBODY = new Gradient(
			new Stop(0xfff82600, 0f),
			new Stop(0xffffb12f, 0.25f),
			new Stop(0xffffe7bf, 0.50f),
			new Stop(0xffdfe6ff, 0.75f),
			new Stop(0xffa6baff, 1f)
		);
	
	
}
