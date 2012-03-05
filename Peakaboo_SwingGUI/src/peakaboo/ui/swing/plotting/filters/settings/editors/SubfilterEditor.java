package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;
import swidget.widgets.Spacing;
import eventful.swing.EventfulTypePanel;
import fava.functionable.FArray;
import fava.signatures.FnMap;


public class SubfilterEditor extends EventfulTypePanel<SubfilterEditor> implements Editor
{
	
	List<AbstractFilter> 	filters;
	SingleFilterView		subfilterView;
	AbstractFilter 			subfilter;
	Parameter				param;
	IFilteringController	controller;
	
	JComboBox				filterCombo;
	
	JPanel					subfilterPanel;
	
	public SubfilterEditor(Parameter param, AbstractFilter filter, final IFilteringController controller, SingleFilterView view)
	{
		
		Object[] options = param.possibleValues;
		AbstractFilter selectedFilter = param.filterValue();
		
		this.param = param;
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
		
		
		subfilterPanel = new JPanel();	
		subfilterPanel.setLayout(new BorderLayout());
		subfilterPanel.setBorder(new TitledBorder(""));
		
		
		
		add(subfilterPanel, BorderLayout.CENTER);
		
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
		return subfilter;
	}
	
	
	public void setFilter(AbstractFilter f)
	{
		changeFilter(f);
	}
	
	private void changeFilter(AbstractFilter f)
	{
		
		if (f == null) return;
		
		if (! filterCombo.getSelectedItem().equals(f)) filterCombo.setSelectedItem(f);
		
		subfilterPanel.setVisible(f.getParameters().size() != 0);
		
		
		if (subfilterView != null) subfilterPanel.removeAll();
		subfilter = f;
		subfilterView = new SingleFilterView(subfilter, controller, false);
		
		
		JScrollPane scroller = new JScrollPane(subfilterView);
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		subfilterPanel.add(scroller, BorderLayout.CENTER);
		
		validate();
		repaint();
	}
	

	
	@Override
	public boolean expandVertical()
	{
		return true;
	}

	@Override
	public boolean expandHorizontal()
	{
		return false;
	}

	@Override
	public Style getStyle()
	{
		return Style.LABEL_ON_TOP;
	}

	@Override
	public JComponent getComponent()
	{
		return this;
	}


	@Override
	public void setFromParameter()
	{
		changeFilter(param.filterValue());
	}
	
	@Override
	public Object getEditorValue()
	{
		return getFilter();
	}
	
	
}
