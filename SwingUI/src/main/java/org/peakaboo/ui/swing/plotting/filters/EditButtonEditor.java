package org.peakaboo.ui.swing.plotting.filters;



import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.autodialog.view.editors.AutoDialogButtons;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoDialog;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.ui.swing.app.PeakabooIcons;



class EditButtonEditor extends DefaultCellEditor {

	private FluentButton				button;
	private JPanel 					container;
	
	private Window					owner;

	private Filter					filter;
	private FilteringController		controller;
	private FiltersetViewer 		filtersUI;

	private String					label;
	private boolean					isPushed;
	
	private ImageIcon 				imgEdit, imgEditSelected;
	

	public EditButtonEditor(FilteringController controller, Window owner, FiltersetViewer filtersUI) {
		super(new JCheckBox());

		this.controller = controller;
		this.owner = owner;
		this.filtersUI = filtersUI;

		imgEdit = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON);
		imgEditSelected = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON, Color.WHITE);
		
		button = new FluentButton(PeakabooIcons.MENU_SETTINGS, IconSize.BUTTON)
				.withTooltip("Edit Filter")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false)
				.withAction(this::fireEditingStopped);
		container = new JPanel();
		container.setBorder(Spacing.bNone());
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object filterObject, boolean isSelected, int row, int column) {

		filter = (Filter) filterObject;
		int numParameters = (filter == null) ? 0 : filter.getParameters().size();
		
		label = (filter == null) ? "" : filter.toString();
		isPushed = true;
		
		
		container.setBackground(table.getSelectionBackground());
		container.setOpaque(true);

		
		container.remove(button);
		if (numParameters == 0){		
			return container;
		}
		container.add(button);
		if (table.getSelectedRow() == row) {
			button.setIcon(imgEditSelected);
		} else {
			button.setIcon(imgEdit);
		}
		return container;
	}


	@Override
	public Object getCellEditorValue() {
		
		if (isPushed) {
			this.filtersUI.showSettingsPane(this.filter);
		}
		isPushed = false;
		return label;
	}


	@Override
	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}


	@Override
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}