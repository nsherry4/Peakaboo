package peakaboo.ui.swing.widgets;


import java.awt.Color;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import peakaboo.ui.swing.icons.IconFactory;
import peakaboo.ui.swing.icons.IconSize;


public class ImageButton extends JButton
{

	protected static Layout defaultLayout = Layout.IMAGE_ON_SIDE;
	protected static IconSize defaultSize = IconSize.BUTTON;
	protected static boolean defaultBorder = false;
	
	public enum Layout
	{
		IMAGE, TEXT, IMAGE_ON_TOP, IMAGE_ON_SIDE
	}
	
	

	private Color	originalBackground;
	private boolean	showBorder;

	public ImageButton(String filename, String text)
	{
		super();
		initialize(filename, text, null, defaultLayout, defaultBorder, defaultSize, null, null);
	}

	public ImageButton(String filename, String text, boolean showBorder)
	{
		super();
		initialize(filename, text, null, defaultLayout, showBorder, defaultSize, null, null);
	}
	
	public ImageButton(String filename, String text, IconSize size)
	{
		super();
		initialize(filename, text, null, defaultLayout, defaultBorder, size, null, null);
	}
	
	
	public ImageButton(String filename, String text, Layout mode)
	{
		super();
		initialize(filename, text, null, mode, defaultBorder, defaultSize, null, null);
	}

	public ImageButton(String filename, String text, String tooltip)
	{
		super();
		initialize(filename, text, tooltip, defaultLayout, defaultBorder, defaultSize, null, null);
	}
	
	public ImageButton(String filename, String text, String tooltip, boolean showBorder)
	{
		super();
		initialize(filename, text, tooltip, defaultLayout, showBorder, defaultSize, null, null);
	}
	
	public ImageButton(String filename, String text, Layout mode, IconSize size)
	{
		super();
		initialize(filename, text, null, mode, defaultBorder, size, null, null);
	}
	
	public ImageButton(String filename, String text, String tooltip, Layout mode)
	{
		super();
		initialize(filename, text, tooltip, mode, defaultBorder, defaultSize, null, null);
	}

	public ImageButton(String filename, String text, String tooltip, Layout mode, IconSize size)
	{
		super();
		initialize(filename, text, tooltip, mode, defaultBorder, size, null, null);
	}

	public ImageButton(String filename, String text, String tooltip, IconSize size)
	{
		super();
		initialize(filename, text, tooltip, defaultLayout, defaultBorder, size, null, null);
	}

	public ImageButton(String filename, String text, Layout mode, boolean showBorder)
	{
		super();
		initialize(filename, text, null, mode, showBorder, defaultSize, null, null);
	}

	public ImageButton(String filename, String text, String tooltip, Layout mode, boolean showBorder)
	{
		super();
		initialize(filename, text, tooltip, mode, showBorder, defaultSize, null, null);
	}

	public ImageButton(String filename, String text, String tooltip, Layout mode, boolean showBorder, IconSize size)
	{
		super();
		initialize(filename, text, tooltip, mode, showBorder, size, null, null);
	}
	
	public ImageButton(String filename, String text, String tooltip, Layout mode, boolean showBorder, IconSize size, Insets insets, Border border)
	{
		super();
		initialize(filename, text, tooltip, mode, showBorder, size, insets, border);
	}





	private void initialize(String filename, String text, String tooltip, Layout mode, boolean _showBorder, IconSize size, Insets insets, Border border)
	{
				
		originalBackground = this.getBackground();
		this.showBorder = _showBorder;		

		
		if (!showBorder) {
			this.setOpaque(false);
			this.setBackground(new Color(0, 0, 0, 0));
		}

		ImageIcon image = IconFactory.getImageIcon(filename, size);

		if (image.getIconHeight() == -1) {
			mode = Layout.TEXT;
		} else if (text == null || "".equals(text)) {
			mode = Layout.IMAGE;
		}

		// don't remove the borders if this button is text only
		if (mode != Layout.TEXT) {

			if (!showBorder) {
				this.setBorderPainted(false);
			}
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

		}
		
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

		addFocusListener(new FocusListener() {
		
			public void focusLost(FocusEvent e)
			{
				setButtonBorder();
			}
		
		
			public void focusGained(FocusEvent e)
			{
				setButtonBorder();
			}
		});

		
		
		switch (mode) {

			case IMAGE:

				this.setMargin(Spacing.iNone());
				super.setBorder(Spacing.bMedium());
				
				
				
				this.setIcon(image);
				if (tooltip == null || "".equals(tooltip)) {
					tooltip = text;
				}

				break;

			case TEXT:

				this.setMargin(Spacing.iButton());
				super.setBorder(Spacing.bLarge());
				
				this.setText(text);
				break;

			case IMAGE_ON_TOP:

				this.setMargin(Spacing.iSmall());
				super.setBorder(Spacing.bLarge());
				
				this.setIcon(image);
				this.setText(text);

				this.setVerticalTextPosition(SwingConstants.BOTTOM);
				this.setHorizontalTextPosition(SwingConstants.CENTER);

				break;

			case IMAGE_ON_SIDE:

				this.setMargin(Spacing.iButton());
				super.setBorder(Spacing.bLarge());
				
				this.setIcon(image);
				this.setText(text);

				break;

		}
		
		if (insets != null) this.setMargin(insets);
		if (border != null) super.setBorder(border);
		
		if (tooltip != null) this.setToolTipText(tooltip);

	}
	
	
	
	protected void setButtonBorder()
	{
		setButtonBorder(false);
	}
	
	protected void setButtonBorder(boolean forceBorder)
	{
		ButtonModel m = this.getModel();
		boolean showBackground = isEnabled() && (m.isSelected() || m.isRollover() || m.isPressed() || m.isArmed() || forceBorder || showBorder);

		if (showBackground || (isEnabled() && hasFocus()) ) {
			setBackground(originalBackground);
		} else {
			setBackground(new Color(0, 0, 0, 0));
		}

		
		setBorderPainted(showBackground);
		
		repaint();
	}
	



}
