package org.peakaboo.framework.swidget.widgets.fluent.button;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.border.Border;

import org.peakaboo.framework.swidget.widgets.fluent.FluentAPI;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonConfig.BORDER_STYLE;

interface FluentButtonAPI<
		B extends JComponent & FluentButtonAPI<B, C>, 
		C extends FluentButtonConfig
	> extends FluentAPI<B, C> {

	

	

	
	/**
	 * For internal use only
	 */
	FluentButtonConfigurator getConfigurator();
	
	
	void setForeground(Color c);
	void setBackground(Color c);
	

	
	default B withBordered(boolean bordered) {
		getComponentConfig().bordered = bordered ? BORDER_STYLE.ALWAYS : BORDER_STYLE.ACTIVE;
		makeWidget();
		return getSelf();
	}
	
	default B withBordered(FluentButtonConfig.BORDER_STYLE borderStyle) {
		getComponentConfig().bordered = borderStyle;
		makeWidget();
		return getSelf();
	}

	
	
	default B withBorder(Border border) {
		getComponentConfig().border = border;
		makeWidget();
		return getSelf();
	}
	
	
	default B withLayout(FluentButtonLayout layout) {
		getComponentConfig().layout = layout;
		makeWidget();
		return getSelf();
	}
	
	default B withButtonSize(FluentButtonSize buttonSize) {
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
