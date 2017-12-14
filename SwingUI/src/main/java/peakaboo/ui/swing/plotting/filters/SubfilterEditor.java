package peakaboo.ui.swing.plotting.filters;

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
import autodialog.model.SelectionParameter;
import autodialog.view.editors.Editor;
import autodialog.view.swing.AutoPanel;
import autodialog.view.swing.editors.AbstractSwingEditor;
import autodialog.view.swing.editors.SwingEditorFactory;
import eventful.Eventful;
import peakaboo.filter.model.AbstractFilter;
import peakaboo.filter.model.Filter;
import swidget.widgets.Spacing;



public class SubfilterEditor extends AbstractSwingEditor<Filter> 
{
	
	//Param containing the subfilter
	SelectionParameter<Filter>	param;
	
	//GUI
	JPanel						control;
	AutoPanel					subfilterView;
	JPanel						subfilterPanel;
	JComboBox<Filter>			filterCombo;
	
	//Subfilter
	Filter 						subfilter;
	
	public SubfilterEditor() {}
	
	@Override
	public void initialize(Parameter<Filter> p)
	{
		this.param = (SelectionParameter<Filter>) p;
		
		filterCombo = new JComboBox<Filter>(param.getPossibleValues().toArray(new AbstractFilter[0]));
		control = new JPanel();
		control.setLayout(new BorderLayout());
		control.add(filterCombo, BorderLayout.NORTH);
		subfilterPanel = new JPanel();	
		subfilterPanel.setLayout(new BorderLayout());
		subfilterPanel.setBorder(new TitledBorder(""));
		control.add(subfilterPanel, BorderLayout.CENTER);
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		
		
		filterCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				changeFilter((Filter)filterCombo.getSelectedItem());
				getValueHook().updateListeners(getEditorValue());
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
				SubfilterEditor.this.getValueHook().updateListeners(f);
			}};
		subfilterView = new AutoPanel(filerDialogController.getEditors());
	
		
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
		
		//in cases where the model has changed the selected filter, we must find 
		//the filter object of the same class name in the combobox list and replace 
		//it with the new filter.
		String filterName = param.getValue().getClass().getName();
		for (int i = 0; i < filterCombo.getItemCount(); i++) {
			if (filterCombo.getItemAt(i).getClass().getName().equals(filterName)) {
				filterCombo.removeItemAt(i);
				filterCombo.insertItemAt(param.getValue(), i);
				filterCombo.setSelectedItem(param.getValue());
				break;
			}
		}

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

	@Override
	public Parameter<Filter> getParameter() {
		return param;
	}
	
	
}
