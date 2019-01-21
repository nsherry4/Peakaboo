package peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.controller.mapper.filtering.MapFilteringController;
import peakaboo.mapping.filter.model.MapFilter;
import peakaboo.mapping.filter.plugin.plugins.AverageMapFilter;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.models.ListTableModel;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.listcontrols.ListControls;
import swidget.widgets.listwidget.ListWidget;
import swidget.widgets.listwidget.ListWidgetCellEditor;
import swidget.widgets.listwidget.ListWidgetTableCellRenderer;

public class FiltersPanel extends JPanel {

	private CardLayout layout;
	
	private String PANEL_FILTERS = "PANEL_FILTERS";
	private String PANEL_ADD = "PANEL_ADD";
	
	private MapFilteringController controller;
	
	private JTable filterTable;
	private ListControls filterControls;
	
	public FiltersPanel(MapFilteringController controller) {
		this.controller = controller;
		
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
					controller.get(rowIndex).setEnabled((boolean) aValue); 
					//TODO: does this have to be here, it should be lower (ie not in the ui)
					controller.updateListeners(UpdateType.FILTER.toString());
					return;
				case 1: return; //TODO: Show dialog?
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
		
		filterTable.getColumnModel().getColumn(1).setCellRenderer(new ListWidgetTableCellRenderer<MapFilter>(new MapFilterSettingsButton()));
		filterTable.getColumnModel().getColumn(1).setCellEditor(new ListWidgetCellEditor<MapFilter>(new MapFilterSettingsButton()));
		filterTable.getColumnModel().getColumn(1).setMinWidth(32);
		filterTable.getColumnModel().getColumn(1).setPreferredWidth(32);
		filterTable.getColumnModel().getColumn(1).setMaxWidth(32);
		
		filterTable.getColumnModel().getColumn(2).setCellRenderer(new ListWidgetTableCellRenderer<MapFilter>(new MapFilterWidget()));
		filterTable.getColumnModel().getColumn(2).setCellEditor(new ListWidgetCellEditor<MapFilter>(new MapFilterWidget()));
		
		
		ImageButton add = new ImageButton(StockIcon.EDIT_ADD)
			.withTooltip("Add Filter")
			.withAction(() -> {
				//layout.show(this, PANEL_ADD);
				//TODO: Remove Me
				controller.add(new AverageMapFilter());
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
		// TODO Auto-generated method stub
		return new JPanel();
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
	
	public MapFilterSettingsButton() {
		
		setLayout(new BorderLayout());
		add(button, BorderLayout.CENTER);
		
		button.withBordered(false);
		button.setOpaque(false);
		
		button.withAction(() -> {
			System.out.println("Show Settings for Filter " + filter);
		});
	}
	
	@Override
	protected void onSetValue(MapFilter filter) {
		this.filter = filter;
	}
	
}
