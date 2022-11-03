package org.peakaboo.framework.swidget.widgets.fluent.button;


import java.awt.Dimension;

import javax.swing.JButton;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;


public class FluentButton extends JButton implements FluentButtonAPI<FluentButton, FluentButtonConfig> {
	
	private FluentButtonConfigurator configurator;
	

	public FluentButton() {
		init();
		makeWidget();
	}
	
	public FluentButton(String text) {
		config().text = text;
		
		init();
		makeWidget();
	}

	public FluentButton(StockIcon icon) {
		config().imagename = icon.toIconName();
		
		init();
		makeWidget();
	}

	public FluentButton(StockIcon icon, IconSize size) {
		config().imagename = icon.toIconName();
		config().size = size;

		init();
		makeWidget();
	}
	
	public FluentButton(String text, StockIcon icon) {
		config().text = text;
		config().imagename = icon.toIconName();

		init();
		makeWidget();
	}
	
	public FluentButton(String text, String icon) {
		config().text = text;
		config().imagename = icon;

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
			super.setToolTipText(Swidget.lineWrapHTML(this, text));
		}
	}




}
