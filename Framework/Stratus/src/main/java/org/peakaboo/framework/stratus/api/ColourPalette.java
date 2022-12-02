package org.peakaboo.framework.stratus.api;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ColourPalette {
	
	public static record Shade(String name, Color colour) {};
	
	public static class Hue {
		
		private String name;
		private Map<String, Shade> shades = new LinkedHashMap<>();
		
		public Hue(String name) {
			this.name = name;
		}
		
		public List<String> shadeNames() {
			return new ArrayList<>(shades.keySet());
		}
		public void addShade(String name, int argb) {
			addShade(name, new Color(argb, true));
		}
		public void addShade(String name, Color colour) {
			addShade(new Shade(name, colour));
		}
		public void addShade(Shade shade) {
			this.shades.put(shade.name, shade);
		}
		public Shade getShade(String shadeName) {
			return shades.getOrDefault(shadeName, null);
		}
		public Color getColour(String shadeName) {
			return getShade(shadeName).colour;
		}
	};
	
	
	private Map<String, Hue> hues;
	
	public ColourPalette() {
		this.hues = new LinkedHashMap<>(); 
	}
	
	
	public Hue addHue(String name) {
		this.hues.put(name, new Hue(name));
		return getHue(name);
	}
	public Hue addHue(Hue hue) {
		this.hues.put(hue.name, hue);
		return getHue(hue.name);
	}
	
	
	public void addShade(String hueName, String shadeName, Color colour) {
		this.addShade(hueName, new Shade(shadeName, colour));
	}
	public void addShade(String hueName, Shade shade) {
		hues.get(hueName).addShade(shade);
	}
	
	
	public Color getColour(String hueName, String shadeName) {
		Hue hue = hues.getOrDefault(hueName, null);
		if (hue == null) { return null; }
		return hue.getColour(shadeName);
	}
	
	public Hue getHue(String hueName) {
		return hues.get(hueName);
	}
	
	public Map<String, Shade> getShades(String shadeName) {
		Map<String, Shade> map = new LinkedHashMap<>();
		for (var hue : hues.values()) {
			var shade = hue.getShade(shadeName);
			if (shade != null) {
				map.put(hue.name, shade);
			}
		}
		return map;
	}
	public Map<String, Color> getShadeColours(String shadeName) {
		Map<String, Color> map = new LinkedHashMap<>();
		for (var hue : hues.values()) {
			var shade = hue.getShade(shadeName);
			if (shade != null) {
				map.put(hue.name, shade.colour);
			}
		}
		return map;
	}
	
	public List<Hue> allHues() {
		return new ArrayList<>(hues.values());
	}
	
	public List<String> hueNames() {
		return new ArrayList<>(hues.keySet());
	}
	

	
	
}
