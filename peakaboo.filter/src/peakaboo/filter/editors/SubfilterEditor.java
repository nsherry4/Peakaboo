package peakaboo.filter.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import autodialog.model.Parameter;
import autodialog.view.AutoPanel;
import autodialog.view.editors.IEditor;
import eventful.Eventful;
import peakaboo.filter.model.AbstractFilter;
import peakaboo.filter.model.Filter;
import swidget.widgets.Spacing;



public class SubfilterEditor extends Eventful implements IEditor<Filter>
{
	
	//Param containing the subfilter
	Parameter<Filter>	param;
	
	//GUI
	JPanel						control;
	AutoPanel					subfilterView;
	JPanel						subfilterPanel;
	JComboBox<Filter>			filterCombo;
	
	//Subfilter
	List<Filter>				availableFilters;
	Filter 						subfilter;
	
	public SubfilterEditor(List<Filter> filters) {
		availableFilters = filters;
	}
	
	@Override
	public void initialize(Parameter<Filter> param)
	{
		
		Filter selectedFilter = (Filter)param.getValue();
		
		this.param = param;
		control = new JPanel();
				
		control.setLayout(new BorderLayout());
		
		filterCombo = new JComboBox<Filter>(availableFilters.toArray(new AbstractFilter[0]));
		control.add(filterCombo, BorderLayout.NORTH);
		
		
		subfilterPanel = new JPanel();	
		subfilterPanel.setLayout(new BorderLayout());
		subfilterPanel.setBorder(new TitledBorder(""));
		
		
		
		control.add(subfilterPanel, BorderLayout.CENTER);
		
		changeFilter(selectedFilter);
		
		
		filterCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				changeFilter((Filter)filterCombo.getSelectedItem());
				updateListeners();
			}
		});

		param.setValue(getFilter());

		
	}
	
	
	public Filter getFilter()
	{
		return subfilter;
	}
	
	
	public void setFilter(Filter f)
	{
		changeFilter(f);
	}
	
	private void changeFilter(Filter f)
	{
		
		if (f == null) return;
		
		//If the combobox doesn't match this filter, change it
		if (! filterCombo.getSelectedItem().equals(f)) filterCombo.setSelectedItem(f);
		
		//If this filter has parameters, show it, and remove all previous widgets
		subfilterPanel.setVisible(f.getParameters().size() != 0);
		if (subfilterView != null) subfilterPanel.removeAll();
		
		//set the new filter
		subfilter = f;
		
		
		//subfilterView = new SingleFilterView(subfilter, controller, false);
		FilterDialogController filerDialogController = new FilterDialogController(f){

			@Override
			public void parameterUpdated(Parameter<?> param) {
				SubfilterEditor.this.updateListeners();
			}};
		subfilterView = new AutoPanel(filerDialogController.getParameters());
	
		
		JScrollPane scroller = new JScrollPane(subfilterView);
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		subfilterPanel.add(scroller, BorderLayout.CENTER);
		
		control.validate();
		control.repaint();
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
	public LabelStyle getLabelStyle()
	{
		return LabelStyle.LABEL_ON_TOP;
	}

	@Override
	public JComponent getComponent()
	{
		return control;
	}


	@Override
	public void setFromParameter()
	{
		changeFilter((Filter)param.getValue());
	}
	
	@Override
	public Filter getEditorValue()
	{
		return getFilter();
	}

	@Override
	public void validateFailed() {
		setFromParameter();
	}
	
	
}
