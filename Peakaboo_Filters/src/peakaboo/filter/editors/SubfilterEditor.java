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

import peakaboo.filter.AbstractFilter;
import swidget.widgets.Spacing;
import autodialog.model.Parameter;
import autodialog.view.AutoPanel;
import autodialog.view.editors.IEditor;
import eventful.Eventful;



public class SubfilterEditor extends Eventful implements IEditor<AbstractFilter>
{
	
	//Param containing the subfilter
	Parameter<AbstractFilter>	param;
	
	//GUI
	JPanel						control;
	AutoPanel					subfilterView;
	JPanel						subfilterPanel;
	JComboBox<AbstractFilter>	filterCombo;
	
	//Subfilter
	List<AbstractFilter>		availableFilters;
	AbstractFilter 				subfilter;
	
	public SubfilterEditor(List<AbstractFilter> filters) {
		availableFilters = filters;
	}
	
	@Override
	public void initialize(Parameter<AbstractFilter> param)
	{
		
		AbstractFilter selectedFilter = (AbstractFilter)param.getValue();
		
		this.param = param;
		control = new JPanel();
				
		control.setLayout(new BorderLayout());
		
		filterCombo = new JComboBox<AbstractFilter>(availableFilters.toArray(new AbstractFilter[0]));
		control.add(filterCombo, BorderLayout.NORTH);
		
		
		subfilterPanel = new JPanel();	
		subfilterPanel.setLayout(new BorderLayout());
		subfilterPanel.setBorder(new TitledBorder(""));
		
		
		
		control.add(subfilterPanel, BorderLayout.CENTER);
		
		changeFilter(selectedFilter);
		
		
		filterCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				changeFilter((AbstractFilter)filterCombo.getSelectedItem());
				updateListeners();
			}
		});

		param.setValue(getFilter());

		
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
		
		//If the combobox doesn't match this filter, change it
		if (! filterCombo.getSelectedItem().equals(f)) filterCombo.setSelectedItem(f);
		
		//If this filter has parameters, show it, and remove all previous widgets
		subfilterPanel.setVisible(f.getParameters().size() != 0);
		if (subfilterView != null) subfilterPanel.removeAll();
		
		//set the new filter
		subfilter = f;
		
		
		//subfilterView = new SingleFilterView(subfilter, controller, false);
		FilterDialogController filerDialogController = new FilterDialogController(f, new IFilterChangeListener() {
			
			@Override
			public void change() {
				SubfilterEditor.this.updateListeners();
			}
		});
		subfilterView = AutoPanel.panel(filerDialogController);
		
		
		
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
		changeFilter((AbstractFilter)param.getValue());
	}
	
	@Override
	public AbstractFilter getEditorValue()
	{
		return getFilter();
	}

	@Override
	public void validateFailed() {
		setFromParameter();
	}
	
	
}
