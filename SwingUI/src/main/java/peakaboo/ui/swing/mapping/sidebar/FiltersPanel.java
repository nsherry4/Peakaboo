package peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sciencestudio.autodialog.view.editors.AutoDialogButtons;
import net.sciencestudio.autodialog.view.swing.SwingAutoDialog;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.controller.mapper.filtering.MapFilteringController;
import peakaboo.mapping.filter.model.MapFilter;
import peakaboo.mapping.filter.model.MapFilterPluginManager;
import peakaboo.mapping.filter.plugin.MapFilterPlugin;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.models.ListTableModel;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.layout.ButtonBox;
import swidget.widgets.listcontrols.ListControls;
import swidget.widgets.listwidget.ListWidget;
import swidget.widgets.listwidget.ListWidgetCellEditor;
import swidget.widgets.listwidget.ListWidgetTableCellRenderer;

public class FiltersPanel extends JPanel {

	private Window window;
	private CardLayout layout;
	
	private String PANEL_FILTERS = "PANEL_FILTERS";
	private String PANEL_ADD = "PANEL_ADD";
	
	private MapFilteringController controller;
	
	private JTable filterTable;
	private ListControls filterControls;
	
	public FiltersPanel(MapFilteringController controller, Window window) {
		this.controller = controller;
		this.window = window;
		
		layout = new CardLayout();
		setLayout(layout);
		add(buildFiltersPanel(), PANEL_FILTERS);
		add(buildAddPanel(), PANEL_ADD);
		
		layout.show(this, PANEL_FILTERS);		
		
		
		controller.addListener(type -> {
			if (!UpdateType.FILTER.toString().equals(type)) {
				return;
			}

			
			filterControls.setElementCount(controller.size());
		});
		
	}

	private JPanel buildFiltersPanel() {
		filterTable = new JTable(new TableModel() {

			@Override
			public int getRowCount() {
				return controller.size();
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "" + columnIndex;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case 0: return Boolean.class;
				case 1: return MapFilter.class;
				case 2: return MapFilter.class;
				default: return MapFilter.class;
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex <= 1;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
				case 0: return controller.get(rowIndex).isEnabled();
				case 1: return controller.get(rowIndex);
				case 2: return controller.get(rowIndex);
				default: return controller.get(rowIndex);
				}
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				switch (columnIndex) {
				case 0: 
					controller.setMapFilterEnabled(rowIndex, (boolean)aValue);
					return;
				case 1: return;
				case 2: return;
				default: return;
				}
			}

			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}});
		filterTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		filterTable.getColumnModel().getColumn(0).setMinWidth(32);
		filterTable.getColumnModel().getColumn(0).setPreferredWidth(32);
		filterTable.getColumnModel().getColumn(0).setMaxWidth(32);
		
		filterTable.getColumnModel().getColumn(1).setCellRenderer(new ListWidgetTableCellRenderer<MapFilter>(new MapFilterSettingsButton(controller, window)));
		filterTable.getColumnModel().getColumn(1).setCellEditor(new ListWidgetCellEditor<MapFilter>(new MapFilterSettingsButton(controller, window)));
		filterTable.getColumnModel().getColumn(1).setMinWidth(32);
		filterTable.getColumnModel().getColumn(1).setPreferredWidth(32);
		filterTable.getColumnModel().getColumn(1).setMaxWidth(32);
		
		filterTable.getColumnModel().getColumn(2).setCellRenderer(new ListWidgetTableCellRenderer<MapFilter>(new MapFilterWidget()));
		filterTable.getColumnModel().getColumn(2).setCellEditor(new ListWidgetCellEditor<MapFilter>(new MapFilterWidget()));
		
		
		ImageButton add = new ImageButton(StockIcon.EDIT_ADD)
			.withTooltip("Add Filter")
			.withAction(() -> {
				layout.show(this, PANEL_ADD);
				//TODO: Remove Me
				//controller.add(new AverageMapFilter());
		});
		ImageButton remove = new ImageButton(StockIcon.EDIT_REMOVE)
			.withTooltip("Remove Filter")
			.withAction(() -> {
				int index = filterTable.getSelectionModel().getAnchorSelectionIndex();
				if (index < 0 || index >= controller.size()) { return; }
				controller.remove(index);
		});
		ImageButton clear = new ImageButton(StockIcon.EDIT_CLEAR)
			.withTooltip("Clear Filters")
			.withAction(() -> {
				controller.clear();
		});
		
		filterControls = new ListControls(add, remove, clear);
		
		JPanel addPanel = new JPanel(new BorderLayout());
		addPanel.add(filterControls, BorderLayout.NORTH);
		addPanel.add(filterTable, BorderLayout.CENTER);
		return addPanel;
	}

	private JPanel buildAddPanel() {
		
		List<BoltPluginPrototype<? extends MapFilterPlugin>> plugins = MapFilterPluginManager.SYSTEM.getPlugins().getAll();
		TableModel model = new ListTableModel<>(plugins);
		JTable table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setCellRenderer(new ListWidgetTableCellRenderer<>(new MapFilterPluginWidget()));
		
		
		ImageButton ok = new ImageButton(StockIcon.CHOOSE_OK).withText("OK").withBordered(false).withAction(() -> {
			int index = table.getSelectedRow();
			if (index >= 0) { 
				MapFilterPlugin plugin = plugins.get(index).create();
				plugin.initialize();
				controller.add(plugin);
			}
			layout.show(this, PANEL_FILTERS);
			
		});
		ImageButton cancel = new ImageButton(StockIcon.CHOOSE_CANCEL).withText("Cancel").withBordered(false).withAction(() -> {
			layout.show(this, PANEL_FILTERS);
		});
		ButtonBox buttons = new ButtonBox(Spacing.small, false);
		buttons.addCentre(ok);
		buttons.addCentre(cancel);
		JPanel header = new TitlePaintedPanel("Add Map Filter", false, buttons);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(table, BorderLayout.CENTER);
		panel.add(header, BorderLayout.NORTH);
		
		// TODO Auto-generated method stub
		return panel;
	}
	
}

