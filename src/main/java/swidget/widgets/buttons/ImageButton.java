package swidget.widgets.buttons;


import java.awt.Dimension;

import javax.swing.JButton;

import swidget.icons.IconSize;
import swidget.icons.StockIcon;


public class ImageButton extends JButton implements ImageButtonFluentAPI<ImageButton> {
	
	private ImageButtonConfigurator configurator;
	

	public ImageButton() {
		init();
		makeButton();
	}
	
	public ImageButton(String text) {
		config().text = text;
		
		init();
		makeButton();
	}
	
	public ImageButton(StockIcon icon) {
		config().imagename = icon.toIconName();

		init();
		makeButton();
	}

	public ImageButton(StockIcon icon, IconSize size) {
		config().imagename = icon.toIconName();
		config().size = size;

		init();
		makeButton();
	}

	
	public ImageButton(String text, StockIcon icon) {
		config().text = text;
		config().imagename = icon.toIconName();

		init();
		makeButton();
	}
	
	public ImageButton(String text, String icon) {
		config().text = text;
		config().imagename = icon;

		init();
		makeButton();
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
	public ImageButton getSelf() {
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
			super.setToolTipText(ImageButtonConfigurator.getWrappingTooltipText(this, text));
		}
	}




}
