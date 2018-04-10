package peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.swing.mapping.sidebar.MapContourPanel;
import peakaboo.ui.swing.mapping.sidebar.MapDimensionsPanel;
import peakaboo.ui.swing.mapping.sidebar.MapFittingPanel;
import peakaboo.ui.swing.mapping.sidebar.MapSelectionPanel;
import plural.streams.StreamExecutor;
import plural.streams.swing.StreamExecutorPanel;
import plural.streams.swing.StreamExecutorView;
import scitypes.Coord;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;
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
	
	
		
		JPanel tabSettings = new JPanel(new GridBagLayout());
		
		GridBagConstraints maingbc = new GridBagConstraints();
		maingbc.insets = Spacing.iTiny();
		maingbc.ipadx = 0;
		maingbc.ipady = 0;
		maingbc.weightx = 1;

		
		// map settings
		maingbc.gridx = 0;
		maingbc.gridy = 0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		tabSettings.add(new MapDimensionsPanel(tabPanel, controller), maingbc);

		// map settings
		maingbc.gridy++;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		tabSettings.add(new MapContourPanel(controller), maingbc);

		//Selection settings
		// map settings
		maingbc.gridy++;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		tabSettings.add(new MapSelectionPanel(controller), maingbc);
		
		maingbc.gridy++;
		maingbc.weighty = 1f;
		tabSettings.add(Box.createVerticalGlue(), maingbc);
		
		tabs.addTab("Map Settings", tabSettings);

		
		
		
		
		JPanel tabFittings = new JPanel(new BorderLayout());
		tabFittings.setBorder(Spacing.bSmall());
		tabFittings.add(new MapFittingPanel(controller.getSettings()), BorderLayout.CENTER);
		tabs.add("Peak Fittings", tabFittings);
		
		
		
		
		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);

	}



	
}
