package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;


public class TitledPanel extends JPanel
{

	private ImageIcon icon = StockIcon.BADGE_INFO.toImageIcon(IconSize.ICON);
	private JLabel iconLabel;
	private GridBagConstraints c;
	
	private boolean showBadge = false;
	private String caption = null;
	private JComponent component;
	private JComponent controls = null;
	
	public TitledPanel(JComponent component)
	{
		this(component, null);
	}

	public TitledPanel(JComponent component, String caption)
	{
		this(component, caption, true);
	}
	
	public TitledPanel(JComponent component, boolean showBadge)
	{
		this(component, null, showBadge);
	}

	public TitledPanel(JComponent component, String caption, boolean showBadge)
	{
		this.caption = caption;
		this.showBadge = showBadge;
		this.component = component;
		setLayout(new GridBagLayout());
		
		make();
		
	}
	
	public void addControls(JComponent controls) {
		this.controls = controls;
		make();
	}

	private void make() {
		
		removeAll();
		
		c = new GridBagConstraints();
		c.ipadx = 8;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.anchor = GridBagConstraints.NORTH;
		c.gridy = 0;
		c.gridx = 0;
		c.gridheight = 2;
		c.weightx = 0;
		
		if (showBadge) {
			if (iconLabel == null) {
				iconLabel = new JLabel();
				iconLabel.setBorder(new EmptyBorder(0, 0, 0, Spacing.huge));
			}
			iconLabel.setIcon(icon);
			add(iconLabel, c);
		}
		
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.gridheight = 1;
		c.gridy = 0;
		c.gridx = (showBadge ? 1 : 0);
		
		if (caption != null) {
			add(new JLabel("<html><big><b>" + caption + "</b></big></html>"), c);
			c.gridx++;
		}
		
		if (controls != null) {
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.NONE;
			add(controls, c);
			c.gridx++;
		}
		
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = (showBadge ? 1 : 0);
		c.gridy = 1;
		if (component != null) {
			add(component, c);
		}

		this.repaint();
		
		
	}
	

	public void setBadge(ImageIcon imageIcon) {
		icon = imageIcon;
		make();
	}
}
