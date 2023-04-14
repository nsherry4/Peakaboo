package org.peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
import java.awt.Dimension;

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
import org.peakaboo.controller.mapper.fitting.modes.CorrelationModeController;
import org.peakaboo.controller.mapper.fitting.modes.OverlayModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.correlation.CorrelationMapMode;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.display.map.modes.overlay.OverlayMapMode;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.ui.swing.mapping.colours.ColourComboTableCellRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;


class OverlayUI extends JPanel {

	private MapFittingController viewController;
		
	public OverlayUI(MapFittingController viewController) {
		this.viewController = viewController;
		createElementsList();
	}

	private OverlayModeController modeController() {
		return (OverlayModeController) viewController.getModeController(OverlayMapMode.MODE_NAME).get();
	}

	private JPanel createScaleOptions() {
		JPanel options = new JPanel(new BorderLayout());
		
		ScaleModeWidget scaleMode = new ScaleModeWidget(viewController, "Colour", "All", true);
		options.add(scaleMode, BorderLayout.CENTER);
				
		return options;
	}
	
	private void createElementsList() {

		setLayout(new BorderLayout(Spacing.medium, Spacing.medium));

		// elements list
		add(createTransitionSeriesList(), BorderLayout.CENTER);
		add(createScaleOptions(), BorderLayout.SOUTH);
		
	}

	private JScrollPane createTransitionSeriesList() {
		
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				
				if (columnIndex == 0) {
					Boolean bvalue = (Boolean) value;
					ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);
					modeController().setVisibility(ts, bvalue);
				} 
				else if (columnIndex == 2) {
					ITransitionSeries ts = viewController.getAllTransitionSeries().get(rowIndex);
					modeController().setColour(ts, (OverlayColour)value);
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
					case 0: return modeController().getVisibility(ts);
					case 1: return ts;
					case 2: return modeController().getColour(ts);
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
					case 2: return "Colour";
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
					case 2: return OverlayColour.class;
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
		column.setPreferredWidth(35);
		column.setMaxWidth(35);

		
		

				
		ColourComboTableCellRenderer<OverlayColour> colourRenderer = new ColourComboTableCellRenderer<>();
		JComboBox<OverlayColour> comboBox = new JComboBox<>(OverlayColour.values());
		comboBox.setRenderer(colourRenderer);
		TableCellEditor editor = new DefaultCellEditor(comboBox);
		
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(colourRenderer);
		column.setCellEditor(editor);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);
		
		
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));
		scroll.setBorder(Spacing.bNone());
		
		return scroll;

	}

}
