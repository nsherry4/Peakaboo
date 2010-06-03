package peakaboo.ui.swing.mapping.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import peakaboo.controller.mapper.MapController;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.ui.swing.widgets.Spacing;

public class Ratio extends JPanel {

	private MapController controller;

	public Ratio(MapController _controller) {

		this.controller = _controller;

		setLayout(new GridBagLayout());

		GridBagConstraints maingbc = new GridBagConstraints();
		maingbc.insets = Spacing.iNone();
		maingbc.ipadx = 0;
		maingbc.ipady = 0;

		maingbc.gridx = 0;
		maingbc.gridy = 0;
		maingbc.weightx = 1.0;
		maingbc.weighty = 1.0;
		maingbc.fill = GridBagConstraints.BOTH;
		add(createElementsList(), maingbc);

	}

	private JPanel createElementsList() {

		JPanel elementsPanel = new JPanel();
		elementsPanel.setLayout(new BorderLayout());

		// elements list
		elementsPanel.add(createTransitionSeriesList(), BorderLayout.CENTER);

		return elementsPanel;
	}

	private JScrollPane createTransitionSeriesList() {
		
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				
				if (columnIndex == 0) {
					
					Boolean bvalue = (Boolean) value;
					TransitionSeries ts = controller.getActiveTabModel().getAllTransitionSeries().get(rowIndex);

					controller.getActiveTabModel().visible.put(ts, bvalue);
					controller.invalidateInterpolation();
				} 
				else if (columnIndex == 2)
				{
					TransitionSeries ts = controller.getActiveTabModel().getAllTransitionSeries().get(rowIndex);
					controller.getActiveTabModel().ratioSide.put(ts, (Integer)value);
					controller.invalidateInterpolation();
				}
			}

			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				
				switch (columnIndex) {

					case 0: return true;
					case 1: return false;
					case 2: return true;
				}

				return false;
				
			}

			public Object getValueAt(int rowIndex, int columnIndex) {

				TransitionSeries ts = controller.getActiveTabModel().getAllTransitionSeries().get(rowIndex);

				switch (columnIndex) {

					case 0: return controller.getActiveTabModel().visible.get(ts);
					case 1: return ts.toElementString();
					case 2: return controller.getActiveTabModel().ratioSide.get(ts);
				}

				return null;

			}

			public int getRowCount() {
				return controller.getActiveTabModel().getAllTransitionSeries().size();
			}

			public String getColumnName(int columnIndex) {
				
				switch (columnIndex)
				{
					case 0:	return "Map";
					case 1: return "Element";
					case 2: return "Ratio Sets";
				}
				return "";
			}

			public int getColumnCount() {
				return 3;
			}

			public Class<?> getColumnClass(int columnIndex) {
				
				switch (columnIndex)
				{
					case 0:	return Boolean.class;
					case 1: return String.class;
					case 2: return Integer.class;
				}
				return Object.class;
			}

			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}
		};

		JTable table = new JTable(m);

		
		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);

		
		
		Integer choices[] = {1,2};
		JComboBox comboBox = new JComboBox(choices);
		TableCellEditor editor = new DefaultCellEditor(comboBox);
		column = table.getColumnModel().getColumn(2);
		//column.setCellRenderer(renderer);
		column.setCellEditor(editor);
		
		/*ComboTableCellRenderer renderer = new ComboTableCellRenderer();
		
		comboBox.setRenderer(renderer);
		
		
		

		*/
		
		
		JScrollPane scroll = new JScrollPane(table);

		scroll.setPreferredSize(new Dimension(200,
				scroll.getPreferredSize().height));

		return scroll;

	}

}
