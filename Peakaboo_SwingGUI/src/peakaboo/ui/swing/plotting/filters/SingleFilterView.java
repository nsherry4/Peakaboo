package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JToolTip;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fava.Fn;
import fava.Functions;

import peakaboo.controller.plotter.FilterController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.JMultiLineToolTip;
import swidget.widgets.Spacing;
import swidget.widgets.ImageButton.Layout;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.gradientpanel.TitleGradientPanel.Side;


public class SingleFilterView extends JPanel
{

	private AbstractFilter	filter;
	private FilterController	controller;

	private JPanel			settingsPanel;


	public SingleFilterView(AbstractFilter filter, FilterController controller)
	{

		super(new BorderLayout());


		this.filter = filter;
		this.controller = controller;

		settingsPanel = createSettingsPanel();
		settingsPanel.setOpaque(false);

		ImageButton info = new ImageButton(StockIcon.BADGE_INFO, "Info", "", Layout.IMAGE, false, IconSize.BUTTON);
		
		
		info.setToolTipText(filter.getFilterDescription());
		
		//info.setToolTipText("<html>" + filter.getFilterDescription() + "</html>");
		info.setBorder(Spacing.bMedium());

		
		TitleGradientPanel panel = new TitleGradientPanel(filter.getFilterName() + " Filter", true);
		panel.addSideComponent(info, Side.LEFT);
		this.add(panel, BorderLayout.NORTH);
		
		
		this.add(settingsPanel, BorderLayout.CENTER);
		// this.add(Box.createHorizontalStrut(15), BorderLayout.WEST);


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


	private JPanel createSettingsPanel()
	{

		//get a list of parameters
		final List<Parameter<?>> paramslist = Fn.map( filter.getParameters().values(), Functions.<Parameter<?>>id());
		
				
		
		Iterator<Parameter<?>> params = paramslist.iterator();
		
		
		final List<JComponent> controls = DataTypeFactory.<JComponent>list();

		class ParamListener implements ActionListener, ChangeListener
		{

			private Parameter<Object>	param;


			public ParamListener(Parameter<Object> param)
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

				if (param.type == ValueType.INTEGER || param.type == ValueType.REAL) {
					param.setValue(  ((JSpinner) source).getValue()  );
				} else if (param.type == ValueType.SET_ELEMENT) {
					param.setValue(  ((JComboBox) source).getSelectedItem()  );
				} else if (param.type == ValueType.BOOLEAN) {
					param.setValue(  ((JCheckBox) source).isSelected()  );
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
					if (param.type == ValueType.INTEGER) {
						((JSpinner) source).setValue(((Integer) param.getValue()).intValue());
					} else if (param.type == ValueType.REAL) {
						((JSpinner) source).setValue(((Double) param.getValue()).doubleValue());
					} else if (param.type == ValueType.SET_ELEMENT) {
						((JComboBox) source).setSelectedItem(param.getValue());
					} else if (param.type == ValueType.BOOLEAN) {
						((JCheckBox) source).setSelected(((Boolean) param.getValue()).booleanValue());
					}
					
				}



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
		Parameter<Object> param;
		
		while (params.hasNext()) {
			c.gridy += 1;
			c.gridx = 0;
			c.weightx = 1.0;
			c.anchor = GridBagConstraints.LINE_START;

			param = (Parameter<Object>)params.next();

			paramLabel = new JLabel(param.name);
			paramLabel.setFont(paramLabel.getFont().deriveFont(Font.PLAIN));
			paramLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(paramLabel, c);

			c.gridx = 1;
			c.weightx = 0;
			c.anchor = GridBagConstraints.LINE_END;

			
			component = null;
			if (param.type == ValueType.INTEGER) {

				JSpinner spinner = new JSpinner();
				spinner.getEditor().setPreferredSize(new Dimension(50, spinner.getEditor().getPreferredSize().height));
				spinner.setValue(param.getValue());

				spinner.addChangeListener(new ParamListener(param));

				component = spinner;

			} else if (param.type == ValueType.REAL) {

				JSpinner spinner = new JSpinner();
				spinner.setModel(new SpinnerNumberModel(((Double)param.getValue()).doubleValue(), Double.MIN_VALUE, Double.MAX_VALUE, 0.01));				
				spinner.getEditor().setPreferredSize(new Dimension(50, spinner.getEditor().getPreferredSize().height));
				
				spinner.addChangeListener(new ParamListener(param));

				component = spinner;

			} else if (param.type == ValueType.SET_ELEMENT) {

				Enum<?>[] enumValues = (Enum<?>[]) param.possibleValues;

				JComboBox enumCombo = new JComboBox(enumValues);

				enumCombo.setSelectedItem(param.getValue());
				enumCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				enumCombo.addActionListener(new ParamListener(param));
				
				component = enumCombo;

			} else if (param.type == ValueType.BOOLEAN) {

				// JSpinner spinner = new JSpinner();
				JCheckBox check = new JCheckBox();
				check.setSelected((Boolean) param.getValue());
				check.setAlignmentX(Component.LEFT_ALIGNMENT);
				check.setOpaque(false);
				
				check.addChangeListener(new ParamListener(param));
				
				component = check;
			}
			
			if (component != null)
			{
				panel.add(component, c);
				component.setEnabled(param.enabled);
				controls.add(component);
			}

		}


		panel.setBorder(Spacing.bHuge());

		return panel;

	}

}
