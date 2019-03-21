package org.peakaboo.framework.swidget.widgets.buttons;

import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;


//TODO: Can't base this on ImageButton anymore
public class ToolbarImageButton extends JButton implements ImageButtonFluentAPI<ToolbarImageButton>{

	private ToolbarImageButtonConfigurator configurator;
	
	private void firstconfig() {
		config().size = IconSize.TOOLBAR_SMALL;
		config().bordered = false;
	}
	
	public ToolbarImageButton() {
		firstconfig();
		init();
		makeButton();
	}
	
	

	public ToolbarImageButton(StockIcon icon) {
		firstconfig();
		config().imagename = icon.toIconName();
		
		init();
		makeButton();
	}

	public ToolbarImageButton(StockIcon icon, IconSize size) {
		firstconfig();
		config().imagename = icon.toIconName();
		config().size = size;

		init();
		makeButton();
	}

	
	public ToolbarImageButton(String text, StockIcon icon) {
		firstconfig();
		config().text = text;
		config().imagename = icon.toIconName();

		init();
		makeButton();
	}
	
	public ToolbarImageButton(String text, String icon) {
		firstconfig();
		config().text = text;
		config().imagename = icon;

		init();
		makeButton();
	}
	
	
	
	public ToolbarImageButton(String text) {
		firstconfig();
		config().text = text;

		init();
		makeButton();
	}



	
	
	public ToolbarImageButton withSignificance(boolean significant) {
		getConfigurator().isSignificant = significant;
		
		makeButton();
		return this;
	}
	
	private ImageButtonConfig config() {
		return getConfigurator().getConfiguration();
	}
	
	/**
	 * For internal use only
	 */
	@Override
	public ToolbarImageButtonConfigurator getConfigurator() {
		if (configurator == null) {
			configurator = new ToolbarImageButtonConfigurator(this, this, new ImageButtonConfig());
		}
		return configurator;
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
	public ToolbarImageButton getSelf() {
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

class ToolbarImageButtonConfigurator extends ImageButtonConfigurator {
	
	public boolean isSignificant = false;
	
	public ToolbarImageButtonConfigurator(AbstractButton button, ImageButtonFluentAPI<? extends AbstractButton> api, ImageButtonConfig config) {
		super(button, api, config);
	}
	
	@Override
	protected ImageButtonLayout guessLayout() {
		ImageButtonLayout mode = this.isSignificant ? ImageButtonLayout.IMAGE_ON_SIDE : ImageButtonLayout.IMAGE;
		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
		if (config.imagename == null || image.getIconHeight() == -1) {
			mode = ImageButtonLayout.TEXT;
		} else if (config.text == null || "".equals(config.text)) {
			mode = ImageButtonLayout.IMAGE;
		}
		return mode;
	}
	
}
