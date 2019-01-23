package org.peakaboo.ui.swing.plotting.filters;



import java.awt.Component;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;

import net.sciencestudio.autodialog.view.editors.AutoDialogButtons;
import net.sciencestudio.autodialog.view.swing.SwingAutoDialog;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonLayout;



class EditButtonEditor extends DefaultCellEditor
{

	private ImageButton				button;
	private JPanel 					container;
	
	private Window					owner;

	private Filter					filter;
	private FilteringController		controller;

	private String					label;
	private boolean					isPushed;
	
	private Map<Filter, SwingAutoDialog> settingsDialogs;


	public EditButtonEditor(FilteringController controller, Window owner)
	{
		super(new JCheckBox());

		this.controller = controller;
		this.owner = owner;
		this.settingsDialogs = new HashMap<>();

		button = new ImageButton(StockIcon.MISC_PREFERENCES, IconSize.TOOLBAR_SMALL).withTooltip("Edit Filter").withLayout(ImageButtonLayout.IMAGE).withBordered(false);
		button.addActionListener(e -> fireEditingStopped());
		button.setOpaque(false);
		
		container = new JPanel();
		container.setBorder(Spacing.bNone());
		
		

	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object _filter, boolean isSelected, int row, int column)
	{

		filter = (Filter) _filter;
		int numParameters = (_filter == null) ? 0 : filter.getParameters().size();
		
		label = (_filter == null) ? "" : _filter.toString();
		isPushed = true;
		
		
		container.setBackground(table.getSelectionBackground());
		container.setOpaque(true);

		
		container.remove(button);
		if (numParameters == 0)
		{		
			return container;
		}
		container.add(button);
		return container;
	}


	@Override
	public Object getCellEditorValue()
	{
		if (isPushed)
		{
			FilterDialog dialog;

			if (!settingsDialogs.containsKey(filter)) {
				
				dialog = new FilterDialog(controller, filter, AutoDialogButtons.CLOSE, owner);	
				dialog.setHelpMessage(filter.getFilterDescription());
				dialog.setHelpTitle(filter.getFilterName());
				settingsDialogs.put(filter, dialog);
				dialog.initialize();
			} else {
				settingsDialogs.get(filter).setVisible(true);
			}

		}
		isPushed = false;
		return label;
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