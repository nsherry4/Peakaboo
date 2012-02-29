package peakaboo.ui.swing.plotting.filters.settings;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import fava.functionable.FList;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import peakaboo.ui.swing.plotting.filters.settings.editors.EditorFactory;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;


public class SingleFilterView extends JPanel implements Scrollable
{

	private AbstractFilter			filter;
	private IFilteringController	controller;

	private JPanel					settingsPanel;

	private List<JComponent>		controls;
	private List<Parameter> 		paramslist;

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
	
	public void updateWidgetsEnabled()
	{
		for (int i = 0; i < controls.size(); i++)
		{
			controls.get(i).setEnabled(paramslist.get(i).enabled);
		}
	}


	private JPanel createSettingsPanel(boolean bigBorder)
	{

		//get a list of parameters
		paramslist = new FList<Parameter>(filter.getParameters().values()).reverse();
		controls = new ArrayList<JComponent>();
		
		Iterator<Parameter> params = paramslist.iterator();
		

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

			param = params.next();

			paramLabel = new JLabel(param.name);
			paramLabel.setFont(paramLabel.getFont().deriveFont(Font.PLAIN));
			paramLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			component = EditorFactory.createEditor(param, filter, controller, this);
			
			c.gridy += 1;
			c.gridx = 0;
			c.weightx = 1.0;
			c.anchor = GridBagConstraints.LINE_START;
			if (component != null)
			{
				
				if (param.type == ValueType.CODE) {
				
					c.gridwidth = 2;
					
					panel.add(paramLabel, c);
					c.gridy++;
					panel.add(component, c);
					
					c.gridwidth = 1;
					
				} else if (param.type != ValueType.FILTER && param.type != ValueType.SEPARATOR) {
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

		panel.doLayout();

		if (bigBorder) {
			panel.setBorder(Spacing.bHuge());
		} else {
			panel.setBorder(Spacing.bNone());
		}

		return panel;

	}

	
	////////////////////////////////////
	// SCROLLABLE INTERFACE
	////////////////////////////////////
	
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 500));
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 50;
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2)
	{
		return 5;
	}

}


