package org.peakaboo.ui.swing.plotting.filters;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Window;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class FiltersetViewer extends ClearPanel {
	
	private FilteringController controller;
	private CardLayout layout;
	private FilterSettingsPanel settingsPanel;
	
	private static final String EDIT = "EDIT";
	private static final String SELECT = "SELECT";
	private static final String SETTINGS = "SETTINGS";
	
	@Override
	public String getName()
	{
		return "Data Filters";
	}
	
	public FiltersetViewer(FilteringController filteringController, Window owner){
		
		super();
		
		setPreferredSize(new Dimension(200, getPreferredSize().height));
		
		this.controller = filteringController;
		
		layout = new CardLayout();
		this.setLayout(layout);
		
		this.settingsPanel = new FilterSettingsPanel(controller, this);
		
		this.add(new FilterList(controller, owner, this), EDIT);
		this.add(new FilterSelectionList(controller, this), SELECT);
		this.add(this.settingsPanel, SETTINGS);
		
	}
	
	void showEditPane(){
		layout.show(this, EDIT);
	}
	void showSelectPane(){
		layout.show(this, SELECT);
	}
	void showSettingsPane(Filter filter) {
		this.settingsPanel.setFilter(filter);
		layout.show(this, SETTINGS);
	}
	
}
