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

import org.peakaboo.controller.mapper.settings.MapFittingSettings;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.OverlayColour;
import org.peakaboo.ui.swing.mapping.colours.ComboTableCellRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;

import swidget.widgets.Spacing;


public class Overlay extends JPanel {

	private MapFittingSettings mapFittings;
	private MapSettingsController controller;
	
	private ScaleModeWidget scaleMode;
	
	public Overlay(MapSettingsController _controller) {

		this.controller = _controller;
		this.mapFittings = _controller.getMapFittings();
		
		createElementsList();
		
	}

	

	private JPanel createScaleOptions() {
		JPanel options = new JPanel(new BorderLayout());
		
		scaleMode = new ScaleModeWidget(controller, "Colour", "All", true);
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
					ITransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);

					mapFittings.setTransitionSeriesVisibility(ts, bvalue);
				} 
				else if (columnIndex == 2)
				{
					ITransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);
					mapFittings.setOverlayColour(ts, (OverlayColour)value);
				}
			}

			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				ITransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);
				
				switch (columnIndex) {

					case 0: return mapFittings.getTransitionSeriesEnabled(ts);
					case 1: return false;
					case 2: return true;
				}

				return false;
				
			}

			public Object getValueAt(int rowIndex, int columnIndex) {

				ITransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);

				switch (columnIndex) {

					case 0: return mapFittings.getTransitionSeriesVisibility(ts);
					case 1: return ts;
					case 2: return mapFittings.getOverlayColour(ts);
				}

				return null;

			}

			public int getRowCount() {
				return mapFittings.getAllTransitionSeries().size();
			}

			public String getColumnName(int columnIndex) {
				
				switch (columnIndex)
				{
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
				
				switch (columnIndex)
				{
					case 0:	return Boolean.class;
					case 1: return ITransitionSeries.class;
					case 2: return OverlayColour.class;
				}
				return Object.class;
			}

			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}
		};

		JTable table = new JTable(m);
		table.setTableHeader(null);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setFillsViewportHeight(true);
		
		MapFittingRenderer fitRenderer = new MapFittingRenderer(mapFittings::getTransitionSeriesEnabled);
		table.getColumnModel().getColumn(1).setCellRenderer(fitRenderer);
		table.setRowHeight(fitRenderer.getPreferredSize().height);
		
		
		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setResizable(false);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);

		
		

				
		ComboTableCellRenderer<OverlayColour> colourRenderer = new ComboTableCellRenderer<>();
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
