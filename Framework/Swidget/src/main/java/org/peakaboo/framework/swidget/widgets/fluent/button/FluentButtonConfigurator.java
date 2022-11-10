package org.peakaboo.framework.swidget.widgets.fluent.button;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonConfig.BORDER_STYLE;

public class FluentButtonConfigurator {
	
	protected AbstractButton button;
	protected FluentButtonAPI<? extends JComponent, FluentButtonConfig> api;
	protected FluentButtonConfig config;
	
	public FluentButtonConfigurator(AbstractButton button, FluentButtonAPI<? extends JComponent, FluentButtonConfig> api, FluentButtonConfig config) {
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
	
	private ImageIcon makeImage() {
		return IconFactory.getImageIcon(config.imagepath, config.imagename, config.size);
	}

	public void makeButton() {
				
		boolean isNimbus = Swidget.isNumbusDerivedLaF();
		
		if (Swidget.isStratusLaF()) {
			button.putClientProperty(Stratus.KEY_BUTTON_BORDER_PAINTED, config.bordered == BORDER_STYLE.ALWAYS);	
		} else {
			button.setContentAreaFilled(config.bordered == BORDER_STYLE.ALWAYS);
			button.setBorderPainted(config.bordered == BORDER_STYLE.ALWAYS);
		}
		
		
		FluentButtonLayout mode = config.layout;
		if (mode == null) {
			mode = guessLayout();
		}

		FluentButtonSize buttonSize = config.buttonSize;
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
					button.setBorder(buttonSize == FluentButtonSize.COMPACT ? Spacing.bMedium() : Spacing.bLarge());
				} else {
					button.setMargin(Spacing.iSmall());
				}
				
								
				button.setIcon(makeImage());
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
				
				button.setIcon(makeImage());
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
				
				button.setIcon(makeImage());
				button.setText(text);

				break;

		}
		
		if (isNimbus && config.border != null) button.setBorder(config.border);
		if (tooltip != null) button.setToolTipText(tooltip);
		
	}
	
	protected FluentButtonLayout guessLayout() {
		FluentButtonLayout mode = FluentButtonLayout.IMAGE_ON_SIDE;
		ImageIcon image = IconFactory.getImageIcon(config.imagepath, config.imagename, config.size);
		if (config.imagename == null || image.getIconHeight() == -1) {
			mode = FluentButtonLayout.TEXT;
		} else if (config.text == null || "".equals(config.text)) {
			mode = FluentButtonLayout.IMAGE;
		}
		return mode;
	}
	
	protected FluentButtonSize guessButtonSize(FluentButtonLayout mode) {
		if (mode == FluentButtonLayout.IMAGE) {
			return FluentButtonSize.COMPACT;
		}
		return FluentButtonSize.LARGE;
		
	}
	
	protected Dimension getPreferredSize(Dimension superPreferred) {
				
		FluentButtonLayout mode = config.layout;
		if (mode == null) {
			mode = guessLayout();
		}
		
		FluentButtonSize buttonSize = config.buttonSize;
		if (buttonSize == null) {
			buttonSize = guessButtonSize(mode);
		}
		
		int prefHeight = 32, prefWidth = 80;
		
		Dimension preferred = superPreferred;
		if (buttonSize == FluentButtonSize.LARGE) {
			
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
				button.putClientProperty(Stratus.KEY_BUTTON_BORDER_PAINTED, true);	
			} else {
				button.setContentAreaFilled(true);
				button.setBorderPainted(true);
			}
		} else {
			if (Swidget.isStratusLaF()) {
				button.putClientProperty(Stratus.KEY_BUTTON_BORDER_PAINTED, false);	
			} else {
				button.setContentAreaFilled(true);
				button.setBorderPainted(true);
			}
		}
	

		
		button.repaint();
	}

	public FluentButtonConfig getConfiguration() {
		return config;
	}
	
}