package peakaboo.ui.swing.mapping.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
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

import eventful.EventfulTypeListener;

import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.maptab.MapScaleMode;
import peakaboo.controller.mapper.maptab.MapTabController;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.mapping.colours.OverlayColour;
import peakaboo.ui.swing.mapping.colours.ComboTableCellRenderer;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;


public class Overlay extends JPanel {

	private MapTabController controller;

	private JRadioButton 		relativeScale;
	private JRadioButton 		absoluteScale;
	
	public Overlay(MapTabController _controller) {

		this.controller = _controller;
		
		createElementsList();

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{
				
				absoluteScale.setSelected(controller.getMapScaleMode() == MapScaleMode.ABSOLUTE);
				relativeScale.setSelected(controller.getMapScaleMode() == MapScaleMode.RELATIVE);			

			}
		});
		
	}

	

	private JPanel createScaleOptions()
	{
		
		JPanel modeFrame = new JPanel();
		
		TitledBorder titleBorder = new TitledBorder("Scale Colours:");
		titleBorder.setBorder(Spacing.bNone());
		
		modeFrame.setBorder(titleBorder);
		modeFrame.setLayout(new BorderLayout());
		
		JPanel visibleElementsPanel = new ClearPanel();
		visibleElementsPanel.setLayout(new BorderLayout());
		
		relativeScale = new JRadioButton("Separately (Qualitative)");
		JLabel warning = new JLabel( StockIcon.BADGE_WARNING.toImageIcon(IconSize.BUTTON) );
		visibleElementsPanel.add(relativeScale, BorderLayout.WEST);
		visibleElementsPanel.add(warning, BorderLayout.EAST);
		
		relativeScale.setToolTipText("Warning: This option gives qualitative results only. Scaling each colour group independantly may lead to better looking graphs, but they will not be accurate.");
		warning.setToolTipText("Warning: This option gives qualitative results only. Scaling each colour group independantly may lead to better looking graphs, but they will not be accurate.");
		
		absoluteScale = new JRadioButton("As a Group");
		
		
		
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(relativeScale);
		scaleGroup.add(absoluteScale);
		absoluteScale.setSelected(true);
		
		relativeScale.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.RELATIVE);
			}
		});
		absoluteScale.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.ABSOLUTE);
			}
		});
		
		
		modeFrame.add(visibleElementsPanel, BorderLayout.NORTH);
		modeFrame.add(absoluteScale, BorderLayout.SOUTH);
		
		return modeFrame;
		
	}
	
	private void createElementsList() {

		setLayout(new BorderLayout());

		// elements list
		add(createTransitionSeriesList(), BorderLayout.CENTER);
		add(createScaleOptions(), BorderLayout.SOUTH);
		
	}

	private JScrollPane createTransitionSeriesList() {
		
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				
				if (columnIndex == 0) {
					
					Boolean bvalue = (Boolean) value;
					TransitionSeries ts = controller.getAllTransitionSeries().get(rowIndex);

					controller.setTransitionSeriesVisibility(ts, bvalue);
					controller.invalidateInterpolation();
				} 
				else if (columnIndex == 2)
				{
					TransitionSeries ts = controller.getAllTransitionSeries().get(rowIndex);
					controller.setOverlayColour(ts, (OverlayColour)value);
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

				TransitionSeries ts = controller.getAllTransitionSeries().get(rowIndex);

				switch (columnIndex) {

					case 0: return controller.getTransitionSeriesVisibility(ts);
					case 1: return ts.toElementString();
					case 2: return controller.getOverlayColour(ts);
				}

				return null;

			}

			public int getRowCount() {
				return controller.getAllTransitionSeries().size();
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
					case 1: return String.class;
					case 2: return OverlayColour.class;
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

		
		

				
		ComboTableCellRenderer renderer = new ComboTableCellRenderer();
		JComboBox comboBox = new JComboBox(OverlayColour.values());
		comboBox.setRenderer(renderer);
		TableCellEditor editor = new DefaultCellEditor(comboBox);
		
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);
		
		
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));

		return scroll;

	}

}
