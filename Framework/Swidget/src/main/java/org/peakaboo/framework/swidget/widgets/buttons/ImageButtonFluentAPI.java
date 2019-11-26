package org.peakaboo.framework.swidget.widgets.buttons;

import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.Border;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonConfig.BORDER_STYLE;
import org.peakaboo.framework.swidget.widgets.buttons.components.SwidgetComponentFluentAPI;

interface ImageButtonFluentAPI<
		B extends JComponent & ImageButtonFluentAPI<B, C>, 
		C extends ImageButtonConfig
	> extends SwidgetComponentFluentAPI<B, C> {

	

	

	
	/**
	 * For internal use only
	 */
	ImageButtonConfigurator getConfigurator();
	
	
	void setForeground(Color c);
	void setBackground(Color c);
	

	
	default B withBordered(boolean bordered) {
		getComponentConfig().bordered = bordered ? BORDER_STYLE.ALWAYS : BORDER_STYLE.ACTIVE;
		makeWidget();
		return getSelf();
	}
	
	default B withBordered(ImageButtonConfig.BORDER_STYLE borderStyle) {
		getComponentConfig().bordered = borderStyle;
		makeWidget();
		return getSelf();
	}

	
	
	default B withBorder(Border border) {
		getComponentConfig().border = border;
		makeWidget();
		return getSelf();
	}
	
	
	default B withLayout(ImageButtonLayout layout) {
		getComponentConfig().layout = layout;
		makeWidget();
		return getSelf();
	}
	
	default B withButtonSize(ImageButtonSize buttonSize) {
		getComponentConfig().buttonSize = buttonSize;
		makeWidget();
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
