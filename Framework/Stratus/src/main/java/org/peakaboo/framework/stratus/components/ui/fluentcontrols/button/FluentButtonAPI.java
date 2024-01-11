package org.peakaboo.framework.stratus.components.ui.fluentcontrols.button;

import java.awt.Color;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.FluentAPI;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton.NotificationDotState;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig.BorderStyle;

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
		getComponentConfig().bordered = bordered ? BorderStyle.ALWAYS : BorderStyle.ACTIVE;
		makeWidget();
		return getSelf();
	}
	
	default B withBordered(FluentButtonConfig.BorderStyle borderStyle) {
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
	
	
	default B withNotificationDot(NotificationDotState state) {
		Color colour = switch(state) {
			case EVENT -> Stratus.getTheme().getPalette().getColour("Blue", "4");
			case OFF -> null;
			case OK -> Stratus.getTheme().getPalette().getColour("Green", "4");
			case PROBLEM -> Stratus.getTheme().getPalette().getColour("Red", "4");
			case WARNING -> Stratus.getTheme().getPalette().getColour("Orange", "4");
		};
		return withNotificationDot(Optional.ofNullable(colour));
	}

	default B withNotificationDot(Optional<Color> colour) {
		getComponentConfig().notificationDot = colour;
		makeWidget();
		return getSelf();
	}
	
	
	default B withStateDefault() {
		this.setBackground(Stratus.getTheme().getHighlight());
		this.setForeground(Color.WHITE);
		return getSelf();
	}
	
	default B withStateCritical() {
		this.setBackground(new Color(0xffe01b24, true));
		this.setForeground(Color.WHITE);
		return getSelf();
	}

	

	/**
	 * Sets the Action to display the given popup menu
	 * @param menu the menu to display
	 */
	default B withPopupMenuAction(JPopupMenu menu) {
		return this.withPopupMenuAction(menu, false);
	}
	
	/**
	 * Sets the Action to display the given popup menu
	 * @param menu the menu to display
	 */
	default B withPopupMenuAction(JPopupMenu menu, boolean rightAligned) {
		B self = getSelf();
		this.withAction(() -> {
			int x = 0;
			if (rightAligned) { 
				x = (int)(self.getWidth() - menu.getPreferredSize().getWidth()); 
			}
			menu.show(self, x, self.getHeight());
		});
		return self;
	}
	
	
	
}
