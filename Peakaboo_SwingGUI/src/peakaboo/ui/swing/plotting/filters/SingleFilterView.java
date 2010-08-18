package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIDefaults;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eventful.EventfulListener;
import eventful.EventfulTypeListener;
import eventful.swing.EventfulPanel;
import eventful.swing.EventfulTypePanel;
import fava.Fn;
import fava.Functions;
import fava.lists.FList;
import fava.signatures.FnMap;

import peakaboo.common.DataTypeFactory;
import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.AvailableFilters;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import peakaboo.filter.filters.advanced.SegmentFilter;
import sun.net.www.http.Hurryable;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;


public class SingleFilterView extends JPanel
{

	private AbstractFilter	filter;
	private IFilteringController	controller;

	private JPanel			settingsPanel;


	public SingleFilterView(AbstractFilter filter, IFilteringController controller)
	{
		this(filter, controller, true, true);
	}
	
	public SingleFilterView(AbstractFilter filter, IFilteringController controller, boolean showTitle, boolean bigBorder)
	{

		super(new BorderLayout());
		

		this.filter = filter;
		this.controller = controller;

		settingsPanel = createSettingsPanel(bigBorder);
		settingsPanel.setOpaque(false);
		
		

		if (showTitle) {
			TitleGradientPanel panel = new TitleGradientPanel(filter.getFilterName() + " Filter", true);
			panel.setToolTipText(filter.getFilterDescription());
			this.add(panel, BorderLayout.NORTH);
		}
		
		
		this.add(settingsPanel, BorderLayout.CENTER);


	}


	@Override
	public String toString()
	{
		return filter.getFilterName();
	}


	public void showSettings(boolean show)
	{
		settingsPanel.setVisible(show);
	}


