package peakaboo.ui.swing.plotting.datasource;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.peakaboo.datasource.model.DataSource;

import swidget.models.ListTableModel;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.widgets.ListPickerLayer;
import swidget.widgets.listwidget.ListWidgetTableCellRenderer;
import swidget.widgets.listwidget.impl.OptionWidget;


public class DataSourceSelection extends ListPickerLayer<DataSource>
{

	public DataSourceSelection(LayerPanel parent, List<DataSource> dsps, Consumer<DataSource> onSelect) {
		super(parent, "Please Select Data Format", dsps, onSelect);		
	}

	@Override
	protected JTable getTable(List<DataSource> items) {
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
