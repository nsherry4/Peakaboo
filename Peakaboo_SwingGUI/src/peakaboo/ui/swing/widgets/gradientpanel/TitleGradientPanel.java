package peakaboo.ui.swing.widgets.gradientpanel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import peakaboo.ui.swing.widgets.Spacing;


public class TitleGradientPanel extends GradientPanel
{


	public TitleGradientPanel(String title, boolean drawBackground, JComponent bottomWidget){
		super(drawBackground);
		init(title, bottomWidget);
	}
	public TitleGradientPanel(String title, boolean drawBackground){
		super(drawBackground);
		init(title, null);
	}
	public TitleGradientPanel(String title){
		super(false);
		init(title, null);
	}
	
	private void init(String title, JComponent bottomWidget){
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();

		
		JLabel titleLabel = new JLabel(title);
		titleLabel.setOpaque(false);
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getSize() + 4f).deriveFont(Font.BOLD));
		titleLabel.setBorder(Spacing.bMedium());
		
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;
		add(titleLabel, c);
		
		
		if (bottomWidget != null){
			
			bottomWidget.setOpaque(false);
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 1.0;
			c.weighty = 0.0;
			add(bottomWidget, c);
		}
	}
	
}
