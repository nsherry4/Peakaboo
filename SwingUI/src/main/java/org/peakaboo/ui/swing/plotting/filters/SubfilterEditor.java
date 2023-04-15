package org.peakaboo.ui.swing.plotting.filters;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoPanel;
import org.peakaboo.framework.autodialog.view.swing.editors.AbstractSwingEditor;
import org.peakaboo.framework.autodialog.view.swing.layouts.NarrowSwingLayout;
import org.peakaboo.framework.stratus.api.Spacing;



class SubfilterEditor extends AbstractSwingEditor<Filter> {
	
	//GUI
	private JPanel						control;
	private JComponent					subfilterView;
	private JPanel						subfilterPanel;
	private JComboBox<Filter>			filterCombo;
	
	//Subfilter
	private Filter 						subfilter;
	
	SubfilterEditor() {}
	
	@Override
	public void initialize(Parameter<Filter> p)
	{
		this.param = p;
		SelectionParameter<Filter> selparam = (SelectionParameter<Filter>) p;
		
		filterCombo = new JComboBox<>(selparam.getPossibleValues().toArray(new Filter[0]));
		control = new JPanel();
		control.setLayout(new BorderLayout());
		control.add(filterCombo, BorderLayout.NORTH);
		subfilterPanel = new JPanel();	
		subfilterPanel.setLayout(new BorderLayout());
		subfilterPanel.setBorder(Spacing.bNone());
		control.add(subfilterPanel, BorderLayout.CENTER);
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);
		
		filterCombo.addActionListener(e -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!param.setValue((Filter)filterCombo.getSelectedItem())) {
				validateFailed();
			}
			changeFilter((Filter)filterCombo.getSelectedItem());
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
		subfilterPanel.setVisible(!f.getParameters().isEmpty());
		if (subfilterView != null) subfilterPanel.removeAll();
		
		//set the new filter, and hook into the top level parameter group to invalidate the parent 
		//filter when the subfilter changes
		subfilter = f;
		subfilter.getParameterGroup().getValueHook().addListener(o -> subfilterInvalidated());
		

		var group = f.getParameterGroup();
		var layout = new NarrowSwingLayout(200);
		layout.initialize(group);
		subfilterView = new SwingAutoPanel(group, false, layout);
		
	
		
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
	public void setEditorValue(Filter value)
	{
		//in cases where the model has changed the selected filter, we must find 
		//the filter object of the same class name in the combobox list and replace 
		//it with the new filter.
		String filterName = value.getClass().getName();
		for (int i = 0; i < filterCombo.getItemCount(); i++) {
			if (filterCombo.getItemAt(i).getClass().getName().equals(filterName)) {
				filterCombo.removeItemAt(i);
				filterCombo.insertItemAt(param.getValue(), i);
				filterCombo.setSelectedItem(param.getValue());
				break;
			}
		}

		changeFilter(value);
	}
	
	@Override
	public Filter getEditorValue()
	{
		return getFilter();
	}

	private void validateFailed() {
		setFromParameter();
	}
	
	private void subfilterInvalidated() {
		param.getValueHook().updateListeners(subfilter);
	}

	@Override
	protected void setEnabled(boolean enabled) {
		subfilterView.setEnabled(enabled);
	}

	
	
}
