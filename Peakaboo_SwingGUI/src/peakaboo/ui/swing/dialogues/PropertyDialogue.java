package peakaboo.ui.swing.dialogues;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import peakaboo.controller.plotter.PlotController;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;


public class PropertyDialogue extends JDialog
{

	GridBagConstraints c;
	JPanel mainPanel;
	
	public PropertyDialogue(String title, JFrame owner, Map<String, String> properties)
	{
		super(owner, title);
		init(owner, properties);
	}
	public PropertyDialogue(String title, JDialog owner, Map<String, String> properties)
	{
		super(owner, title);
		init(owner, properties);
	}
	
	private void init(Window owner, Map<String, String> properties){
		
		Container container = getContentPane();
		JPanel containerPanel = new ClearPanel();
		containerPanel.setLayout(new BorderLayout());
		containerPanel.setBorder(Spacing.bHuge());
		container.add(containerPanel);

		
		mainPanel = new JPanel(new GridBagLayout());

		c = new GridBagConstraints();
		c.ipadx = 8;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.anchor = GridBagConstraints.NORTH;
		c.gridy = 0;
		c.gridx = 0;
		c.gridheight = 11;
		c.weightx = 0;
		
		JLabel icon = new JLabel(StockIcon.BADGE_INFO.toImageIcon(IconSize.ICON));
		icon.setBorder(new EmptyBorder(0, 0, 0, Spacing.huge));
		mainPanel.add(icon, c);
		
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.gridheight = 1;
		c.gridy = 0;
		c.gridx = 1;
		
		for (Entry<String, String> property : properties.entrySet())
		{
			addJLabelPair(property.getKey(), property.getValue());
		}
		
			
		JPanel buttonBox = new ClearPanel();
		buttonBox.setLayout(new BorderLayout());
		
		JSeparator s = new JSeparator(JSeparator.HORIZONTAL);
		s.setBorder(new EmptyBorder(Spacing.medium, 0, Spacing.medium, 0));
		buttonBox.add(s, BorderLayout.NORTH);
		
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", true);
		close.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e)
			{
				PropertyDialogue.this.setVisible(false);
			}
		});
		buttonBox.add(close, BorderLayout.EAST);
		
		
		containerPanel.add(mainPanel, BorderLayout.NORTH);
		containerPanel.add(buttonBox, BorderLayout.SOUTH);
		
		pack();
		
		setMinimumSize(getPreferredSize());
		
		setModal(true);
		setLocationRelativeTo(owner);
		setVisible(true);
		
	}
	
	private void addJLabelPair(String one, String two)
	{
		
		JLabel label;
		
		c.gridx = 1;
		c.weightx = 0.0f;
		label = new JLabel(one, SwingConstants.LEFT);
		mainPanel.add(label, c);
		
		c.gridx = 2;
		c.weightx = 1f;
		label = new JLabel(two, SwingConstants.RIGHT);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setPreferredSize(new Dimension(225, label.getPreferredSize().height));
		label.setToolTipText(two);
		mainPanel.add(label, c);
		c.gridy += 1;
		c.gridx = 0;
		
		
	}
	
}
