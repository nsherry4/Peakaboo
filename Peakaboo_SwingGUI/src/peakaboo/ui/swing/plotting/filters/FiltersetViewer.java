package peakaboo.ui.swing.plotting.filters;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;

import peakaboo.filter.controller.IFilteringController;
import swidget.widgets.ClearPanel;

public class FiltersetViewer extends ClearPanel {

	
	private IFilteringController controller;
	private CardLayout layout;
	
	private String EDIT = "EDIT";
	private String SELECT = "SELECT";
	
	@Override
	public String getName()
	{
		return "Filters";
	}
	
	public FiltersetViewer(IFilteringController _controller, Container owner){
		
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
