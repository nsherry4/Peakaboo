package peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
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
import peakaboo.controller.mapper.settings.MapSettingsController;
import peakaboo.curvefit.transition.TransitionSeries;
import peakaboo.controller.mapper.settings.MapFittingSettings;
import peakaboo.controller.mapper.settings.MapScaleMode;
import peakaboo.ui.swing.mapping.colours.ComboTableCellRenderer;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;


public class Ratio extends JPanel {

	private MapFittingSettings mapFittings;
	
	private JRadioButton 		relativeScale;
	private JRadioButton 		absoluteScale;
	
	public Ratio(MapSettingsController _controller) {

		this.mapFittings = _controller.getMapFittings();

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
		
		
		_controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{
				
				absoluteScale.setSelected(mapFittings.getMapScaleMode() == MapScaleMode.ABSOLUTE);
				relativeScale.setSelected(mapFittings.getMapScaleMode() == MapScaleMode.RELATIVE);			

			}
		});
		

	}

	
	private JPanel createScaleOptions()
	{
		
		JPanel modeFrame = new JPanel();
		
		TitledBorder titleBorder = new TitledBorder("Scale Ratio Sides:");
		titleBorder.setBorder(Spacing.bNone());
		
		modeFrame.setBorder(titleBorder);
		modeFrame.setLayout(new BorderLayout());
		
		JPanel visibleElementsPanel = new ClearPanel();
		visibleElementsPanel.setLayout(new BorderLayout());
		
		relativeScale = new JRadioButton("Separately (Qualitative)");
		JLabel warning = new JLabel( StockIcon.BADGE_WARNING.toImageIcon(IconSize.BUTTON) );
		visibleElementsPanel.add(relativeScale, BorderLayout.WEST);
		visibleElementsPanel.add(warning, BorderLayout.EAST);
		
		relativeScale.setToolTipText("Warning: This option gives qualitative results only. Scaling each ratio side separately may lead to better looking graphs, but they will not be accurate.");
		warning.setToolTipText("Warning: This option gives qualitative results only. Scaling each ratio side separately may lead to better looking graphs, but they will not be accurate.");
		
		absoluteScale = new JRadioButton("As a Group");
		
		
		
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(relativeScale);
		scaleGroup.add(absoluteScale);
		absoluteScale.setSelected(true);
		
		relativeScale.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				mapFittings.setMapScaleMode(MapScaleMode.RELATIVE);
			}
		});
		absoluteScale.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				mapFittings.setMapScaleMode(MapScaleMode.ABSOLUTE);
			}
		});
		
		
		modeFrame.add(visibleElementsPanel, BorderLayout.NORTH);
		modeFrame.add(absoluteScale, BorderLayout.SOUTH);
		
		return modeFrame;
		
	}
	
	
	
	private JPanel createElementsList() {

		JPanel elementsPanel = new JPanel();
		elementsPanel.setLayout(new BorderLayout());

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
					TransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);

					mapFittings.setTransitionSeriesVisibility(ts, bvalue);
					mapFittings.invalidateInterpolation();
				} 
				else if (columnIndex == 2)
				{
					TransitionSeries ts = mapFittings.getAllTransitionSeries().get(rowIndex);
					mapFittings.setRatioSide(ts, (Integer)value);
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
					case 1: return ts.toElementString();
					case 2: return mapFittings.getRatioSide(ts);
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
		column.setResizable(false);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);

		
		
		Integer choices[] = {1,2};
		ComboTableCellRenderer<Integer> renderer = new ComboTableCellRenderer<>();
		JComboBox<Integer> comboBox = new JComboBox<>(choices);
		comboBox.setRenderer(renderer);
		TableCellEditor editor = new DefaultCellEditor(comboBox);
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);
		
		/*ComboTableCellRenderer renderer = new ComboTableCellRenderer();
		
		comboBox.setRenderer(renderer);
		
		
		

		*/
		
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));

		return scroll;

	}

}
