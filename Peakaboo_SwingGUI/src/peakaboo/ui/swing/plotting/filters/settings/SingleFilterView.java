package peakaboo.ui.swing.plotting.filters.settings;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import fava.functionable.FList;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.editors.EditorFactory;
import peakaboo.ui.swing.plotting.filters.settings.editors.Editor;
import peakaboo.ui.swing.plotting.filters.settings.editors.Editor.Style;
import swidget.widgets.Spacing;


public class SingleFilterView extends JPanel
{

	private AbstractFilter			filter;
	private IFilteringController	controller;

	private List<Editor>			editors;
	private List<Parameter> 		paramslist;

	public SingleFilterView(AbstractFilter filter, IFilteringController controller)
	{
		this(filter, controller, true);
	}
	
	public SingleFilterView(AbstractFilter filter, IFilteringController controller, boolean bigBorder)
	{

		super(new BorderLayout());
		
		this.filter = filter;
		this.controller = controller;

		createSettingsPanel(bigBorder);
		setOpaque(false);
		
	}


	@Override
	public String toString()
	{
		return filter.getFilterName();
	}


	public void showSettings(boolean show)
	{
		setVisible(show);
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

	private void createSettingsPanel(boolean bigBorder)
	{

		//get a list of parameters
		paramslist = new FList<Parameter>(filter.getParameters().values());
		editors = new ArrayList<Editor>();
		
		Iterator<Parameter> params = paramslist.iterator();
		
	
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);

		



		JLabel paramLabel;
		Editor editor;
		Parameter param;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1f;
		add(Box.createHorizontalGlue(), c);
		c.insets = Spacing.iSmall();
		
		boolean needsVerticalGlue = true;
		
		while (params.hasNext()) {

			param = params.next();

			paramLabel = new JLabel(param.name);
			paramLabel.setFont(paramLabel.getFont().deriveFont(Font.PLAIN));
			paramLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			editor = EditorFactory.createEditor(param, filter, controller, this);
			
			needsVerticalGlue &= (!editor.expandVertical());
			
			c.weighty = editor.expandVertical() ? 1f : 0f;
			c.weightx = editor.expandHorizontal() ? 1f : 0f;
			c.gridy += 1;
			c.gridx = 0;
			c.fill = GridBagConstraints.BOTH;
			
			c.anchor = GridBagConstraints.LINE_START;

			if (editor.getStyle() == Style.LABEL_ON_SIDE)
			{
				c.weightx = 0;
				add(paramLabel, c);
				
				c.weightx = editor.expandHorizontal() ? 1f : 0f;
				c.gridx++;
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.LINE_END;
				
				add(editor.getComponent(), c);
				
			}
			else if (editor.getStyle() == Style.LABEL_ON_TOP)
			{
				c.gridwidth = 2;
				
				c.weighty = 0f;
				add(paramLabel, c);

				c.gridy++;
				
				c.weighty = editor.expandVertical() ? 1f : 0f;
				add(editor.getComponent(), c);
				
				c.gridwidth = 1;
			}
			else if(editor.getStyle() == Style.LABEL_HIDDEN)
			{
				c.gridwidth = 2;				
				add(editor.getComponent(), c);
				c.gridwidth = 1;
			}

		}
		
		if (needsVerticalGlue)
		{
			c.gridy++;
			c.weighty = 1f;
			add(Box.createVerticalGlue(), c);
		}
		
		doLayout();

		if (bigBorder) {
			setBorder(Spacing.bHuge());
		} else {
			setBorder(Spacing.bNone());
		}
		

	}
	
	
	

}


