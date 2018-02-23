package peakaboo.ui.swing.plotting.filters;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;

import peakaboo.controller.plotter.filtering.FilteringController;
import swidget.widgets.ClearPanel;

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
	
	public FiltersetViewer(FilteringController _controller, Container owner){
		
		super();
		
		setPreferredSize(new Dimension(200, getPreferredSize().height));
		
		this.controller = _controller;
		
		layout = new CardLayout();
		this.setLayout(layout);
		
		this.add(new FilterList(controller, owner, this), EDIT);
		this.add(new FilterSelectionList(controller, this), SELECT);
		
	}
	
	public void showEditPane(){
		layout.show(this, EDIT);
	}
	public void showSelectPane(){
		layout.show(this, SELECT);
	}
	
}
