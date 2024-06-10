package org.peakaboo.framework.cyclops.visualization.palette;

import java.util.List;
import java.util.Optional;

import org.peakaboo.framework.cyclops.visualization.palette.Gradient.Stop;

public class Gradients {

	private Gradients() {};
	
	
	public static final Gradient MONOCHROME = new Gradient("Monochrome",
			new Stop(0xff000000, 0f),
			new Stop(0xffffffff, 1f)
		);
	
	public static final Gradient INV_MONOCHROME = new Gradient("Inverted Monochrome",
			new Stop(0xffffffff, 0f),
			new Stop(0xff000000, 1f)
		);
	
	
	public static final Gradient SPECTRUM = new Gradient("Spectrum",
			new Stop(0xff10154D, 0f),
			new Stop(0xff0D47A1, 0.235f),
			new Stop(0xff388E3C, 0.392f),
			new Stop(0xffFBC02D, 0.607f),
			new Stop(0xffEF6C00, 0.764f),
			new Stop(0xffB71C1C, 1f)
		);
	
	
	public static final Gradient NAVIA = new Gradient("Navia",
			new Stop(0xff0b1627, 0f),
			new Stop(0xff1a2b3f, 0.11f),
			new Stop(0xff19598c, 0.22f),
			new Stop(0xff29728e, 0.33f),
			new Stop(0xff3a8285, 0.44f),
			new Stop(0xff4b9379, 0.55f),
			new Stop(0xff66aa6a, 0.66f),
			new Stop(0xff98ca6e, 0.77f),
			new Stop(0xffd9e5a6, 0.88f),
			new Stop(0xfffcf5d9, 1f)
		);


	public static final Gradient LAJOLLA = new Gradient("Lajolla", 
			new Stop(0xff1f1e0f, 0f),
			new Stop(0xff332312, 0.11f), 
			new Stop(0xff5b2f22, 0.22f), 
			new Stop(0xff91403c, 0.33f), 
			new Stop(0xffc94e4a, 0.44f),
			new Stop(0xffe1714d, 0.55f), 
			new Stop(0xffe9924f, 0.66f), 
			new Stop(0xffefb553, 0.77f), 
			new Stop(0xfff8de7a, 0.88f),
			new Stop(0xfffcf9cb, 1f)
		);

	public static final Gradient OSLO = new Gradient("Oslo", 
			new Stop(0xff080706, 0f),
			new Stop(0xff101928, 0.11f), 
			new Stop(0xff143250, 0.22f), 
			new Stop(0xff1f4c7b, 0.33f), 
			new Stop(0xff3968a7, 0.44f),
			new Stop(0xff688ac4, 0.55f), 
			new Stop(0xff8a9fc8, 0.66f), 
			new Stop(0xffabb6c9, 0.77f), 
			new Stop(0xffd4d6da, 0.88f),
			new Stop(0xfffefefe, 1f)
		);
		
	public static final Gradient IRON = new Gradient("Iron",
			new Stop(0xff020f15, 0f),
			new Stop(0xff26036c, 0.1f),
			new Stop(0xff66059f, 0.2f),
			new Stop(0xffa60b9c, 0.3f),
			new Stop(0xffcd2972, 0.4f),
			new Stop(0xffee4e3d, 0.5f),
			new Stop(0xfffa7c0f, 0.6f),
			new Stop(0xfff6aa00, 0.7f),
			new Stop(0xfffdcc06, 0.8f),
			new Stop(0xfffeee5c, 0.9f),
			new Stop(0xfff0fff0, 1f)
		);
	
	
	public static final Gradient GEORGIA = new Gradient("Georgia",
			new Stop(0xffFFFFFF, 0f),
			new Stop(0xffE6B350, 0.2f),
			new Stop(0xffF27C55, 0.4f),
			new Stop(0xffD94C58, 0.6f),
			new Stop(0xff993D6B, 0.8f),
			new Stop(0xff521C5E, 1f)
		);
	
	
	public static final Gradient RATIO_THERMAL = new Gradient("Ratio Thermal",
			new Stop(0xff1485CC, 0f),
			new Stop(0xff000000, 0.5f),
			new Stop(0xffff0000, 1f)
		);
	
	
	public static final Gradient RATIO_MONOCHROME = new Gradient("Ratio Monochrome",
			new Stop(0xff000000, 0f),
			new Stop(0xffffffff, 1f)
		);
		

	public static final Gradient DEFAULT = SPECTRUM;
	
	public static Optional<Gradient> forName(String colourPalette) {
		var gs = List.of(MONOCHROME, INV_MONOCHROME, SPECTRUM, NAVIA, LAJOLLA, OSLO, IRON, GEORGIA);
		for (var g : gs) {
			if (g.getName().equalsIgnoreCase(colourPalette)) {
				return Optional.of(g);
			}
		}
		return Optional.empty();
	}
	
	
}
