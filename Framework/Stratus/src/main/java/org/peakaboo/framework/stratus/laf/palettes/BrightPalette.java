package org.peakaboo.framework.stratus.laf.palettes;

import org.peakaboo.framework.stratus.api.ColourPalette;

public class BrightPalette extends ColourPalette {

	public BrightPalette() {
		Hue hue;
		
		hue = addHue("Blue");
		hue.addShade("1", 0xff99c1f1);
		hue.addShade("2", 0xff62a0ea);
		hue.addShade("3", 0xff3584e4);
		hue.addShade("4", 0xff1c71d8);
		hue.addShade("5", 0xff1a5fb4);

		hue = addHue("Green");
		hue.addShade("1", 0xff8ff0a4);
		hue.addShade("2", 0xff57e389);
		hue.addShade("3", 0xff33d17a);
		hue.addShade("4", 0xff2ec27e);
		hue.addShade("5", 0xff26a269);

		hue = addHue("Yellow");
		hue.addShade("1", 0xfff9f06b);
		hue.addShade("2", 0xfff8e45c);
		hue.addShade("3", 0xfff6d32d);
		hue.addShade("4", 0xfff5c211);
		hue.addShade("5", 0xffe5a50a);

		hue = addHue("Orange");
		hue.addShade("1", 0xffffbe6f);
		hue.addShade("2", 0xffffa348);
		hue.addShade("3", 0xffff7800);
		hue.addShade("4", 0xffe66100);
		hue.addShade("5", 0xffc64600);

		hue = addHue("Red");
		hue.addShade("1", 0xfff66151);
		hue.addShade("2", 0xffed333b);
		hue.addShade("3", 0xffe01b24);
		hue.addShade("4", 0xffc01c28);
		hue.addShade("5", 0xffa51d2d);

		hue = addHue("Purple");
		hue.addShade("1", 0xffdc8add);
		hue.addShade("2", 0xffc061cb);
		hue.addShade("3", 0xff9141ac);
		hue.addShade("4", 0xff813d9c);
		hue.addShade("5", 0xff613583);

		hue = addHue("Brown");
		hue.addShade("1", 0xffcdab8f);
		hue.addShade("2", 0xffb5835a);
		hue.addShade("3", 0xff986a44);
		hue.addShade("4", 0xff865e3c);
		hue.addShade("5", 0xff63452c);

		hue = addHue("Light");
		hue.addShade("1", 0xffffffff);
		hue.addShade("2", 0xfff6f5f4);
		hue.addShade("3", 0xffdeddda);
		hue.addShade("4", 0xffc0bfbc);
		hue.addShade("5", 0xff9a9996);

		hue = addHue("Dark");
		hue.addShade("1", 0xff77767b);
		hue.addShade("2", 0xff5e5c64);
		hue.addShade("3", 0xff3d3846);
		hue.addShade("4", 0xff241f31);
		hue.addShade("5", 0xff000000);
	}
	
}
