package org.peakaboo.framework.swidget.widgets.fluent.button;


import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.JToggleButton;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.fluent.menuitem.FluentCheckMenuItem;



public class FluentToggleButton extends JToggleButton implements FluentButtonAPI<FluentToggleButton, FluentButtonConfig>
{

	private FluentButtonConfigurator configurator;

	public FluentToggleButton() {
		init();
		makeWidget();
	}
	
	public FluentToggleButton(String text) {
		config().text = text;
		
		init();
		makeWidget();
	}
	
	public FluentToggleButton(StockIcon icon) {
		config().imagename = icon.toIconName();

		init();
		makeWidget();
	}

	public FluentToggleButton(StockIcon icon, IconSize size) {
		config().imagename = icon.toIconName();
		config().size = size;

		init();
		makeWidget();
	}

	
	public FluentToggleButton(String text, StockIcon icon) {
		config().text = text;
		config().imagename = icon.toIconName();

		init();
		makeWidget();
	}
	
	public FluentToggleButton(String text, String icon) {
		config().text = text;
		config().imagename = icon;

		init();
		makeWidget();
	}
	
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		setButtonBorder();
	}
	
	
	public FluentToggleButton withSelected(boolean selected) {
		setSelected(selected);
		return getSelf();
	}
	
	
	public FluentToggleButton withAction(Consumer<Boolean> action) {
		if (action == null) {
			getComponentConfig().onAction = null;
		} else {
			getComponentConfig().onAction = () -> action.accept(this.isSelected());
		}
		return getSelf();
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
	public FluentToggleButton getSelf() {
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