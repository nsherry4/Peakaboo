package org.peakaboo.framework.stratus.components.ui.fluentcontrols;

import java.awt.Color;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.icons.IconSet;
import org.peakaboo.framework.stratus.api.icons.IconSize;

public interface FluentAPI<
		B extends JComponent & FluentAPI<B, C>, 
		C extends FluentConfig
	> {
	
	/**
	 * For internal use only
	 */
	B getSelf();
	
	/**
	 * For internal use only
	 */
	C getComponentConfig();
	
	/**
	 * For internal use only
	 */
	void makeWidget();
	
	

	default B withIcon(IconSet stock) {
		return withIcon(stock, getComponentConfig().size);
	}
			
	default B withIcon(IconSet stock, IconSize size) {
		return withIcon(stock.path(), stock.toIconName(), size, null);
	}

	default B withIcon(IconSet stock, IconSize size, Color color) {
		return withIcon(stock.path(), stock.toIconName(), size, color);
	}
	
	default B withIcon(String filepath, String filename) {
		return withIcon(filepath, filename, getComponentConfig().size, null);
	}
	
	default B withIcon(String filepath, String filename, IconSize size) {
		return withIcon(filepath, filename, size, null);
	}
	
	default B withIcon(String filepath, String filename, IconSize size, Color colour) {
		getComponentConfig().imagename = filename;
		getComponentConfig().imagepath = filepath;
		getComponentConfig().imagecolour = colour;
		getComponentConfig().size = size;
		
		makeWidget();
		return getSelf();
	}
	
	default B withAction(Runnable action) {
		getComponentConfig().onAction = action;
		return getSelf();
	}

	default B withText(String text) {
		getComponentConfig().text = text;
		makeWidget();
		return getSelf();
	}
	
	default B withTooltip(String tooltip) {
		getComponentConfig().tooltip = tooltip;
		makeWidget();
		return getSelf();
	}

	
}
