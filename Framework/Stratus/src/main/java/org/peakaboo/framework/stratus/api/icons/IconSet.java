package org.peakaboo.framework.stratus.api.icons;

import java.awt.Color;

import javax.swing.ImageIcon;

public interface IconSet {

	public String name();
	public String path();
	
	
	default String toIconName() {
		return this.name().replace("_", "-").toLowerCase();
	}
	
	default ImageIcon toImageIcon(IconSize size) {
		return IconFactory.getImageIcon(path(), toIconName(), size);
	}
	
	default ImageIcon toImageIcon(IconSize size, Color c) {
		ImageIcon icon = toImageIcon(size);
		return IconFactory.recolour(icon, c);
	}
	
}
