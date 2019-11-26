package org.peakaboo.framework.swidget.widgets.buttons.components.menuitem;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.peakaboo.framework.swidget.widgets.buttons.components.SwidgetComponentConfig;

public class SwidgetMenuItemConfig extends SwidgetComponentConfig {
	public KeyStroke keystroke = null;
	public Integer mnemonic = null;
	public JComponent keystrokeParent = null;
}
