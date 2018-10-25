package swidget.widgets.buttons;


import java.awt.Dimension;

import javax.swing.JToggleButton;

import swidget.Swidget;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;



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
			super.setToolTipText(Swidget.lineWrapTooltip(this, text));
		}
	}



//
//
//	public void initialize(String filename, String text, String tooltip, Layout mode, boolean _showBorder, IconSize size, Insets insets, Border border)
//	{
//		
//	
//		isNimbus = Swidget.isNumbusDerivedLaF();
//		
//		
//		
//		this.showBorder = _showBorder;		
//		this.setContentAreaFilled(showBorder);
//		this.setBorderPainted(showBorder);
//		
//		/* SET BUTTON CONTENT */
//		ImageIcon image = IconFactory.getImageIcon(filename, size);
//
//		if (image.getIconHeight() == -1) {
//			mode = Layout.TEXT;
//		} else if (text == null || "".equals(text)) {
//			mode = Layout.IMAGE;
//		}
//
//	
//		this.addMouseListener(new MouseListener() {
//
//			public void mouseReleased(MouseEvent e)
//			{
//				setButtonBorder();
//			}
//
//
//			public void mousePressed(MouseEvent e)
//			{
//				setButtonBorder();
//			}
//
//
//			public void mouseExited(MouseEvent e)
//			{
//				setButtonBorder();
//			}
//
//
//			public void mouseEntered(MouseEvent e)
//			{
//				setButtonBorder();
//			}
//
//
//			public void mouseClicked(MouseEvent e)
//			{
//				setButtonBorder();
//			}
//		});
//		
//		this.addFocusListener(new FocusListener() {
//		
//			public void focusLost(FocusEvent arg0)
//			{
//				setButtonBorder();
//			}
//		
//		
//			public void focusGained(FocusEvent arg0)
//			{
//				setButtonBorder();
//			}
//		});
//
//		
//		this.addKeyListener(new KeyListener() {
//		
//			public void keyTyped(KeyEvent arg0)
//			{
//				setButtonBorder();
//			}
//		
//		
//			public void keyReleased(KeyEvent arg0)
//			{
//				setButtonBorder();
//			}
//		
//		
//			public void keyPressed(KeyEvent arg0)
//			{
//				setButtonBorder();
//			}
//		});
//
//		
//		switch (mode) {
//
//			case IMAGE:
//
//				if (isNimbus) { 
//					super.setBorder(Spacing.bMedium());
//				} else {
//					this.setMargin(Spacing.iSmall());
//				}
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
//		//if (! isNumbus && insets != null) this.setMargin(insets);
//		if (isNimbus && border != null) super.setBorder(border);
//		
//		
//		//setMargin(new Insets(-1, -1, -1, -1));
//
//		//setBorder(new CompoundBorder(getBorder(), new javax.swing.plaf.basic.BasicBorders.MarginBorder()));
//				
//		if (tooltip != null) this.setToolTipText(tooltip);
//
//		
//	}
//	
//	
//	
//	protected void setButtonBorder()
//	{
//		setButtonBorder(false);
//	}
//	
//	protected void setButtonBorder(boolean forceBorder)
//	{
//		ButtonModel m = this.getModel();
//		boolean showBackground = isEnabled() && (m.isSelected() || m.isRollover() || m.isPressed() || m.isArmed() || forceBorder || showBorder);
//
//		if (showBackground || (isEnabled() && hasFocus()) ) {
//			setBorderPainted(true);
//			this.setContentAreaFilled(true);
//		} else {
//			setBorderPainted(false);
//			this.setContentAreaFilled(false);
//		}
//
//		
//		
//		repaint();
//	}
//	
//
//	
//	public static String getWrappingTooltipText(Component c, String text)
//	{
//		int width = 400;
//		List<String> lines = new ArrayList<String>();
//		
//		Font font = c.getFont();
//		FontMetrics metrics = c.getFontMetrics(font);
//				
//		String line = "";
//		Graphics g = c.getGraphics();
//		
//		List<String> chars = new ArrayList<String>(Arrays.asList(text.split(" ")));
//		
//		
//		lines.clear();
//		while (chars.size() > 0)
//		{
//		
//			while ( metrics.getStringBounds(line, g).getWidth() < width )
//			{
//				if (chars.size() == 0) break;
//				if (line.equals("")) line += " ";
//				line = line + chars.get(0);
//				chars = chars.subList(1, chars.size());
//			}
//			
//			lines.add(line);
//			line = "";
//			
//		}
//		
//		Optional<String> str = lines.stream().reduce((a, b) -> a + "<br>" + b);
//		return "<html>" + str.orElse("") + "</html>";
//	}
//	
//	@Override
//	public void setToolTipText(String text)
//	{
//		super.setToolTipText(ImageButtonConfigurator.getWrappingTooltipText(this, text));
//	}
	



}