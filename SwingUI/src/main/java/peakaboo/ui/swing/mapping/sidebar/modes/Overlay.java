package peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import peakaboo.controller.mapper.settings.MapFittingSettings;
import peakaboo.controller.mapper.settings.MapScaleMode;
import peakaboo.controller.mapper.settings.MapSettingsController;
import peakaboo.controller.mapper.settings.OverlayColour;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.ui.swing.mapping.colours.ComboTableCellRenderer;
import peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
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
		scaleMode = new ScaleModeWidget(controller, "Colour", "All", true);
		return scaleMode;
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
					TransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);

					mapFittings.setTransitionSeriesVisibility(ts, bvalue);
					mapFittings.invalidateInterpolation();
				} 
				else if (columnIndex == 2)
				{
					TransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);
					mapFittings.setOverlayColour(ts, (OverlayColour)value);
					mapFittings.invalidateInterpolation();
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

				TransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);

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
					case 1: return TransitionSeries.class;
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
		
		MapFittingRenderer fitRenderer = new MapFittingRenderer();
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
