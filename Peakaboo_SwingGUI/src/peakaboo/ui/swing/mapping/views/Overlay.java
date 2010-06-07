package peakaboo.ui.swing.mapping.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
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

import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.MapScaleMode;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.mapping.colours.OverlayColor;
import peakaboo.ui.swing.icons.IconFactory;
import peakaboo.ui.swing.icons.IconSize;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.colours.ComboTableCellRenderer;

public class Overlay extends JPanel {

	private MapController controller;

	private JRadioButton 		visibleElements;
	private JRadioButton 		allElements;
	
	public Overlay(MapController _controller) {

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

	

	private JPanel createScaleOptions()
	{
		
		JPanel modeFrame = new JPanel();
		
		TitledBorder titleBorder = new TitledBorder("Scale Colour Groups:");
		titleBorder.setBorder(Spacing.bNone());
		
		modeFrame.setBorder(titleBorder);
		modeFrame.setLayout(new BorderLayout());
		
		JPanel visibleElementsPanel = new ClearPanel();
		visibleElementsPanel.setLayout(new BorderLayout());
		
		visibleElements = new JRadioButton("Independantly");
		JLabel warning = new JLabel(IconFactory.getImageIcon("warn", IconSize.BUTTON));
		visibleElementsPanel.add(visibleElements, BorderLayout.WEST);
		visibleElementsPanel.add(warning, BorderLayout.EAST);
		
		visibleElements.setToolTipText("Warning: This option gives qualitative results only. Scaling each colour group independantly may lead to better looking graphs, but they will not be accurate.");
		warning.setToolTipText("Warning: This option gives qualitative results only. Scaling each colour group independantly may lead to better looking graphs, but they will not be accurate.");
		
		allElements = new JRadioButton("Against Strongest Group");
		
		
		
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(visibleElements);
		scaleGroup.add(allElements);
		allElements.setSelected(true);
		
		visibleElements.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.VISIBLE_ELEMENTS);
			}
		});
		allElements.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.ALL_ELEMENTS);
			}
		});
		
		
		modeFrame.add(visibleElementsPanel, BorderLayout.NORTH);
		modeFrame.add(allElements, BorderLayout.SOUTH);
		
		return modeFrame;
		
	}
	
	private JPanel createElementsList() {

		JPanel elementsPanel = new JPanel();
		elementsPanel.setLayout(new BorderLayout());

		// elements list
		elementsPanel.add(createTransitionSeriesList(), BorderLayout.CENTER);
		//elementsPanel.add(createScaleOptions(), BorderLayout.SOUTH);
		
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
					controller.getActiveTabModel().overlayColour.put(ts, (OverlayColor)value);
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
					case 2: return controller.getActiveTabModel().overlayColour.get(ts);
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
					case 2: return OverlayColor.class;
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

		
		
		/*Color choices[] = { 
				new Color(0.643f, 0f, 0f),
				new Color(0.306f, 0.604f, 0.024f),
				new Color(0.125f, 0.290f, 0.530f),
				Color.black 
			};*/
				
		ComboTableCellRenderer renderer = new ComboTableCellRenderer();
		JComboBox comboBox = new JComboBox(OverlayColor.values());
		comboBox.setRenderer(renderer);
		TableCellEditor editor = new DefaultCellEditor(comboBox);
		
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);
		
		
		
		JScrollPane scroll = new JScrollPane(table);

		scroll.setPreferredSize(new Dimension(200,
				scroll.getPreferredSize().height));

		return scroll;

	}

}
