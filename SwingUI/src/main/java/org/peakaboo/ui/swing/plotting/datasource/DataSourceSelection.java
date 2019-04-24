package org.peakaboo.ui.swing.plotting.datasource;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.plugin.DataSourcePlugin;
import org.peakaboo.framework.swidget.models.ListTableModel;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.widgets.ListPickerLayer;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetTableCellRenderer;
import org.peakaboo.framework.swidget.widgets.listwidget.impl.OptionWidget;


public class DataSourceSelection extends ListPickerLayer<DataSourcePlugin>
{

	public DataSourceSelection(LayerPanel parent, List<DataSourcePlugin> dsps, Consumer<DataSourcePlugin> onSelect) {
		super(parent, "Please Select Data Format", dsps, onSelect);		
	}

	@Override
	protected JTable getTable(List<DataSourcePlugin> items) {
		JTable table = new JTable(new ListTableModel<>(items));
		TableColumn c = table.getColumnModel().getColumn(0);
		c.setCellRenderer(new ListWidgetTableCellRenderer<>( new OptionWidget<DataSource>(
				ds -> ds.getFileFormat().getFormatName(), 
				ds -> ds.getFileFormat().getFormatDescription(), 
				ds -> null
			)));
		return table;
	}
	
}
