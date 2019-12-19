package org.peakaboo.framework.swidget.widgets.fluent.menuitem;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.peakaboo.framework.swidget.widgets.fluent.FluentAPI;

public interface FluentMenuItemAPI<
		B extends JComponent & FluentMenuItemAPI<B, C>, 
		C extends FluentMenuItemConfig
	> extends FluentAPI<B, C> {
	
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
