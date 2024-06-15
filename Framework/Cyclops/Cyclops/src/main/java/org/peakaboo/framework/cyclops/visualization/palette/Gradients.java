package org.peakaboo.framework.cyclops.visualization.palette;

import java.util.List;
import java.util.Optional;

import org.peakaboo.framework.cyclops.visualization.palette.Gradient.Stop;

public class Gradients {

	private Gradients() {};
	
	
	public static final Gradient MONOCHROME = new Gradient("Black to White",
			new Stop(0xff000000, 0f),
			new Stop(0xffffffff, 1f)
		);
	
	public static final Gradient INV_MONOCHROME = new Gradient("White to Black",
			new Stop(0xffffffff, 0f),
			new Stop(0xff000000, 1f)
		);
	
	
	public static final Gradient SPECTRUM = new Gradient("Rainbow",
			new Stop(0xff10154D, 0f),
			new Stop(0xff0D47A1, 0.235f),
			new Stop(0xff388E3C, 0.392f),
			new Stop(0xffFBC02D, 0.607f),
			new Stop(0xffEF6C00, 0.764f),
			new Stop(0xffB71C1C, 1f)
		);
	
	
	public static final Gradient REDHOT = new Gradient("Red Hot",
			new Stop(0xff000000, 0.0f),
			new Stop(0xff320000, 0.06f),
			new Stop(0xff4b0100, 0.12f),
			new Stop(0xff630100, 0.19f),
			new Stop(0xff7e0200, 0.25f),
			new Stop(0xff990400, 0.31f),
			new Stop(0xffb50600, 0.37f),
			new Stop(0xffd10a00, 0.44f),
			new Stop(0xffed1500, 0.5f),
			new Stop(0xfffc4100, 0.56f),
			new Stop(0xffff6900, 0.62f),
			new Stop(0xffff8a00, 0.68f),
			new Stop(0xffffa701, 0.75f),
			new Stop(0xffffc104, 0.81f),
			new Stop(0xffffdb0a, 0.87f),
			new Stop(0xfffff324, 0.93f),
			new Stop(0xffffffff, 1.0f)
		);

	public static final Gradient GOULDIAN = new Gradient("Gouldian",
			new Stop(0xff303030, 0.0f),
			new Stop(0xff3c3659, 0.06f),
			new Stop(0xff423d7f, 0.12f),
			new Stop(0xff4345a2, 0.19f),
			new Stop(0xff3f51bc, 0.25f),
			new Stop(0xff3761c9, 0.31f),
			new Stop(0xff2977b7, 0.37f),
			new Stop(0xff258b95, 0.44f),
			new Stop(0xff429a76, 0.5f),
			new Stop(0xff6ca557, 0.56f),
			new Stop(0xff96ad3a, 0.62f),
			new Stop(0xffbeb31d, 0.68f),
			new Stop(0xffe6b713, 0.75f),
			new Stop(0xfff8c214, 0.81f),
			new Stop(0xfffed313, 0.87f),
			new Stop(0xfffee60f, 0.93f),
			new Stop(0xfff9f90a, 1.0f)
		);
	

	public static final Gradient CRANBERRY = new Gradient("Cranberry",
			new Stop(0xffffffff, 0.0f),
			new Stop(0xffebf7fd, 0.06f),
			new Stop(0xffd8eefd, 0.12f),
			new Stop(0xffc8e5fe, 0.19f),
			new Stop(0xffbedaff, 0.25f),
			new Stop(0xffb9cefe, 0.31f),
			new Stop(0xffb9c1fc, 0.37f),
			new Stop(0xffbeb2f6, 0.44f),
			new Stop(0xffc8a2eb, 0.5f),
			new Stop(0xffd291dd, 0.56f),
			new Stop(0xffdd7eca, 0.62f),
			new Stop(0xffe56bb2, 0.68f),
			new Stop(0xffeb5797, 0.75f),
			new Stop(0xffec4279, 0.81f),
			new Stop(0xffe73158, 0.87f),
			new Stop(0xffdd2535, 0.93f),
			new Stop(0xffd0210e, 1.0f)
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
	
	



	public static final Gradient DEFAULT = SPECTRUM;
	
	public static Optional<Gradient> forName(String colourPalette) {
		var gs = List.of(MONOCHROME, INV_MONOCHROME, SPECTRUM, GOULDIAN, REDHOT, CRANBERRY, IRON, GEORGIA);
		for (var g : gs) {
			if (g.getName().equalsIgnoreCase(colourPalette)) {
				return Optional.of(g);
			}
		}
		return Optional.empty();
	}
	
	
}
