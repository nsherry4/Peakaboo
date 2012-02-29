package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;
import eventful.swing.EventfulTypePanel;
import fava.functionable.FArray;
import fava.signatures.FnMap;


public class SubfilterEditor extends EventfulTypePanel<SubfilterEditor>
{
	
	List<AbstractFilter> 	filters;
	SingleFilterView		filterView;
	AbstractFilter 			filter;
	IFilteringController	controller;
	
	JComboBox				filterCombo;
	
	JPanel					filterPanel;
	
	public SubfilterEditor(Parameter param, AbstractFilter filter, final IFilteringController controller, SingleFilterView view)
	{
		
		Object[] options = param.possibleValues;
		AbstractFilter selectedFilter = param.filterValue();
		
		this.controller = controller;
		
		//create one new filter of each kind which can be used to filter a subset
		filters = FArray.wrap(options).map(new FnMap<Object, AbstractFilter>(){

			public AbstractFilter f(Object o)
			{
				return (AbstractFilter)o;
			}}).toSink();
		
		setLayout(new BorderLayout());
		
		filterCombo = new JComboBox(filters.toArray());
		add(filterCombo, BorderLayout.NORTH);
		
		
		filterPanel = new JPanel();	
		filterPanel.setBorder(new TitledBorder(""));
		
		
		
		
		add(filterPanel, BorderLayout.CENTER);
		
		filter = selectedFilter;
		changeFilter(selectedFilter);
		
		
		filterCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				changeFilter((AbstractFilter)filterCombo.getSelectedItem());
				updateListeners(SubfilterEditor.this);
			}
		});

		param.setValue(getFilter());
		addListener(new ParamListener(param, filter, controller, view));
		
	}
	
	
	public AbstractFilter getFilter()
	{
		return filter;
	}
	
	
	public void setFilter(AbstractFilter f)
	{
		changeFilter(f);
	}
	
	private void changeFilter(AbstractFilter f)
	{
		
		if (f == null) return;
		
		if (! filterCombo.getSelectedItem().equals(f)) filterCombo.setSelectedItem(f);
		
		filterPanel.setVisible(f.getParameters().size() != 0);
		
		
		if (filterView != null) filterPanel.removeAll();
		filter = f;
		filterView = new SingleFilterView(filter, controller, false, false);
		filterPanel.add(filterView);
	}
	
	
	
}
