package org.peakaboo.framework.stratus.components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.peakaboo.framework.stratus.api.ColourPalette;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;

public class Banner extends ClearPanel {

	// Related types
	public static record BannerAction(String title, Runnable action) {};
	public static record BannerStyle(Color bg, Color fg, ImageIcon icon) {};
	
	// Preset styles
	private static ColourPalette palette = Stratus.getTheme().getPalette();
	public static BannerStyle STYLE_INFO = new BannerStyle(palette.getColour("Blue", "1") , Stratus.getTheme().getControlText(), StockIcon.BADGE_INFO.toImageIcon(IconSize.TOOLBAR_SMALL) );
	public static BannerStyle STYLE_WARN = new BannerStyle(new Color(0xfffec1) , Stratus.getTheme().getControlText(), StockIcon.BADGE_WARNING.toImageIcon(IconSize.TOOLBAR_SMALL) );
	public static BannerStyle STYLE_ERROR = new BannerStyle(palette.getColour("Red", "4") , Stratus.getTheme().getHighlightText(), StockIcon.BADGE_ERROR.toImageIcon(IconSize.TOOLBAR_SMALL) );

	// Internal state
	private boolean closed = false;
	
	
	

	public Banner(String message) {
		this(message, STYLE_INFO, List.of(), false);
	}
	
	public Banner(String message, BannerStyle style) {
		this(message, style, List.of(), false);
	}

	public Banner(String message, BannerStyle style, boolean closable) {
		this(message, style, List.of(), closable);
	}
	
	public Banner(String message, BannerStyle style, BannerAction action) {
		this(message, style, action, false);
	}
	
	public Banner(String message, BannerStyle style, BannerAction action, boolean closable) {
		this(message, style, List.of(action), closable);
	}

	public Banner(String message, BannerStyle style, List<BannerAction> actions) {
		this(message, style, actions, false);
	}
	
	public Banner(String message, BannerStyle style, List<BannerAction> actions, boolean closable) {
		
		setOpaque(true);
		setBackground(style.bg);
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(Spacing.bMedium());
		
		var icon = new JLabel(style.icon);
		icon.setBorder(Spacing.bSmall());
		add(icon);
		
		var label = new JLabel(message);
		label.setBorder(Spacing.bSmall());
		label.setForeground(style.fg);
		label.setFont(label.getFont().deriveFont(13f));
		add(label);
		
		add(Box.createHorizontalGlue());
		
		for (var action : actions) {
			var button = new FluentButton()
					.withText(action.title)
					.withAction(action.action)
					.withTooltip(action.title)
					.withBordered(false);
			button.setForeground(style.fg);
			add(button);

		}
		
		if (closable) {
			var close = new FluentButton()
					.withIcon(StockIcon.WINDOW_CLOSE, IconSize.BUTTON)
					.withBordered(false)
					.withTooltip("Close this banner")
					.withAction(this::closeBanner);
			close.setPreferredSize(new Dimension(32, 32));
			add(close);
		}
		
		
	}
	
	/**
	 * Display this banner even if the user has previously closed it. Use with
	 * caution to avoid annoying the user
	 */
	public void openBanner() {
		this.closed = false;
		showBanner();
	}
	
	/**
	 * Display this banner so long as the user has not previously dismissed it.
	 */
	public void showBanner() {
		if (this.closed) return;
		this.setVisible(true);
	}
	
	/**
	 * Hide this banner without marking it as closed by the user.
	 */
	public void hideBanner() {
		this.setVisible(false);
	}
	
	/**
	 * Hide this banner and mark it as closed by the user.
	 */
	public void closeBanner() {
		this.closed = true;
		hideBanner();
	}
	
}
