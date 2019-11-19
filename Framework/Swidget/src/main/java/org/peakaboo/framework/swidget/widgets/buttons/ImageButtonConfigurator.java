package org.peakaboo.framework.swidget.widgets.buttons;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonConfig.BORDER_STYLE;

public class ImageButtonConfigurator {
	
	protected AbstractButton button;
	protected ImageButtonFluentAPI<? extends AbstractButton> api;
	protected ImageButtonConfig config;
	
	public ImageButtonConfigurator(AbstractButton button, ImageButtonFluentAPI<? extends AbstractButton> api, ImageButtonConfig config) {
		this.button = button;
		this.api = api;
		this.config = config;
	}
	
	void init(Runnable updateBorder) {
		
		button.addActionListener(e -> {
			if (config.onAction != null) {
				config.onAction.run();
			}
		});
		
		button.setOpaque(false);
		
		button.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e)
			{
				updateBorder.run();
			}


			public void mousePressed(MouseEvent e)
			{
				updateBorder.run();
			}


			public void mouseExited(MouseEvent e)
			{
				updateBorder.run();
			}


			public void mouseEntered(MouseEvent e)
			{
				updateBorder.run();
			}


			public void mouseClicked(MouseEvent e)
			{
				updateBorder.run();
			}
		});
		
		button.addFocusListener(new FocusListener() {
		
			public void focusLost(FocusEvent arg0)
			{
				updateBorder.run();
			}
		
		
			public void focusGained(FocusEvent arg0)
			{
				updateBorder.run();
			}
		});

		
		button.addKeyListener(new KeyListener() {
		
			public void keyTyped(KeyEvent arg0)
			{
				updateBorder.run();
			}
		
		
			public void keyReleased(KeyEvent arg0)
			{
				updateBorder.run();
			}
		
		
			public void keyPressed(KeyEvent arg0)
			{
				updateBorder.run();
			}
		});
		
	}
	

	public void makeButton() {
				
		boolean isNimbus = Swidget.isNumbusDerivedLaF();
		
		if (Swidget.isStratusLaF()) {
			button.putClientProperty("stratus-button-border-painted", config.bordered == BORDER_STYLE.ALWAYS);	
		} else {
			button.setContentAreaFilled(config.bordered == BORDER_STYLE.ALWAYS);
			button.setBorderPainted(config.bordered == BORDER_STYLE.ALWAYS);
		}
		
		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
		
		
		ImageButtonLayout mode = config.layout;
		if (mode == null) {
			mode = guessLayout();
		}

		ImageButtonSize buttonSize = config.buttonSize;
		if(buttonSize == null) {
			buttonSize = guessButtonSize(mode);
		}
		
		
		String text = config.text;
		String tooltip = config.tooltip;
		
		button.setIcon(null);
		button.setText("");
		button.setToolTipText(null);
		
		
		switch (mode) {

			case IMAGE:

				if (isNimbus) { 
					button.setBorder(buttonSize == ImageButtonSize.COMPACT ? Spacing.bMedium() : Spacing.bLarge());
				} else {
					button.setMargin(Spacing.iSmall());
				}
				
								
				button.setIcon(image);
				if (tooltip == null || "".equals(tooltip)) {
					tooltip = text;
				}

				break;

			case TEXT:

				if (isNimbus) { 
					button.setBorder(Spacing.bLarge());
				} else {
					button.setMargin(Spacing.iSmall());
				}
				
				button.setText(text);
				break;

			case IMAGE_ON_TOP:

				if (isNimbus) { 
					button.setBorder(Spacing.bLarge());
				} else {
					button.setMargin(Spacing.iSmall());
				}
				
				button.setIcon(image);
				button.setText(text);

				button.setVerticalTextPosition(SwingConstants.BOTTOM);
				button.setHorizontalTextPosition(SwingConstants.CENTER);

				break;

			case IMAGE_ON_SIDE:

				if (isNimbus) { 
					button.setBorder(Spacing.bLarge());
				} else {
					button.setMargin(Spacing.iSmall());
				}
				
				button.setIcon(image);
				button.setText(text);

				break;

		}
		
		if (isNimbus && config.border != null) button.setBorder(config.border);
		if (tooltip != null) button.setToolTipText(tooltip);
		
	}
	
	protected ImageButtonLayout guessLayout() {
		ImageButtonLayout mode = ImageButtonLayout.IMAGE_ON_SIDE;
		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
		if (config.imagename == null || image.getIconHeight() == -1) {
			mode = ImageButtonLayout.TEXT;
		} else if (config.text == null || "".equals(config.text)) {
			mode = ImageButtonLayout.IMAGE;
		}
		return mode;
	}
	
	protected ImageButtonSize guessButtonSize(ImageButtonLayout mode) {
		if (mode == ImageButtonLayout.IMAGE) {
			return ImageButtonSize.COMPACT;
		}
		return ImageButtonSize.LARGE;
		
	}
	
	protected Dimension getPreferredSize(Dimension superPreferred) {
				
		ImageButtonLayout mode = config.layout;
		if (mode == null) {
			mode = guessLayout();
		}
		
		ImageButtonSize buttonSize = config.buttonSize;
		if (buttonSize == null) {
			buttonSize = guessButtonSize(mode);
		}
		
		int prefHeight = 32, prefWidth = 80;
		
		Dimension preferred = superPreferred;
		if (buttonSize == ImageButtonSize.LARGE) {
			
			switch (mode) {
			case IMAGE:
				preferred = new Dimension((int)Math.max(preferred.getWidth(), prefHeight), (int)Math.max(preferred.getHeight(), prefHeight));
				break;
			case TEXT:
			case IMAGE_ON_SIDE:
			case IMAGE_ON_TOP:
			default:
				preferred = new Dimension((int)Math.max(preferred.getWidth(), prefWidth), (int)Math.max(preferred.getHeight(), prefHeight));
				break;
			}
		}
		
		return preferred;
		
	}
	
	void setButtonBorder(boolean forceBorder) {
		ButtonModel m = button.getModel();
		boolean showBackground = button.isEnabled() && config.bordered == BORDER_STYLE.ACTIVE && (m.isSelected() || m.isRollover() || m.isPressed() || m.isArmed() || button.hasFocus() || forceBorder);

		if (config.bordered == BORDER_STYLE.ALWAYS || showBackground ) {
			if (Swidget.isStratusLaF()) {
				button.putClientProperty("stratus-button-border-painted", true);	
			} else {
				button.setContentAreaFilled(true);
				button.setBorderPainted(true);
			}
		} else {
			if (Swidget.isStratusLaF()) {
				button.putClientProperty("stratus-button-border-painted", false);	
			} else {
				button.setContentAreaFilled(true);
				button.setBorderPainted(true);
			}
		}
	

		
		button.repaint();
	}

	public ImageButtonConfig getConfiguration() {
		return config;
	}
	
}