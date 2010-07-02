package peakaboo.ui.swing.plotting.filters;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTable;

import peakaboo.controller.plotter.FilterController;
import peakaboo.filters.AbstractFilter;
import swidget.containers.SwidgetContainer;
import swidget.icons.IconSize;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;

class FilterEditButtonEditor extends DefaultCellEditor
{

	protected ImageButton		button;
	private SwidgetContainer			owner;

	private AbstractFilter	filter;
	private FilterController	controller;
	
	private String			label;
	private boolean			isPushed;


	public FilterEditButtonEditor(FilterController controller, SwidgetContainer owner)
	{
		super(new JCheckBox());

		this.controller = controller;
		this.owner = owner;

		button = new ImageButton("preferences", "…", Layout.IMAGE, IconSize.TOOLBAR_SMALL);
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				fireEditingStopped();
			}
		});
		
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{

		filter = (AbstractFilter) value;
		
		if (isSelected) {
			// button.setForeground(table.getSelectionForeground());
			// button.setBackground(table.getSelectionBackground());
		} else {
			// button.setForeground(table.getForeground());
			// button.setBackground(table.getBackground());
		}
		label = (value == null) ? "" : value.toString();
		isPushed = true;
		return button;
	}


	@Override
	public Object getCellEditorValue()
	{
		if (isPushed) {
			new FilterEditDialogue(filter, controller, owner);
		}
		isPushed = false;
		return new String(label);
	}


	@Override
	public boolean stopCellEditing()
	{
		isPushed = false;
		return super.stopCellEditing();
	}


	@Override
	protected void fireEditingStopped()
	{
		super.fireEditingStopped();
	}
}