package org.peakaboo.ui.swing.mapping.sidebar.modes;

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

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.ui.swing.mapping.colours.ColourComboTableCellRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;


public class Ratio extends JPanel {

	private MapFittingController viewController;

	
	public Ratio(MapFittingController viewController) {

		this.viewController = viewController;

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

	
	private JPanel createScaleOptions() {
		return new ScaleModeWidget(viewController, "Colour", "All", true);
	}
	
	
	
	private JPanel createElementsList() {

		JPanel elementsPanel = new JPanel();
		elementsPanel.setLayout(new BorderLayout(Spacing.medium, Spacing.medium));

		// elements list
		elementsPanel.add(createTransitionSeriesList(), BorderLayout.CENTER);
		elementsPanel.add(createScaleOptions(), BorderLayout.SOUTH);
		
		return elementsPanel;
	}

	private JScrollPane createTransitionSeriesList() {
		
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				
				if (columnIndex == 0) {
					
					Boolean bvalue = (Boolean) value;
					ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);

					viewController.ratioMode().setVisibility(ts, bvalue);
				} 
				else if (columnIndex == 2)
				{
					ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);
					viewController.ratioMode().setSide(ts, (Integer)value);
				}
			}

			public void removeTableModelListener(TableModelListener l) {
				// NOOP
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);
				
				switch (columnIndex) {
					case 0: return viewController.getTransitionSeriesEnabled(ts);
					case 1: return false;
					case 2: return true;
				}

				return false;
				
			}

			public Object getValueAt(int rowIndex, int columnIndex) {

				ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);

				switch (columnIndex) {
					case 0: return viewController.ratioMode().getVisibility(ts);
					case 1: return ts;
					case 2: return viewController.ratioMode().getSide(ts);
				}

				return null;

			}

			public int getRowCount() {
				return viewController.getAllTransitionSeries().size();
			}

			public String getColumnName(int columnIndex) {
				
				switch (columnIndex) {
					case 0:	return "Map";
					case 1: return "Fitting";
					case 2: return "Ratio Sets";
				}
				return "";
			}

			public int getColumnCount() {
				return 3;
			}

			public Class<?> getColumnClass(int columnIndex) {
				
				switch (columnIndex) {
					case 0:	return Boolean.class;
					case 1: return ITransitionSeries.class;
					case 2: return Integer.class;
				}
				return Object.class;
			}

			public void addTableModelListener(TableModelListener l) {
				// NOOP
			}
		};

		JTable table = new JTable(m);
		table.setTableHeader(null);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setFillsViewportHeight(true);
		
		MapFittingRenderer fitRenderer = new MapFittingRenderer(viewController::getTransitionSeriesEnabled);
		table.getColumnModel().getColumn(1).setCellRenderer(fitRenderer);
		table.setRowHeight(fitRenderer.getPreferredSize().height);
		
		
		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setResizable(false);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);

		
		
		Integer[] choices = {1,2};
		ColourComboTableCellRenderer<Integer> renderer = new ColourComboTableCellRenderer<>();
		JComboBox<Integer> comboBox = new JComboBox<>(choices);
		comboBox.setRenderer(renderer);
		TableCellEditor editor = new DefaultCellEditor(comboBox);
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));
		scroll.setBorder(Spacing.bNone());
		
		return scroll;

	}

}
