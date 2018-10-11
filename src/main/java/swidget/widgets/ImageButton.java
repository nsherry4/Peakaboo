package swidget.widgets;


import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton.ButtonConfig;
import swidget.widgets.ImageButton.ButtonSize;
import swidget.widgets.ImageButton.Layout;


public class ImageButton extends JButton
{
	
	public enum ButtonSize {
		LARGE, COMPACT;
	}
	
	public static class ButtonConfig {
		String imagename = null;
		String text = "";
		String tooltip = null;
		public Layout layout = null;
		boolean bordered = true;
		IconSize size = IconSize.BUTTON;
		Border border = null;
		ButtonSize buttonSize = null;
	}
	protected ButtonConfig config = new ButtonConfig();
	
	public final static Layout defaultLayout = Layout.IMAGE_ON_SIDE;
	public final static IconSize defaultSize = IconSize.BUTTON;
	public final static boolean defaultBorder = false;	
	
	private boolean isNimbus;
	private Runnable onAction = null;
	
	public enum Layout
	{
		IMAGE, TEXT, IMAGE_ON_TOP, IMAGE_ON_SIDE
	}
	
	

	
	

	public ImageButton() {
		init();
		makeButton();
	}
	
	public ImageButton(String text) {
		config.text = text;
		
		init();
		makeButton();
	}
	
	public ImageButton(StockIcon icon) {
		config.imagename = icon.toIconName();

		init();
		makeButton();
	}

	public ImageButton(StockIcon icon, IconSize size) {
		config.imagename = icon.toIconName();
		config.size = size;

		init();
		makeButton();
	}

	
	public ImageButton(String text, StockIcon icon) {
		config.text = text;
		config.imagename = icon.toIconName();

		init();
		makeButton();
	}
	
	public ImageButton(String text, String icon) {
		config.text = text;
		config.imagename = icon;

		init();
		makeButton();
	}
	
	

	
	public ImageButton withBordered(boolean bordered) {
		config.bordered = bordered;
		makeButton();
		return this;
	}
	
	public ImageButton withIcon(StockIcon stock) {
		return withIcon(stock, IconSize.BUTTON);
	}
	
	public ImageButton withIcon(StockIcon stock, IconSize size) {
		return withIcon(stock.toIconName(), size);
	}
	
	public ImageButton withIcon(String filename) {
		return withIcon(filename, IconSize.BUTTON);
	}
	
	public ImageButton withIcon(String filename, IconSize size) {
		config.imagename = filename;
		config.size = size;
		makeButton();
		return this;
	}
	
	public ImageButton withBorder(Border border) {
		config.border = border;
		makeButton();
		return this;
	}
	
	public ImageButton withText(String text) {
		config.text = text;
		makeButton();
		return this;
	}
	
	public ImageButton withTooltip(String tooltip) {
		config.tooltip = tooltip;
		makeButton();
		return this;
	}
	
	protected void makeButton() {
		ButtonDesigner.makeButton(this, config);
	}
	
	public ImageButton withLayout(Layout layout) {
		config.layout = layout;
		ButtonDesigner.makeButton(this, config);
		return this;
	}
	
	public ImageButton withButtonSize(ButtonSize buttonSize) {
		config.buttonSize = buttonSize;
		ButtonDesigner.makeButton(this, config);
		return this;
	}
	
	public ImageButton withAction(Runnable action) {
		this.onAction = action;
		return this;
	}
	
	public ImageButton withStateDefault() {
		this.setBackground(new Color(0xff1f89d1, true));
		this.setForeground(Color.WHITE);
		return this;
	}
	
	public ImageButton withStateCritical() {
		this.setBackground(new Color(0xffF60A24, true));
		this.setForeground(Color.WHITE);
		return this;
	}
	
	
	private void init() {
		
		this.setOpaque(false);
		
		this.addActionListener(e -> {
			if (onAction != null) {
				onAction.run();
			}
		});
		
		this.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e)
			{
				setButtonBorder();
			}


			public void mousePressed(MouseEvent e)
			{
				setButtonBorder();
			}


			public void mouseExited(MouseEvent e)
			{
				setButtonBorder();
			}


			public void mouseEntered(MouseEvent e)
			{
				setButtonBorder();
			}


			public void mouseClicked(MouseEvent e)
			{
				setButtonBorder();
			}
		});
		
		this.addFocusListener(new FocusListener() {
		
			public void focusLost(FocusEvent arg0)
			{
				setButtonBorder();
			}
		
		
			public void focusGained(FocusEvent arg0)
			{
				setButtonBorder();
			}
		});

		
		this.addKeyListener(new KeyListener() {
		
			public void keyTyped(KeyEvent arg0)
			{
				setButtonBorder();
			}
		
		
			public void keyReleased(KeyEvent arg0)
			{
				setButtonBorder();
			}
		
		
			public void keyPressed(KeyEvent arg0)
			{
				setButtonBorder();
			}
		});
		
	}
