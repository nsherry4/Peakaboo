package peakaboo.ui.swing.plotting.datasource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import peakaboo.datasource.model.DataSource;
import swidget.Swidget;
import swidget.models.ListTableModel;
import swidget.widgets.Spacing;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.widgets.ListPickerLayer;
import swidget.widgets.listwidget.ListWidget;
import swidget.widgets.listwidget.ListWidgetTableCellRenderer;


public class DataSourceSelection extends ListPickerLayer<DataSource>
{

	
	public DataSourceSelection(LayerPanel parent, List<DataSource> dsps, Consumer<DataSource> onSelect) {
		super(parent, "Please Select Data Format", dsps, onSelect);		
	}

	@Override
	protected JTable getTable(List<DataSource> items) {
		JTable table = new JTable(new ListTableModel<>(items));
		TableColumn c = table.getColumnModel().getColumn(0);
		c.setCellRenderer(new ListWidgetTableCellRenderer<>(new DataSourceWidget()));
		return table;
	}

	private static class DataSourceWidget extends ListWidget<DataSource> {
		
		private JLabel titleLabel;
		private JLabel descLabel;
		
		public DataSourceWidget() {
			setLayout(new BorderLayout(Spacing.tiny, Spacing.tiny));
			
			titleLabel = new JLabel("");
			descLabel = new JLabel("");
			titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(titleLabel.getFont().getSize() + 4f));
			descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN));

			add(titleLabel, BorderLayout.NORTH);
			add(descLabel, BorderLayout.CENTER);
			
			setBorder(Spacing.bLarge());
		}

		@Override
		protected void onSetValue(DataSource value) {
			if (titleLabel != null) titleLabel.setText(value.getFileFormat().getFormatName());
			if (descLabel != null) {
				String desc = value.getFileFormat().getFormatDescription();
				desc = Swidget.lineWrapHTML(descLabel, desc, 400);
				descLabel.setText(desc);
			}
		}
		
		@Override
		public void setForeground(Color color) {
			super.setForeground(color);
			if (titleLabel != null) titleLabel.setForeground(color);
			if (descLabel != null) descLabel.setForeground(color);
		}
		
	}
	
}
