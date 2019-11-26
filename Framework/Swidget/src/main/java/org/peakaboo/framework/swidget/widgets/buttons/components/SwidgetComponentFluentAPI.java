package org.peakaboo.framework.swidget.widgets.buttons.components;

import javax.swing.JComponent;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

public interface SwidgetComponentFluentAPI<
		B extends JComponent & SwidgetComponentFluentAPI<B, C>, 
		C extends SwidgetComponentConfig
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
	
	

	default B withIcon(StockIcon stock) {
		return withIcon(stock, getComponentConfig().size);
	}
	
	default B withIcon(StockIcon stock, IconSize size) {
		return withIcon(stock.toIconName(), size);
	}
	
	default B withIcon(String filename) {
		return withIcon(filename, getComponentConfig().size);
	}
	
	default B withIcon(String filename, IconSize size) {
		getComponentConfig().imagename = filename;
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
