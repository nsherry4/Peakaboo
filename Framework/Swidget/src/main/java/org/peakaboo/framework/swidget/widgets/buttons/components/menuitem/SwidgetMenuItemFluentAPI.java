package org.peakaboo.framework.swidget.widgets.buttons.components.menuitem;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.peakaboo.framework.swidget.widgets.buttons.components.SwidgetComponentFluentAPI;

public interface SwidgetMenuItemFluentAPI<
		B extends JComponent & SwidgetMenuItemFluentAPI<B, C>, 
		C extends SwidgetMenuItemConfig
	> extends SwidgetComponentFluentAPI<B, C> {
	
	default B withKeyStroke(KeyStroke key, JComponent parent) {
		getComponentConfig().keystroke = key;
		getComponentConfig().keystrokeParent = parent;
		makeWidget();
		return getSelf();
	}
	
	default B withMnemonic(Integer mnemonic) {
		getComponentConfig().mnemonic = mnemonic;
		makeWidget();
		return getSelf();
	}

}
