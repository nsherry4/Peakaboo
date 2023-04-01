package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.filtering.MapFilteringController;
import org.peakaboo.framework.autodialog.view.editors.AutoDialogButtons;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoDialog;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.api.models.GroupedListTreeModel;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.stencil.StencilCellEditor;
import org.peakaboo.framework.stratus.components.stencil.StencilTableCellRenderer;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.itemlist.ListControls;
import org.peakaboo.framework.stratus.components.ui.itemlist.ReorderTransferHandler;
import org.peakaboo.framework.stratus.components.ui.itemlist.SelectionListControls;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.MapFilterPluginManager;
import org.peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;
import org.peakaboo.ui.swing.app.PeakabooIcons;

public class FiltersPanel extends JPanel {

	private Window window;
	private CardLayout layout;
	
	private static final String PANEL_FILTERS = "PANEL_FILTERS";
	private static final String PANEL_ADD = "PANEL_ADD";
	
	private MapFilteringController controller;
	
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
			if (type != MapUpdateType.FILTER) {
				return;
			}
			filterControls.setElementCount(controller.size());
		});
		
	}

	private JPanel buildFiltersPanel() {
		JTable filterTable = new JTable(new TableModel() {

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
				// NOOP
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// NOOP
				
			}});
		filterTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		filterTable.getColumnModel().getColumn(0).setMinWidth(32);
		filterTable.getColumnModel().getColumn(0).setPreferredWidth(32);
		filterTable.getColumnModel().getColumn(0).setMaxWidth(32);
		
		filterTable.getColumnModel().getColumn(1).setCellRenderer(new StencilTableCellRenderer<MapFilter>(new MapFilterSettingsButton(controller, window)));
		filterTable.getColumnModel().getColumn(1).setCellEditor(new StencilCellEditor<MapFilter>(new MapFilterSettingsButton(controller, window)));
		filterTable.getColumnModel().getColumn(1).setMinWidth(28);
		filterTable.getColumnModel().getColumn(1).setPreferredWidth(28);
		filterTable.getColumnModel().getColumn(1).setMaxWidth(28);
		
		filterTable.getColumnModel().getColumn(2).setCellRenderer(new StencilTableCellRenderer<MapFilter>(new MapFilterWidget()));
		filterTable.getColumnModel().getColumn(2).setCellEditor(new StencilCellEditor<MapFilter>(new MapFilterWidget()));
		
		
		
		filterTable.setDragEnabled(true);
		filterTable.setDropMode(DropMode.INSERT_ROWS);
		filterTable.setTransferHandler(new ReorderTransferHandler(filterTable) {
			
			@Override
			public void move(int from, int to) {
				controller.moveMapFilter(from, to);
			}
		});
		
		ToolTipManager.sharedInstance().registerComponent(filterTable);
		
		
		
		FluentButton add = new FluentButton(StockIcon.EDIT_ADD)
			.withTooltip("Add Filter")
			.withAction(() -> layout.show(this, PANEL_ADD));
		FluentButton remove = new FluentButton(StockIcon.EDIT_REMOVE)
			.withTooltip("Remove Filter")
			.withAction(() -> {
				int index = filterTable.getSelectionModel().getAnchorSelectionIndex();
				if (index < 0 || index >= controller.size()) { return; }
				controller.remove(index);
		});
		FluentButton clear = new FluentButton(StockIcon.EDIT_CLEAR)
			.withTooltip("Clear Filters")
			.withAction(controller::clear);
		
		filterControls = new ListControls(add, remove, clear);
		
		JPanel addPanel = new JPanel(new BorderLayout());
		addPanel.add(filterControls, BorderLayout.NORTH);
		addPanel.add(filterTable, BorderLayout.CENTER);
		return addPanel;
	}

	private JPanel buildAddPanel() {
		
		//model and tree
		List<BoltPluginPrototype<? extends JavaMapFilterPlugin>> plugins = MapFilterPluginManager.system().getPlugins();
		GroupedListTreeModel<BoltPluginPrototype<? extends JavaMapFilterPlugin>> treeModel = new GroupedListTreeModel<>(plugins, 
				item -> item.getReferenceInstance().getFilterDescriptor().getGroup());
		JTree tree = new JTree(treeModel);
		tree.setRootVisible(false);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setBorder(Spacing.bMedium());
		renderer.setLeafIcon(StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON));
		tree.setCellRenderer(renderer);
		
		
		SelectionListControls controls = new SelectionListControls("Map Filter", "Add Map Filter") {
			
			@Override
			protected void cancel() {
				layout.show(FiltersPanel.this, PANEL_FILTERS);
			}
			
			@Override
			protected void approve() {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				BoltPluginPrototype<? extends JavaMapFilterPlugin> proto = null;
				try {
					proto = (BoltPluginPrototype<? extends JavaMapFilterPlugin>) node.getUserObject();	
				} catch (ClassCastException e) {}
				
				if (proto != null) { 
					JavaMapFilterPlugin plugin = proto.create();
					plugin.initialize();
					controller.add(plugin);
				}
				layout.show(FiltersPanel.this, PANEL_FILTERS);
			}
		};
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(tree, BorderLayout.CENTER);
		panel.add(controls, BorderLayout.NORTH);
		
		return panel;
	}
	
}

class MapFilterWidget extends Stencil<MapFilter> {

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
	protected void onSetValue(MapFilter filter, boolean selected) {
		label.setText(filter.getFilterName());
	}
	
}

class MapFilterSettingsButton extends Stencil<MapFilter> {
	
	private FluentButton button = new FluentButton(PeakabooIcons.MENU_SETTINGS, IconSize.BUTTON);
	private MapFilter filter;
	
	private ImageIcon imgEdit, imgEditSel;
	
	private static Map<MapFilter, MapFilterDialog> dialogs = new HashMap<>();
	
	public MapFilterSettingsButton(MapFilteringController controller, Window window) {
		
		setLayout(new BorderLayout());
		add(button, BorderLayout.CENTER);
		
		imgEdit = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON);
		
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
	protected void onSetValue(MapFilter filter, boolean selected) {
		this.filter = filter;
		button.setVisible(!filter.getParameters().isEmpty());
		if (selected) {
			if (imgEditSel == null) {
				imgEditSel = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON, getForeground());
			}
			button.setIcon(imgEditSel);
		} else {
			button.setIcon(imgEdit);
		}
	}
	
}

class MapFilterDialog extends SwingAutoDialog {
	
	MapFilterDialog(MapFilteringController controller, MapFilter filter, AutoDialogButtons buttons, Window window) {
		super(window, filter.getParameterGroup(), buttons);
		
		getGroup().getValueHook().addListener(o -> controller.filteredDataInvalidated());
		
	}
}