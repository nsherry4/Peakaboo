package org.peakaboo.framework.stratus.components.ui.fluentcontrols;

import javax.swing.JLabel;
import javax.swing.border.Border;

import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;

public class FluentLabel extends JLabel implements FluentAPI<FluentLabel, FluentConfig> {

	private FluentConfig config = new FluentConfig();
	
	@Override
	public FluentLabel getSelf() {
		return this;
	}

	@Override
	public FluentConfig getComponentConfig() {
		return config;
	}

	public FluentLabel withBorder(Border border) {
		this.setBorder(border);
		return this;
	}
	
	@Override
	public void makeWidget() {
		
		this.setText(config.text);
		this.setToolTipText(config.tooltip == null ? null : StratusText.lineWrapHTML(this, config.tooltip, 300));
		this.setIcon(IconFactory.getImageIcon(config));
		
	}

	protected FluentButtonSize guessButtonSize(FluentButtonLayout mode) {
		if (mode == FluentButtonLayout.IMAGE) {
			return FluentButtonSize.COMPACT;
		}
		return FluentButtonSize.LARGE;
	}
	
}
