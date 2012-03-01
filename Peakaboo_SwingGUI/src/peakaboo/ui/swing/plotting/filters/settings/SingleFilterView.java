package peakaboo.ui.swing.plotting.filters.settings;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fava.functionable.FList;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.editors.EditorFactory;
import peakaboo.ui.swing.plotting.filters.settings.editors.Editor;
import peakaboo.ui.swing.plotting.filters.settings.editors.Editor.Style;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;


public class SingleFilterView extends JPanel
{

	private AbstractFilter			filter;
	private IFilteringController	controller;

	private JPanel					settingsPanel;

	private List<Editor>			editors;
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
		
		JScrollPane scroller = new JScrollPane(settingsPanel);
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		

		if (showTitle) 
		{
			TitleGradientPanel panel = new TitleGradientPanel(filter.getFilterName() + " Filter", true);			
			panel.setToolTipText(filter.getFilterDescription());
			this.add(panel, BorderLayout.NORTH);	
		}
		
		
		this.add(scroller, BorderLayout.CENTER);


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
		for (int i = 0; i < editors.size(); i++)
		{
			editors.get(i).getComponent().setEnabled(paramslist.get(i).enabled);
		}
	}

	public void updateFromParameters()
	{
		for (Editor editor: editors)
		{
			editor.setFromParameter();
		}
	}

	private JPanel createSettingsPanel(boolean bigBorder)
	{

		//get a list of parameters
		paramslist = new FList<Parameter>(filter.getParameters().values());
		editors = new ArrayList<Editor>();
		
		Iterator<Parameter> params = paramslist.iterator();
		

		JPanel panel = new ParametersPanel();
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(layout);

		



		JLabel paramLabel;
		Editor editor;
		Parameter param;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1f;
		panel.add(Box.createHorizontalGlue(), c);
		c.insets = Spacing.iSmall();
		
		while (params.hasNext()) {

			param = params.next();

			paramLabel = new JLabel(param.name);
			paramLabel.setFont(paramLabel.getFont().deriveFont(Font.PLAIN));
			paramLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			editor = EditorFactory.createEditor(param, filter, controller, this);
			
			c.weighty = editor.getVerticalWeight();
			c.weightx = editor.expandHorizontal() ? 1f : 0f;
			c.gridy += 1;
			c.gridx = 0;
			c.fill = GridBagConstraints.BOTH;
			
			c.anchor = GridBagConstraints.LINE_START;

			if (editor.getStyle() == Style.LABEL_ON_SIDE)
			{
				c.weightx = 0;
				panel.add(paramLabel, c);
				
				c.weightx = editor.expandHorizontal() ? 1f : 0f;
				c.gridx++;
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.LINE_END;
				
				panel.add(editor.getComponent(), c);
				
			}
			else if (editor.getStyle() == Style.LABEL_ON_TOP)
			{
				c.gridwidth = 2;
				
				c.weighty = 0f;
				panel.add(paramLabel, c);

				c.gridy++;
				
				c.weighty = editor.getVerticalWeight();
				panel.add(editor.getComponent(), c);
				
				c.gridwidth = 1;
			}
			else if(editor.getStyle() == Style.LABEL_HIDDEN)
			{
				c.gridwidth = 2;				
				panel.add(editor.getComponent(), c);
				c.gridwidth = 1;
			}

		}
		
		c.gridy++;
		c.weighty = 1f;
		//panel.add(Box.createVerticalGlue(), c);
		
		
		panel.doLayout();

		if (bigBorder) {
			panel.setBorder(Spacing.bHuge());
		} else {
			panel.setBorder(Spacing.bNone());
		}

		return panel;

	}

}


