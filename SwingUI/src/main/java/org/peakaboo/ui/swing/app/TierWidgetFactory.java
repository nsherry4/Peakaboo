package org.peakaboo.ui.swing.app;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToolbarButton;
import org.peakaboo.tier.Tier;
import org.peakaboo.tier.TierUIAction;

public class TierWidgetFactory {

	public static <V, C> JComponent toolbarButton(TierUIAction<V, C> action, V view, C controller) {
		
		FluentToolbarButton component = new FluentToolbarButton(action.name())
				.withIcon(Tier.provider().iconPath(), action.iconname, IconSize.TOOLBAR_SMALL)
				.withTooltip(action.description())
				.withSignificance(true)
				.withAction(() -> action.action.accept(view, controller));
		
		return component;
		
	}
	
}
