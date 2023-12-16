package org.peakaboo.framework.stratus.components.ui.fluentcontrols.button;

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

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig.BorderStyle;

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
		return IconFactory.getImageIcon(config);
	}

	public void makeButton() {

		button.putClientProperty(Stratus.KEY_BUTTON_BORDER_PAINTED, config.bordered == BorderStyle.ALWAYS);	
		button.putClientProperty(Stratus.KEY_BUTTON_NOTIFICATION_DOT, config.notificationDot);
		
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

				button.setBorder(buttonSize == FluentButtonSize.COMPACT ? Spacing.bMedium() : Spacing.bLarge());
				button.setIcon(makeImage());
				if (tooltip == null || "".equals(tooltip)) {
					tooltip = text;
				}

				break;

			case TEXT:

				button.setBorder(Spacing.bLarge());

				button.setText(text);
				break;

			case IMAGE_ON_TOP:

				button.setBorder(Spacing.bLarge());
				button.setIcon(makeImage());
				button.setText(text);
				button.setVerticalTextPosition(SwingConstants.BOTTOM);
				button.setHorizontalTextPosition(SwingConstants.CENTER);

				break;

			case IMAGE_ON_SIDE:

				button.setBorder(Spacing.bLarge());
				button.setIcon(makeImage());
				button.setText(text);

				break;

		}
		
		if (config.border != null) button.setBorder(config.border);
		if (tooltip != null) button.setToolTipText(tooltip);
		
	}
	
	protected FluentButtonLayout guessLayout() {
		FluentButtonLayout mode = FluentButtonLayout.IMAGE_ON_SIDE;
		if (config.imagename == null || config.imagepath == null) {
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
		boolean showBackground = button.isEnabled() && config.bordered == BorderStyle.ACTIVE && (m.isSelected() || m.isRollover() || m.isPressed() || m.isArmed() || button.hasFocus() || forceBorder);

		if (config.bordered == BorderStyle.ALWAYS || showBackground ) {
			button.putClientProperty(Stratus.KEY_BUTTON_BORDER_PAINTED, true);	
		} else {
			button.putClientProperty(Stratus.KEY_BUTTON_BORDER_PAINTED, false);	
		}
	

		
		button.repaint();
	}

	public FluentButtonConfig getConfiguration() {
		return config;
	}
	
}