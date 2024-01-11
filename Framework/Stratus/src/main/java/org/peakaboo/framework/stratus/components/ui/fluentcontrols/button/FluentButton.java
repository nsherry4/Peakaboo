package org.peakaboo.framework.stratus.components.ui.fluentcontrols.button;


import java.awt.Dimension;

import javax.swing.JButton;

import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.IconSet;
import org.peakaboo.framework.stratus.api.icons.IconSize;


public class FluentButton extends JButton implements FluentButtonAPI<FluentButton, FluentButtonConfig> {
	
	private FluentButtonConfigurator configurator;
	
	public static enum NotificationDotState {
		OFF,
		PROBLEM,
		WARNING,
		EVENT,
		OK,
	}
	

	public FluentButton() {
		init();
		makeWidget();
	}
	
	public FluentButton(String text) {
		config().text = text;
		
		init();
		makeWidget();
	}

	public FluentButton(IconSet icon) {
		config().imagename = icon.toIconName();
		config().imagepath = icon.path();
		
		init();
		makeWidget();
	}

	public FluentButton(IconSet icon, IconSize size) {
		config().imagename = icon.toIconName();
		config().imagepath = icon.path();
		config().size = size;

		init();
		makeWidget();
	}
	
	public FluentButton(String text, IconSet icon) {
		config().text = text;
		config().imagename = icon.toIconName();
		config().imagepath = icon.path();

		init();
		makeWidget();
	}
	
	public FluentButton(String text, String path, String icon) {
		config().text = text;
		config().imagename = icon;
		config().imagepath = path;

		init();
		makeWidget();
	}
	
	
	
	/**
	 * For internal use only
	 */
	@Override
	public FluentButtonConfigurator getConfigurator() {
		if (configurator == null) {
			configurator = new FluentButtonConfigurator(this, this, new FluentButtonConfig());
		}
		return configurator;
	}
	
	private FluentButtonConfig config() {
		return getConfigurator().getConfiguration();
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
	public FluentButton getSelf() {
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