	private JPanel createSettingsPanel(boolean bigBorder)
	{

		//get a list of parameters
		final List<Parameter> paramslist = Fn.map( filter.getParameters().values(), Functions.<Parameter>id());
		
				
		
		Iterator<Parameter> params = paramslist.iterator();
		
		
		final List<JComponent> controls = DataTypeFactory.<JComponent>list();

		class ParamListener implements ActionListener, ChangeListener, EventfulTypeListener<SubfilterView>
		{

			private Parameter	param;


			public ParamListener(Parameter param)
			{
				this.param = param;
			}


			public void actionPerformed(ActionEvent e)
			{

				update(e.getSource());
			}


			public void stateChanged(ChangeEvent e)
			{

				update(e.getSource());
			}


			public void update(Object source)
			{
				
				Object oldValue = param.getValue();

				switch (param.type)
				{
					case INTEGER:
					case REAL:
						param.setValue(  ((JSpinner) source).getValue()  );
						break;
						
					case SET_ELEMENT:
						param.setValue(  ((JComboBox) source).getSelectedItem()  );
						break;
						
					case BOOLEAN:
						param.setValue(  ((JCheckBox) source).isSelected()  );
						break;
						
					case FILTER:
						param.setValue(  ((SubfilterView)source).getFilter()  );
						break;
				}
				
				// if this input validates, signal the change, otherwise, reset
				// the value
				if (filter.validateParameters()) {
					
					controller.filteredDataInvalidated();
					
					for (int i = 0; i < controls.size(); i++)
					{
						controls.get(i).setEnabled(paramslist.get(i).enabled);
					}
					
				} else {
					param.setValue(oldValue);

					// reset the control to the value of the parameter
					switch (param.type)
					{
						case INTEGER:
							((JSpinner) source).setValue(param.intValue());
							break;
							
						case REAL:
							((JSpinner) source).setValue(param.realValue());
							break;
							
						case SET_ELEMENT:
							((JComboBox) source).setSelectedItem(param.getValue());
							break;
							
						case BOOLEAN:
							((JCheckBox) source).setSelected(param.boolValue());
							break;
							
						case FILTER:
							((SubfilterView)source).setFilter(param.filterValue());
							break;
					}
					
				}



			}


			public void change(SubfilterView message)
			{
				update(message);
			}

		}

		

		// JPanel panel = new JPanel(new SpringLayout());
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(layout);

		

		c.weighty = 0;
		c.gridx = 0;
		c.gridy = -1;
		c.fill = GridBagConstraints.NONE;

		c.insets = Spacing.iSmall();

		JLabel paramLabel;
		JComponent component;
		Parameter param;
		
		while (params.hasNext()) {
			c.gridy += 1;
			c.gridx = 0;
			c.weightx = 1.0;
			c.anchor = GridBagConstraints.LINE_START;

			param = params.next();

			paramLabel = new JLabel(param.name);
			paramLabel.setFont(paramLabel.getFont().deriveFont(Font.PLAIN));
			paramLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			



			
			component = null;
			JSpinner spinner;
			
			//generate the control which will display the value for this filter
			switch (param.type)
			{
				case INTEGER:

					spinner = new JSpinner();
					spinner.getEditor().setPreferredSize(new Dimension(70, spinner.getEditor().getPreferredSize().height));
					spinner.setValue(param.intValue());
	
					spinner.addChangeListener(new ParamListener(param));
	
					component = spinner;
					
					break;

				case REAL:

					spinner = new JSpinner();
					spinner.setModel(new SpinnerNumberModel(param.realValue(), null, null, 0.1));
					spinner.getEditor().setPreferredSize(new Dimension(70, spinner.getEditor().getPreferredSize().height));
					spinner.setValue(param.realValue());
					
					spinner.addChangeListener(new ParamListener(param));
	
					component = spinner;

					break;
					
				case SET_ELEMENT:

					Enum<?>[] enumValues = (Enum<?>[]) param.possibleValues;
	
					JComboBox enumCombo = new JComboBox(enumValues);
	
					enumCombo.setSelectedItem(param.getValue());
					enumCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
					
					enumCombo.addActionListener(new ParamListener(param));
					
					component = enumCombo;
					
					break;

				case BOOLEAN:

					JCheckBox check = new JCheckBox();
					check.setSelected(param.boolValue());
					check.setAlignmentX(Component.LEFT_ALIGNMENT);
					check.setOpaque(false);
					
					check.addChangeListener(new ParamListener(param));
					
					component = check;
					
					break;
					
					
				case FILTER:
					
					final SubfilterView subfilterView = new SubfilterView(param.filterValue(), controller, param.possibleValues);
					param.setValue(subfilterView.getFilter());
					subfilterView.addListener(new ParamListener(param));
					
					component = subfilterView;
					
					
			}
			
			
			if (component != null)
			{
				
				if (param.type != ValueType.FILTER)
				{
					panel.add(paramLabel, c);
					
					c.gridx++;
					c.weightx = 0;
					c.fill = GridBagConstraints.NONE;
					c.anchor = GridBagConstraints.LINE_END;
					
					panel.add(component, c);
					
				} else {
					
					c.gridwidth = 2;
					c.fill = GridBagConstraints.HORIZONTAL;
					
					panel.add(component, c);
					
					c.gridwidth = 1;
					
				}
				

				
				component.setEnabled(param.enabled);
				controls.add(component);
			}
			
			

		}


		if (bigBorder) {
			panel.setBorder(Spacing.bHuge());
		} else {
			panel.setBorder(Spacing.bNone());
		}

		return panel;

	}

}


class SubfilterView extends EventfulTypePanel<SubfilterView>
{
	
	List<AbstractFilter> 	filters;
	SingleFilterView		filterView;
	AbstractFilter 			filter;
	IFilteringController	controller;
	
	JComboBox				filterCombo;
	
	JPanel					filterPanel;
	
	public SubfilterView(AbstractFilter selectedFilter, final IFilteringController controller, Object[] options)
	{
		
		this.controller = controller;
		
		//create one new filter of each kind which can be used to filter a subset
		filters = Fn.map(options, new FnMap<Object, AbstractFilter>(){

			public AbstractFilter f(Object o)
			{
				return (AbstractFilter)o;
			}});
		
		setLayout(new BorderLayout());
		
		filterCombo = new JComboBox(filters.toArray());
		add(filterCombo, BorderLayout.NORTH);
		
		
		filterPanel = new JPanel();	
		TitledBorder tb = new TitledBorder("");		
		filterPanel.setBorder(new TitledBorder(""));
		
		
		
		
		add(filterPanel, BorderLayout.CENTER);
		
		filter = selectedFilter;
		changeFilter(selectedFilter);
		
		
		filterCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				changeFilter((AbstractFilter)filterCombo.getSelectedItem());
				updateListeners(SubfilterView.this);
			}
		});

		
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
		
		filterPanel.setVisible(f.parameters.size() != 0);
		
		
		if (filterView != null) filterPanel.removeAll();
		filter = f;
		filterView = new SingleFilterView(filter, controller, false, false);
		filterPanel.add(filterView);
	}
	
	
	
}