//	
//	protected void makeButton() {
//				
//		isNimbus = Swidget.isNumbusDerivedLaF();
//		
//		this.setContentAreaFilled(config.bordered);
//		this.setBorderPainted(config.bordered);
//		
//		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
//		
//		
//		Layout mode = config.layout;
//		if (mode == null) {
//			mode = guessLayout();
//		}
//
//		ButtonSize buttonSize = config.buttonSize;
//		if(buttonSize == null) {
//			buttonSize = guessButtonSize(mode);
//		}
//		
//		
//		String text = config.text;
//		String tooltip = config.tooltip;
//		
//		this.setIcon(null);
//		this.setText("");
//		this.setToolTipText(null);
//		
//		
//		switch (mode) {
//
//			case IMAGE:
//
//				if (isNimbus) { 
//					super.setBorder(buttonSize == ButtonSize.COMPACT ? Spacing.bMedium() : Spacing.bLarge());
//				} else {
//					this.setMargin(Spacing.iSmall());
//				}
//				
//								
//				this.setIcon(image);
//				if (tooltip == null || "".equals(tooltip)) {
//					tooltip = text;
//				}
//
//				break;
//
//			case TEXT:
//
//				if (isNimbus) { 
//					super.setBorder(Spacing.bLarge());
//				} else {
//					this.setMargin(Spacing.iSmall());
//				}
//				
//				this.setText(text);
//				break;
//
//			case IMAGE_ON_TOP:
//
//				if (isNimbus) { 
//					super.setBorder(Spacing.bLarge());
//				} else {
//					super.setMargin(Spacing.iSmall());
//				}
//				
//				this.setIcon(image);
//				this.setText(text);
//
//				this.setVerticalTextPosition(SwingConstants.BOTTOM);
//				this.setHorizontalTextPosition(SwingConstants.CENTER);
//
//				break;
//
//			case IMAGE_ON_SIDE:
//
//				if (isNimbus) { 
//					super.setBorder(Spacing.bLarge());
//				} else {
//					super.setMargin(Spacing.iSmall());
//				}
//				
//				this.setIcon(image);
//				this.setText(text);
//
//				break;
//
//		}
//		
//		if (isNimbus && config.border != null) super.setBorder(config.border);
//		if (tooltip != null) this.setToolTipText(tooltip);
//		
//	}
//	
	
//	protected Layout guessLayout() {
//		Layout mode = Layout.IMAGE_ON_SIDE;
//		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
//		if (config.imagename == null || image.getIconHeight() == -1) {
//			mode = Layout.TEXT;
//		} else if (config.text == null || "".equals(config.text)) {
//			mode = Layout.IMAGE;
//		}
//		return mode;
//	}
	
	protected void setButtonBorder()
	{
		setButtonBorder(false);
	}
	
	protected void setButtonBorder(boolean forceBorder)
	{
		ButtonModel m = this.getModel();
		boolean showBackground = isEnabled() && (m.isSelected() || m.isRollover() || m.isPressed() || m.isArmed() || forceBorder);

		if (config.bordered || showBackground || (isEnabled() && hasFocus()) ) {
			setBorderPainted(true);
			this.setContentAreaFilled(true);
		} else {
			setBorderPainted(false);
			this.setContentAreaFilled(false);
		}

		
		
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		
		if (super.isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		
		Layout mode = config.layout;
		if (mode == null) {
			mode = ButtonDesigner.guessLayout(this, config);
		}
		
		ButtonSize buttonSize = config.buttonSize;
		if (buttonSize == null) {
			buttonSize = ButtonDesigner.guessButtonSize(mode);
		}
		
		int prefHeight = 32, prefWidth = 80;
		
		Dimension preferred = super.getPreferredSize();
		if (buttonSize == ButtonSize.LARGE) {
			
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
	
//	protected ButtonSize guessButtonSize(Layout mode) {
//		if (mode == Layout.IMAGE) {
//			return ButtonSize.COMPACT;
//		}
//		return ButtonSize.LARGE;
//		
//	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	

	
	public static String getWrappingTooltipText(Component c, String text)
	{
		int width = 400;
		List<String> lines = new ArrayList<String>();
		
		Font font = c.getFont();
		FontMetrics metrics = c.getFontMetrics(font);
				
		String line = "";
		Graphics g = c.getGraphics();
		
		List<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));
		
		
		lines.clear();
		while (words.size() > 0)
		{
		
			while ( metrics.getStringBounds(line, g).getWidth() < width )
			{
				if (words.size() == 0) break;
				if (!line.equals("")) line += " ";
				line = line + words.remove(0);
			}
			
			lines.add(line);
			line = "";
			
		}
		
		Optional<String> str = lines.stream().reduce((a, b) -> a + "<br>" + b);
		return "<html>" + str.orElse("") + "</html>";
	}
	
	@Override
	public void setToolTipText(String text)	{
		if (text == null) {
			super.setToolTipText(null);
		} else {
			super.setToolTipText(ImageButton.getWrappingTooltipText(this, text));
		}
	}


}


class ButtonDesigner {
	

	public static void makeButton(AbstractButton button, ButtonConfig config) {
				
		boolean isNimbus = Swidget.isNumbusDerivedLaF();
		
		button.setContentAreaFilled(config.bordered);
		button.setBorderPainted(config.bordered);
		
		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
		
		
		Layout mode = config.layout;
		if (mode == null) {
			mode = guessLayout(button, config);
		}

		ButtonSize buttonSize = config.buttonSize;
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
					button.setBorder(buttonSize == ButtonSize.COMPACT ? Spacing.bMedium() : Spacing.bLarge());
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
	
	protected static Layout guessLayout(AbstractButton button, ButtonConfig config) {
		Layout mode = Layout.IMAGE_ON_SIDE;
		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
		if (config.imagename == null || image.getIconHeight() == -1) {
			mode = Layout.TEXT;
		} else if (config.text == null || "".equals(config.text)) {
			mode = Layout.IMAGE;
		}
		return mode;
	}
	
	protected static ButtonSize guessButtonSize(Layout mode) {
		if (mode == Layout.IMAGE) {
			return ButtonSize.COMPACT;
		}
		return ButtonSize.LARGE;
		
	}
	
}
