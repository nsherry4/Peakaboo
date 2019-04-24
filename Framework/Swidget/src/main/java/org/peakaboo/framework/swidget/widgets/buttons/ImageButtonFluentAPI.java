package org.peakaboo.framework.swidget.widgets.buttons;

import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.border.Border;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

interface ImageButtonFluentAPI<B extends AbstractButton> {

	
	/**
	 * For internal use only
	 */
	ImageButtonConfig getImageButtonConfig();
	
	/**
	 * For internal use only
	 */
	void makeButton();
	
	/**
	 * For internal use only
	 */
	B getSelf();
	
	/**
	 * For internal use only
	 */
	ImageButtonConfigurator getConfigurator();
	
	
	void setForeground(Color c);
	void setBackground(Color c);
	

	
	default B withBordered(boolean bordered) {
		getImageButtonConfig().bordered = bordered;
		makeButton();
		return getSelf();
	}
	

	
	default B withIcon(StockIcon stock) {
		return withIcon(stock, getImageButtonConfig().size);
	}
	
	default B withIcon(StockIcon stock, IconSize size) {
		return withIcon(stock.toIconName(), size);
	}
	
	default B withIcon(String filename) {
		return withIcon(filename, getImageButtonConfig().size);
	}
	
	default B withIcon(String filename, IconSize size) {
		getImageButtonConfig().imagename = filename;
		getImageButtonConfig().size = size;
		makeButton();
		return getSelf();
	}
	
	default B withBorder(Border border) {
		getImageButtonConfig().border = border;
		makeButton();
		return getSelf();
	}
	
	default B withText(String text) {
		getImageButtonConfig().text = text;
		makeButton();
		return getSelf();
	}
	
	default B withTooltip(String tooltip) {
		getImageButtonConfig().tooltip = tooltip;
		makeButton();
		return getSelf();
	}

	
	default B withLayout(ImageButtonLayout layout) {
		getImageButtonConfig().layout = layout;
		makeButton();
		return getSelf();
	}
	
	default B withButtonSize(ImageButtonSize buttonSize) {
		getImageButtonConfig().buttonSize = buttonSize;
		makeButton();
		return getSelf();
	}
	
	default B withAction(Runnable action) {
		getImageButtonConfig().onAction = action;
		return getSelf();
	}
	
	default B withStateDefault() {
		this.setBackground(new Color(0xff1f89d1, true));
		this.setForeground(Color.WHITE);
		return getSelf();
	}
	
	default B withStateCritical() {
		this.setBackground(new Color(0xffF60A24, true));
		this.setForeground(Color.WHITE);
		return getSelf();
	}

	
	
	
}
