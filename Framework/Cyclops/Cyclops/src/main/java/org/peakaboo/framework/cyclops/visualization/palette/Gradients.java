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
	
	
	public static final Gradient RAINBOW = new Gradient("Rainbow",
			new Stop(0xff10154D, 0f),
			new Stop(0xff0D47A1, 0.235f),
			new Stop(0xff388E3C, 0.392f),
			new Stop(0xffFBC02D, 0.607f),
			new Stop(0xffEF6C00, 0.764f),
			new Stop(0xffB71C1C, 1f)
		);

	public static final Gradient AMBER = new Gradient("Amber",
			new Stop(0xffFFFFFF, 0f),
			new Stop(0xffE6B350, 0.2f),
			new Stop(0xffF27C55, 0.4f),
			new Stop(0xffD94C58, 0.6f),
			new Stop(0xff993D6B, 0.8f),
			new Stop(0xff521C5E, 1f)
	);


	// https://colorcet.com
	public static final Gradient REDHOT = new Gradient(
			"Red Hot",
			"/org/peakaboo/framework/cyclops/visualization/palette/CET-L03.csv"); //3

	// https://colorcet.com
	public static final Gradient GOULDIAN = new Gradient(
			"Gouldian",
			"/org/peakaboo/framework/cyclops/visualization/palette/CET-L20.csv"); //20

	// https://colorcet.com
	public static final Gradient CRANBERRY = new Gradient(
			"Cranberry",
			"/org/peakaboo/framework/cyclops/visualization/palette/CET-L19.csv");
	
	// https://www.fabiocrameri.ch/colourpalettes/
	public static final Gradient NAVIA = new Gradient(
			"Navia",
			"/org/peakaboo/framework/cyclops/visualization/palette/navia.csv");

	// https://www.fabiocrameri.ch/colourpalettes/
	public static final Gradient LIPARI = new Gradient(
			"Lipari",
			"/org/peakaboo/framework/cyclops/visualization/palette/lipari.csv");


	public static final Gradient DEFAULT = RAINBOW;
	
	public static Optional<Gradient> forName(String colourPalette) {
		var gs = List.of(MONOCHROME, INV_MONOCHROME, RAINBOW, GOULDIAN, REDHOT, NAVIA, CRANBERRY, AMBER, LIPARI);
		for (var g : gs) {
			if (g.getName().equalsIgnoreCase(colourPalette)) {
				return Optional.of(g);
			}
		}
		return Optional.empty();
	}
	
	
}
