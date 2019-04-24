package org.peakaboo.framework.swidget.dialogues;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;
import org.peakaboo.framework.swidget.widgets.layout.TitledPanel;


public class PropertyDialogue extends JDialog
{

	public PropertyDialogue(String title, Window owner, TitledPanel... panels)
	{
		this(title, owner, Arrays.asList(panels));
	}
	
	public PropertyDialogue(String title, Window owner, List<TitledPanel> panels)
	{
		super(owner, title);

		Container container = getContentPane();
		JPanel containerPanel = new ClearPanel();
		containerPanel.setLayout(new BorderLayout());
		container.add(containerPanel);


		//Property Panels
		JPanel allPropertyPanels = new JPanel();
		allPropertyPanels.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Spacing.iMedium(), 0, 0);
		
		
		for (TitledPanel propPanel : panels) {
			propPanel.setBorder(Spacing.bHuge());
			allPropertyPanels.add(propPanel, gbc);
			gbc.gridy++;
		}
		
		containerPanel.add(allPropertyPanels, BorderLayout.CENTER);
		
		//Button Box
		ButtonBox bbox = new ButtonBox();
		ImageButton close = new ImageButton("Close", StockIcon.WINDOW_CLOSE);
		close.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e)
			{
				PropertyDialogue.this.setVisible(false);
			}
		});
		bbox.addRight(0, close);
		containerPanel.add(bbox, BorderLayout.SOUTH);
		
		
		pack();
		
		setMinimumSize(getPreferredSize());
		
		setModal(true);
		setLocationRelativeTo(owner);
		setVisible(true);
		
	}

	
}
