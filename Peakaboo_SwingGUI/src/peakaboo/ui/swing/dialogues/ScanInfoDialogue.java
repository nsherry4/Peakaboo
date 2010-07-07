package peakaboo.ui.swing.dialogues;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import peakaboo.controller.plotter.PlotController;
import swidget.containers.SwidgetContainer;
import swidget.containers.SwidgetDialog;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;


public class ScanInfoDialogue extends SwidgetDialog
{

	GridBagConstraints c;
	JPanel mainPanel;
	
	public ScanInfoDialogue(SwidgetContainer owner, PlotController controller)
	{
		super(owner, "Scan Information");
		
		Container container = getContentPane();
		mainPanel = new JPanel(new GridBagLayout());
		container.add(mainPanel);
		mainPanel.setBorder(Spacing.bHuge());
		
		setTitle("Dataset Information");
		
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
		
		addJLabelPair("Date of Creation:", controller.getScanCreationTime());
		addJLabelPair("Created By:", controller.getScanCreator());
		
		addJLabelPair("Project Name: ", controller.getScanProjectName());
		addJLabelPair("Session Name: ", controller.getScanSessionName());
		addJLabelPair("Experiment Name: ", controller.getScanExperimentName());
		addJLabelPair("Sample Name: ", controller.getScanSampleName());
		addJLabelPair("Scan Name: ", controller.getScanScanName());
		
		addJLabelPair("Facility: ", controller.getScanFacilityName());
		addJLabelPair("Laboratory: ", controller.getScanLaboratoryName());
		addJLabelPair("Instrument: ", controller.getScanInstrumentName());
		addJLabelPair("Technique: ", controller.getScanTechniqueName());
		
		c.gridwidth = 3;
		c.ipadx = 0;
		mainPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
		
		c.gridy += 1;
		c.ipady = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", true);
		close.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e)
			{
				ScanInfoDialogue.this.setVisible(false);
			}
		});
		mainPanel.add(close, c);
		
		
		pack();
		setModal(true);
		centreOnParent();
		setVisible(true);
		
	}
	
	private void addJLabelPair(String one, String two)
	{
		
		JLabel label;
		
		c.gridx = 1;
		label = new JLabel(one, SwingConstants.LEFT);
		mainPanel.add(label, c);
		c.gridx = 2;
		label = new JLabel(two, SwingConstants.RIGHT);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setPreferredSize(new Dimension(225, label.getPreferredSize().height));
		label.setToolTipText(two);
		mainPanel.add(label, c);
		c.gridy += 1;
		c.gridx = 0;
		
		
	}
	
}
