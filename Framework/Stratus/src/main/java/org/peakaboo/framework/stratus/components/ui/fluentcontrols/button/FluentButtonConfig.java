package org.peakaboo.framework.stratus.components.ui.fluentcontrols.button;

import javax.swing.border.Border;

import org.peakaboo.framework.stratus.components.ui.fluentcontrols.FluentConfig;

public class FluentButtonConfig extends FluentConfig {
	
	public static enum BorderStyle {
		ALWAYS, ACTIVE, NEVER;
	}

	public FluentButtonLayout layout = null;
	public BorderStyle bordered = BorderStyle.ALWAYS;
	public Border border = null;
	public FluentButtonSize buttonSize = null;
	
}