class MapFilterWidget extends ListWidget<MapFilter> {

	private JLabel label = new JLabel();
	
	public MapFilterWidget() {
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setBorder(Spacing.bHuge());
		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (label == null) { return; }
		label.setForeground(c);
	}
	
	@Override
	protected void onSetValue(MapFilter filter) {
		label.setText(filter.getFilterName());
	}
	
}

class MapFilterSettingsButton extends ListWidget<MapFilter> {
	
	private ImageButton button = new ImageButton(StockIcon.MISC_PREFERENCES, IconSize.TOOLBAR_SMALL);
	private MapFilter filter;
	
	private static Map<MapFilter, MapFilterDialog> dialogs = new HashMap<>();
	
	public MapFilterSettingsButton(MapFilteringController controller, Window window) {
		
		setLayout(new BorderLayout());
		add(button, BorderLayout.CENTER);
		
		button.withBordered(false);
		button.setOpaque(false);
		button.withBorder(new EmptyBorder(0, 0, 0, 0));
				
		button.withAction(() -> {
			MapFilterDialog dialog;
			if (dialogs.keySet().contains(filter)) {
				dialog = dialogs.get(filter);
				dialog.setVisible(true);
			} else {
				dialog = new MapFilterDialog(controller, filter, AutoDialogButtons.CLOSE, window);
				dialog.setHelpMessage(filter.getFilterDescription());
				dialog.setHelpTitle(filter.getFilterName());
				dialog.initialize();
				dialogs.put(filter, dialog);
			}
			getListWidgetParent().editingStopped();
		});
	}
	
	@Override
	protected void onSetValue(MapFilter filter) {
		this.filter = filter;
		button.setVisible(filter.getParameters().size() > 0);
	}
	
}

class MapFilterPluginWidget extends ListWidget<BoltPluginPrototype<? extends MapFilterPlugin>> {

	private JLabel label = new JLabel();
	
	public MapFilterPluginWidget() {
		label.setIcon(StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON));
		label.setBorder(Spacing.bMedium());
		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (label == null) { return; }
		label.setForeground(c);
	}
	
	@Override
	protected void onSetValue(BoltPluginPrototype<? extends MapFilterPlugin> value) {
		label.setText(value.getName());
	}
	
	
}

class MapFilterDialog extends SwingAutoDialog {
	
	MapFilterDialog(MapFilteringController controller, MapFilter filter, AutoDialogButtons buttons, Window window) {
		super(window, filter.getParameterGroup(), buttons);
		
		getGroup().getValueHook().addListener(o -> {
			controller.filteredDataInvalidated();
		});
		
	}
}