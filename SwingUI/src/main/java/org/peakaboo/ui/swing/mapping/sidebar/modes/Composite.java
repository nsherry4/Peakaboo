package org.peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;

public class Composite extends JPanel {
	
	private MapFittingController viewController;

	public Composite(MapFittingController viewController) {
		this.viewController = viewController;
		createElementsList();
	}
	
	
	private void createElementsList() {
		setLayout(new BorderLayout(Spacing.medium, Spacing.medium));
				
		//elements list
		add(createTransitionSeriesList(), BorderLayout.CENTER);
		add(createScaleOptions(), BorderLayout.SOUTH);
	}
	
	
	
	
	private JPanel createScaleOptions() {
		return new ScaleModeWidget(viewController, "Visible", "All", false);
	}
	

	private JScrollPane createTransitionSeriesList() {
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				if (columnIndex == 0) {
					Boolean bvalue = (Boolean) value;
					ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);
					viewController.compositeMode().setVisibility(ts, bvalue);
				}
			}


			public void removeTableModelListener(TableModelListener l) {
				//NOOP
			}


			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0) {
					ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);
					return viewController.getTransitionSeriesEnabled(ts);
				}
				return false;
			}


			public Object getValueAt(int rowIndex, int columnIndex) {
				ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);
				if (columnIndex == 0) {
					return viewController.compositeMode().getVisibility(ts);
				} else {
					return ts;
				}
			}


			public int getRowCount() {
				return viewController.getAllTransitionSeries().size();
			}


			public String getColumnName(int columnIndex) {
				if (columnIndex == 0) return "Map";
				return "Fitting";
			}


			public int getColumnCount() {
				return 2;
			}


			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) return Boolean.class;
				return ITransitionSeries.class;
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
		
		MapFittingRenderer renderer = new MapFittingRenderer(viewController::getTransitionSeriesEnabled);
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.setRowHeight(renderer.getPreferredSize().height);
		
		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setResizable(false);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);
		
		

		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));
		scroll.setBorder(Spacing.bNone());
		

		return scroll;

	}
	
}


