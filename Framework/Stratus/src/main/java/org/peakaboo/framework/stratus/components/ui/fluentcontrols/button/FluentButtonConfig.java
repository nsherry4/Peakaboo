package org.peakaboo.framework.stratus.components.ui.fluentcontrols.button;

import javax.swing.border.Border;

import org.peakaboo.framework.stratus.components.ui.fluentcontrols.FluentConfig;

public class FluentButtonConfig extends FluentConfig {
	
	public static enum BORDER_STYLE {
		ALWAYS, ACTIVE, NEVER;
	}

	public FluentButtonLayout layout = null;
	public BORDER_STYLE bordered = BORDER_STYLE.ALWAYS;
	public Border border = null;
	public FluentButtonSize buttonSize = null;
	
}