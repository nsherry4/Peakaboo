package org.peakaboo.framework.stratus.components.ui.fluentcontrols.button;

import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.IconSet;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig.BORDER_STYLE;


public class FluentToolbarButton extends JButton implements FluentButtonAPI<FluentToolbarButton, FluentButtonConfig>{

	private ToolbarImageButtonConfigurator configurator;
	
	private void firstconfig() {
		config().size = IconSize.TOOLBAR_SMALL;
		config().bordered = BORDER_STYLE.ACTIVE;
	}
	
	public FluentToolbarButton() {
		firstconfig();
		init();
		makeWidget();
	}
	
	

	public FluentToolbarButton(IconSet icon) {
		firstconfig();
		config().imagename = icon.toIconName();
		config().imagepath = icon.path();
		
		init();
		makeWidget();
	}

	public FluentToolbarButton(IconSet icon, IconSize size) {
		firstconfig();
		config().imagename = icon.toIconName();
		config().imagepath = icon.path();
		config().size = size;

		init();
		makeWidget();
	}

	
	public FluentToolbarButton(String text, IconSet icon) {
		firstconfig();
		config().text = text;
		config().imagename = icon.toIconName();
		config().imagepath = icon.path();

		init();
		makeWidget();
	}
	
	public FluentToolbarButton(String text, String path, String icon) {
		firstconfig();
		config().text = text;
		config().imagename = icon;
		config().imagepath = path;

		init();
		makeWidget();
	}
	
	
	
	public FluentToolbarButton(String text) {
		firstconfig();
		config().text = text;

		init();
		makeWidget();
	}



	
	
	public FluentToolbarButton withSignificance(boolean significant) {
		getConfigurator().isSignificant = significant;
		
		makeWidget();
		return this;
	}
	
	private FluentButtonConfig config() {
		return getConfigurator().getConfiguration();
	}
	
	/**
	 * For internal use only
	 */
	@Override
	public ToolbarImageButtonConfigurator getConfigurator() {
		if (configurator == null) {
			configurator = new ToolbarImageButtonConfigurator(this, this, new FluentButtonConfig());
		}
		return configurator;
	}
	
	

	/**
	 * For internal use only
	 */
	@Override
	public void makeWidget() {
		getConfigurator().makeButton();
	}
	
	/**
	 * For internal use only
	 */
	@Override
	public FluentButtonConfig getComponentConfig() {
		return config();
	}
	
	/**
	 * For internal use only
	 */
	@Override
	public FluentToolbarButton getSelf() {
		return this;
	}
	
	private void init() {
		getConfigurator().init(this::setButtonBorder);
	}

	
	void setButtonBorder() {
		setButtonBorder(false);
	}
	
	protected void setButtonBorder(boolean forceBorder) {
		getConfigurator().setButtonBorder(forceBorder);
	}
	
	@Override
	public Dimension getPreferredSize() {
		
		if (super.isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		
		return getConfigurator().getPreferredSize(super.getPreferredSize());
		
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	
	@Override
	public void setToolTipText(String text)	{
		if (text == null) {
			super.setToolTipText(null);
		} else {
			super.setToolTipText(StratusText.lineWrapHTML(this, text));
		}
	}


	
	
}

class ToolbarImageButtonConfigurator extends FluentButtonConfigurator {
	
	public boolean isSignificant = false;
	
	public ToolbarImageButtonConfigurator(AbstractButton button, FluentButtonAPI<? extends AbstractButton, FluentButtonConfig> api, FluentButtonConfig config) {
		super(button, api, config);
	}
	
	@Override
	protected FluentButtonLayout guessLayout() {
		FluentButtonLayout mode = this.isSignificant ? FluentButtonLayout.IMAGE_ON_SIDE : FluentButtonLayout.IMAGE;
		if (config.imagename == null || config.imagepath == null) {
			mode = FluentButtonLayout.TEXT;
		} else if (config.text == null || "".equals(config.text)) {
			mode = FluentButtonLayout.IMAGE;
		}
		return mode;
	}
	
}
