package org.peakaboo.ui.swing.plotting.filters;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Window;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.framework.swidget.widgets.ClearPanel;

public class FiltersetViewer extends ClearPanel {

	
	private FilteringController controller;
	private CardLayout layout;
	
	private String EDIT = "EDIT";
	private String SELECT = "SELECT";
	
	@Override
	public String getName()
	{
		return "Data Filters";
	}
	
	public FiltersetViewer(FilteringController _controller, Window owner){
		
		super();
		
		setPreferredSize(new Dimension(200, getPreferredSize().height));
		
		this.controller = _controller;
		
		layout = new CardLayout();
		this.setLayout(layout);
		
		this.add(new FilterList(controller, owner, this), EDIT);
		this.add(new FilterSelectionList(controller, this), SELECT);
		
	}
	
	void showEditPane(){
		layout.show(this, EDIT);
	}
	void showSelectPane(){
		layout.show(this, SELECT);
	}
	
}
