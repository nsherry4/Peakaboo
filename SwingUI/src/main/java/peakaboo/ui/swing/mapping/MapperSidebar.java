package peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import cyclops.Coord;
import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.swing.mapping.sidebar.ConcentrationsPanel;
import peakaboo.ui.swing.mapping.sidebar.MapContourPanel;
import peakaboo.ui.swing.mapping.sidebar.MapDimensionsPanel;
import peakaboo.ui.swing.mapping.sidebar.MapFittingPanel;
import peakaboo.ui.swing.mapping.sidebar.MapSelectionPanel;
import swidget.widgets.Spacing;



class MapperSidebar extends JPanel
{

	private MappingController		controller;
	private MapperPanel 			tabPanel;
	private ConcentrationsPanel concentrations;


	MapperSidebar(MapperPanel tabPanel, MappingController controller)
	{
		this.controller = controller;
		this.tabPanel = tabPanel;

		createControls();
	}


	private void createControls()
	{

		JTabbedPane tabs = new JTabbedPane();
	
	
		
		JPanel tabSettings = new JPanel(new GridBagLayout());
		
		GridBagConstraints maingbc = new GridBagConstraints();
		maingbc.insets = new Insets(Spacing.large, Spacing.small, Spacing.small, Spacing.small);
		maingbc.ipadx = 0;
		maingbc.ipady = 0;
		maingbc.weightx = 1;

		
		// map settings
		maingbc.gridx = 0;
		maingbc.gridy = 0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		tabSettings.add(stylePanel(new MapDimensionsPanel(tabPanel, controller)), maingbc);

		// map settings
		maingbc.gridy++;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		tabSettings.add(stylePanel(new MapContourPanel(controller)), maingbc);

		//Selection settings
		// map settings
		maingbc.gridy++;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		tabSettings.add(stylePanel(new MapSelectionPanel(controller)), maingbc);
		
		maingbc.gridy++;
		maingbc.weighty = 1f;
		tabSettings.add(Box.createVerticalGlue(), maingbc);
		
		tabs.addTab("Map Settings", tabSettings);

		
		
		
		
		JPanel tabFittings = new JPanel(new BorderLayout());
		tabFittings.setBorder(Spacing.bNone());
		tabFittings.add(new MapFittingPanel(controller.getSettings()), BorderLayout.CENTER);
		tabs.add("Peak Fittings", tabFittings);
		
		
		if (!controller.getSettings().getMapFittings().getCalibrationProfile().isEmpty()) {
			JPanel outer = new JPanel(new GridBagLayout());
			outer.setBorder(Spacing.bNone());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.NORTHWEST;
			c.fill = GridBagConstraints.BOTH;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1f;
			c.weighty = 1f;
			
			concentrations = new ConcentrationsPanel(controller);
			outer.add(concentrations, c);
			tabs.add("Concentrations", outer);
		}
		
		
		
		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);

	}

	private JPanel stylePanel(JPanel panel) {
		JPanel inner = new JPanel(new BorderLayout());
		JPanel outer = new JPanel(new BorderLayout());
		
		TitledBorder titleBorder = new TitledBorder(panel.getName());
		titleBorder.setBorder(Spacing.bSmall());
		outer.setBorder(titleBorder);
		
		inner.setBorder(new EmptyBorder(Spacing.tiny, Spacing.medium, Spacing.tiny, Spacing.tiny));
		
		inner.add(panel, BorderLayout.CENTER);
		outer.add(inner, BorderLayout.CENTER);
		
		return outer;
	}


	public void showValueAtCoord(Coord<Integer> mapCoordinateAtPoint) {
		if (concentrations != null) {
			concentrations.show(mapCoordinateAtPoint);
		}
	}


	
}
