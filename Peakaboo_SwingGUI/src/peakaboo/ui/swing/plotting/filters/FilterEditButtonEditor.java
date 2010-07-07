package peakaboo.ui.swing.plotting.filters;



import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import peakaboo.controller.plotter.FilterController;
import peakaboo.filters.AbstractFilter;
import swidget.containers.SwidgetContainer;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;



class FilterEditButtonEditor extends DefaultCellEditor
{

	protected ImageButton		button;
	private JPanel 				noParams;
	
	private SwidgetContainer	owner;

	private AbstractFilter		filter;
	private FilterController	controller;

	private String				label;
	private boolean				isPushed;


	public FilterEditButtonEditor(FilterController controller, SwidgetContainer owner)
	{
		super(new JCheckBox());

		this.controller = controller;
		this.owner = owner;

		button = new ImageButton(StockIcon.MISC_PREFERENCES, "…", Layout.IMAGE, IconSize.TOOLBAR_SMALL);
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				fireEditingStopped();
			}
		});
		
		noParams = new ClearPanel();

	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object _filter, boolean isSelected, int row, int column)
	{

		filter = (AbstractFilter) _filter;
		int numParameters = filter.getParameters().size();
		
		label = (_filter == null) ? "" : _filter.toString();
		isPushed = true;
		
		if (numParameters == 0)
		{
			
			if (isSelected) {
				noParams.setBackground(table.getSelectionBackground());
				noParams.setOpaque(true);
			} else {
				noParams.setOpaque(false);
			}
			
			return noParams;
		}
		return button;
	}


	@Override
	public Object getCellEditorValue()
	{
		if (isPushed)
		{
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