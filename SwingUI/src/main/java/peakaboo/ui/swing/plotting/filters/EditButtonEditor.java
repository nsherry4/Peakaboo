package peakaboo.ui.swing.plotting.filters;



import static java.util.stream.Collectors.toList;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sciencestudio.autodialog.view.editors.AutoDialogButtons;
import net.sciencestudio.autodialog.view.swing.SwingAutoDialog;
import peakaboo.controller.plotter.filtering.FilteringController;
import peakaboo.filter.model.Filter;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;
import swidget.widgets.Spacing;



class EditButtonEditor extends DefaultCellEditor
{

	private ImageButton				button;
	private JPanel 					container;
	
	private Container				owner;

	private Filter					filter;
	private FilteringController		controller;

	private String					label;
	private boolean					isPushed;
	
	private Map<Filter, SwingAutoDialog> settingsDialogs;


	public EditButtonEditor(FilteringController controller, Container owner)
	{
		super(new JCheckBox());

		this.controller = controller;
		this.owner = owner;
		this.settingsDialogs = new HashMap<>();

		button = new ImageButton(StockIcon.MISC_PREFERENCES, IconSize.TOOLBAR_SMALL).withTooltip("Edit Filter").withLayout(Layout.IMAGE).withBordered(false);
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
				
				if (owner instanceof Window) {
					dialog = new FilterDialog(controller, filter, AutoDialogButtons.CLOSE, (Window)owner);	
				} else {
					dialog = new FilterDialog(controller, filter, AutoDialogButtons.CLOSE, null);
				}
				
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