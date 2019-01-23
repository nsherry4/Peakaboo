package org.peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.ui.swing.mapping.sidebar.FiltersPanel;
import org.peakaboo.ui.swing.mapping.sidebar.MapAppearancePanel;
import org.peakaboo.ui.swing.mapping.sidebar.MapDimensionsPanel;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingPanel;
import org.peakaboo.ui.swing.mapping.sidebar.MapSelectionPanel;

import cyclops.Coord;
import swidget.widgets.Spacing;



class MapperSidebar extends JPanel
{

	private MappingController		controller;
	private MapperPanel 			tabPanel;


	MapperSidebar(MapperPanel tabPanel, MappingController controller)
	{
		this.controller = controller;
		this.tabPanel = tabPanel;

		createControls();
	}


	private void createControls()
	{

		JTabbedPane tabs = new JTabbedPane();

		tabs.addTab("Settings", settingsTab());
		tabs.add("Fittings", fittingsTab());
		tabs.add("Filters", filtersTab());
		
		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);

	}

	private JPanel filtersTab() {
		return new FiltersPanel(controller.getFiltering(), tabPanel.getTabbedInterface().getWindow());
	}
	
	private JPanel fittingsTab() {
		JPanel tabFittings = new JPanel(new BorderLayout());
		tabFittings.setBorder(Spacing.bNone());
		tabFittings.add(new MapFittingPanel(controller.getSettings()), BorderLayout.CENTER);
		return tabFittings;
	}
	
	private JPanel settingsTab() {
		
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
		tabSettings.add(stylePanel(new MapAppearancePanel(controller)), maingbc);

		//Selection settings
		// map settings
		maingbc.gridy++;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		tabSettings.add(stylePanel(new MapSelectionPanel(controller)), maingbc);
		
		maingbc.gridy++;
		maingbc.weighty = 1f;
		tabSettings.add(Box.createVerticalGlue(), maingbc);
		
		return tabSettings;
		
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
		//noopo
	}


	
}
