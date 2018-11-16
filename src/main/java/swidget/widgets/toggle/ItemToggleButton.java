package swidget.widgets.toggle;


import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;



public class ItemToggleButton extends JToggleButton
{


	private JLabel				titleLabel;
	private JLabel				descLabel;
	private String				title;
	private JLabel 				icon;
		
	private static final int	defaultWidth = Swidget.DEFAULT_TEXTWRAP_WIDTH;

	public ItemToggleButton(String imageName, String title, String description)
	{
		super();
		init(imageName, title, description, defaultWidth);
	}

	
	public ItemToggleButton(String imageName, String title, String description, int width)
	{
		super();
		init(imageName, title, description, width);
	}
	
	
	
	
	public ItemToggleButton(StockIcon stock, String title, String description)
	{
		super();
		init(stock.toIconName(), title, description, defaultWidth);
	}
	
	public ItemToggleButton(StockIcon stock, String title, String description, int width)
	{
		super();
		init(stock.toIconName(), title, description, width);
	}

	

	private void init(String imageName, String title, String description, int width)
	{

		this.title = title;
		this.setOpaque(false);
		
		this.setFocusPainted(true);

		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		 
		JPanel textPanel = createButtonText(title, description, width);
		
		if (! "".equals(description))
		{
	
			if (! "".equals(imageName))
			{
				Icon i = IconFactory.getImageIcon(imageName, IconSize.ICON);
				icon = new JLabel(i);
				icon.setBorder(Spacing.bSmall());
			}
			else
			{
				icon = new JLabel();
			}
			
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 1.0;
			c.weightx = 0.0;

			add(icon, c);


			
			c.gridx = 1;
			c.gridy = 0;
			c.weighty = 0.0;
			c.weightx = 1.0;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;

			add(textPanel, c);

			
		} else {
		
			Icon i = IconFactory.getImageIcon(imageName, IconSize.BUTTON);
			icon = new JLabel(i);
			icon.setBorder(Spacing.bSmall());
			
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 1.0;
			c.weightx = 0.0;

			add(icon, c);
			
			if (Swidget.isNumbusDerivedLaF()) {
				this.setBorder(Spacing.bMedium());
				this.setMargin(Spacing.iNone());
			} else {
				this.setMargin(Spacing.iTiny());
			}

			icon.setBorder(Spacing.bNone());
			this.setToolTipText(title);
			
		}

		validate();



		this.setContentAreaFilled(false);
		this.setBorderPainted(false);



		ButtonModel m = this.getModel();
		this.setBorderPainted(m.isSelected() || m.isRollover() || m.isPressed());

		this.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e)
			{
				setBorder();
			}


			public void mousePressed(MouseEvent e)
			{
				setBorder();
			}


			public void mouseExited(MouseEvent e)
			{
				setBorder();
			}


			public void mouseEntered(MouseEvent e)
			{
				setBorder(true);
			}


			public void mouseClicked(MouseEvent e)
			{
				setBorder();
			}
		});

		this.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				setBorder();
				ItemToggleButton.this.icon.setEnabled(ItemToggleButton.this.isEnabled());
			}
		});
		
		this.addFocusListener(new FocusListener() {
		
			public void focusLost(FocusEvent e)
			{
				setBorder();
			}
		
		
			public void focusGained(FocusEvent e)
			{
				setBorder();
			}
		});

		this.addKeyListener(new KeyListener() {
		
			public void keyTyped(KeyEvent e)
			{
				setBorder();
			}
		
		
			public void keyReleased(KeyEvent e)
			{
				setBorder();
			}
		
		
			public void keyPressed(KeyEvent e)
			{
				setBorder();
			}
			
		});

		

	}

	
	private JPanel createButtonText(String title, String desc, int width)
	{
		
		JPanel p = new ClearPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		titleLabel = new JLabel(title);
		descLabel = new JLabel();
		descLabel.setText(Swidget.lineWrapHTML(descLabel, desc, width));

		
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(titleLabel.getFont().getSize() + 4f));
		descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN));
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		
		c.anchor = GridBagConstraints.SOUTHWEST; 
		p.add(titleLabel, c);
		
		c.gridy++;
		c.anchor = GridBagConstraints.NORTHWEST;
		p.add(descLabel, c);
		
		return p;
		
	}

	
	public String getTitle()
	{
		return title;
	}


	protected void setBorder()
	{
		setBorder(false);
	}


	protected void setBorder(boolean forceBorder)
	{
		ButtonModel m = this.getModel();
		
		boolean showBackground = isEnabled() && (m.isSelected() || m.isRollover() || m.isPressed() || m.isArmed() || forceBorder || hasFocus()); 
		
		if (showBackground) {
			this.setContentAreaFilled(true);
			this.setBorderPainted(true);
		} else {
			this.setContentAreaFilled(false);
			this.setBorderPainted(false);
		}
		

	}


	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		titleLabel.setEnabled(enabled);
		descLabel.setVisible(enabled);

	}

}