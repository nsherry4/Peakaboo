package peakaboo.ui.swing.widgets.toggle;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import peakaboo.ui.swing.icons.IconFactory;
import peakaboo.ui.swing.icons.IconSize;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.Spacing;



public class ComplexToggle extends JToggleButton
{

	private Color				originalBackground;
	private boolean				showBorder;

	private JLabel				titleLabel;
	private JLabel				descLabel;
	private JPanel				text;
	private String				title;
	private JLabel 				icon;
	

	public ComplexToggle(String imageName, String title, String description)
	{
		super();
		init(imageName, title, description);
	}


	public ComplexToggle(String imageName, String title, String description, ComplexToggleGroup group)
	{
		super();
		group.registerButton(this);
		init(imageName, title, description);
	}


	private void init(String imageName, String title, String description)
	{

		this.title = title;
		this.setOpaque(false);
		
		this.setFocusPainted(true);

		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		 
		


		titleLabel = new JLabel(title);
		descLabel = new JLabel("<html>" + description + "<html>");
		
		if (! "".equals(title))
		{
			
			Icon i = IconFactory.getImageIcon(imageName);
			icon = new JLabel(i);
			icon.setBorder(Spacing.bSmall());
			
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 1.0;
			c.weightx = 0.0;

			add(icon, c);
			
			titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(titleLabel.getFont().getSize() + 4f));
			descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN));
	
			text = new ClearPanel();
			text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
			text.add(titleLabel);
			text.add(descLabel);
			
			c.gridx = 1;
			c.gridy = 0;
			c.weighty = 0.0;
			c.weightx = 1.0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;

			add(text, c);

			
		} else {
				
			Icon i = IconFactory.getImageIcon(imageName, IconSize.BUTTON);
			icon = new JLabel(i);
			icon.setBorder(Spacing.bSmall());
			
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 1.0;
			c.weightx = 0.0;

			add(icon, c);
			
			this.setMargin(Spacing.iNone());
			this.setBorder(Spacing.bMedium());
			icon.setBorder(Spacing.bNone());
			this.setToolTipText(description);
			
		}



		originalBackground = this.getBackground();
		this.showBorder = false;

		if (!showBorder) {
			this.setOpaque(false);
			this.setBackground(new Color(0, 0, 0, 0));
		}



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
				ComplexToggle.this.icon.setEnabled(ComplexToggle.this.isEnabled());
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
			setBackground(originalBackground);
		} else {
			setBackground(new Color(0, 0, 0, 0));
		}
		
		setBorderPainted(showBackground);

	}


	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		titleLabel.setEnabled(enabled);
		descLabel.setVisible(enabled);

	}

}
