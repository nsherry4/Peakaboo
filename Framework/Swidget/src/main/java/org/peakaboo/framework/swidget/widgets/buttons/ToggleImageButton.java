package org.peakaboo.framework.swidget.widgets.buttons;


import java.awt.Dimension;

import javax.swing.JToggleButton;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;



public class ToggleImageButton extends JToggleButton implements ImageButtonFluentAPI<ToggleImageButton>
{

	private ImageButtonConfigurator configurator;

	public ToggleImageButton() {
		init();
		makeButton();
	}
	
	public ToggleImageButton(String text) {
		config().text = text;
		
		init();
		makeButton();
	}
	
	public ToggleImageButton(StockIcon icon) {
		config().imagename = icon.toIconName();

		init();
		makeButton();
	}

	public ToggleImageButton(StockIcon icon, IconSize size) {
		config().imagename = icon.toIconName();
		config().size = size;

		init();
		makeButton();
	}

	
	public ToggleImageButton(String text, StockIcon icon) {
		config().text = text;
		config().imagename = icon.toIconName();

		init();
		makeButton();
	}
	
	public ToggleImageButton(String text, String icon) {
		config().text = text;
		config().imagename = icon;

		init();
		makeButton();
	}
	
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		setButtonBorder();
	}
	
	
	
	
	
	

	
	/**
	 * For internal use only
	 */
	@Override
	public ImageButtonConfigurator getConfigurator() {
		if (configurator == null) {
			configurator = new ImageButtonConfigurator(this, this, new ImageButtonConfig());
		}
		return configurator;
	}
	
	private ImageButtonConfig config() {
		return getConfigurator().getConfiguration();
	}
	
	/**
	 * For internal use only
	 */
	@Override
	public void makeButton() {
		getConfigurator().makeButton();
	}
	
	/**
	 * For internal use only
	 */
	@Override
	public ImageButtonConfig getImageButtonConfig() {
		return config();
	}
	
	/**
	 * For internal use only
	 */
	@Override
	public ToggleImageButton getSelf() {
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