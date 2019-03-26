package org.peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.controls.ButtonLinker;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonSize;
import org.peakaboo.framework.swidget.widgets.buttons.ToggleImageButton;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidget;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetCellEditor;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetListCellRenderer;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetTableCellRenderer;
import org.peakaboo.ui.swing.mapping.colours.ColourComboTableCellRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;


public class Correlation extends JPanel {

	private MapFittingController viewController;

	
	public Correlation(MapFittingController viewController) {

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
		ScaleModeWidget scaleMode = new ScaleModeWidget(viewController, "Set", "Axis", false);
		return scaleMode;
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

					viewController.setTransitionSeriesVisibility(ts, bvalue);
				} 

			}

			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

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

					case 0: return viewController.getTransitionSeriesVisibility(ts);
					case 1: return ts;
					case 2: return ts;
				}

				return null;

			}

			public int getRowCount() {
				return viewController.getAllTransitionSeries().size();
			}

			public String getColumnName(int columnIndex) {
				
				switch (columnIndex)
				{
					case 0:	return "Map";
					case 1: return "Fitting";
					case 2: return "Correlation Set";
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
					case 2: return ITransitionSeries.class;
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
		
		MapFittingRenderer fitRenderer = new MapFittingRenderer(viewController::getTransitionSeriesEnabled);
		table.getColumnModel().getColumn(1).setCellRenderer(fitRenderer);
		table.setRowHeight(fitRenderer.getPreferredSize().height);
		
		
		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setResizable(false);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);

		
		
		AxisRenderer renderer = new AxisRenderer(new AxisWidget(viewController));
		AxisEditor editor = new AxisEditor(new AxisWidget(viewController));
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);
		column.setResizable(false);
		column.setPreferredWidth(60);
		column.setMaxWidth(60);
		
		/*ComboTableCellRenderer renderer = new ComboTableCellRenderer();
		
		comboBox.setRenderer(renderer);
		
		
		

		*/
		
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));
		scroll.setBorder(Spacing.bNone());
		
		return scroll;

	}

}

class AxisWidget extends ListWidget<ITransitionSeries> {

	ToggleImageButton group1, group2;
	ButtonGroup group;
	ButtonLinker linker;
	MapFittingController controller;
	
	ITransitionSeries ts;
	
	public AxisWidget(MapFittingController controller) {
		this.controller = controller;
		
		group1 = new ToggleImageButton("X").withButtonSize(ImageButtonSize.COMPACT);
		group2 = new ToggleImageButton("Y").withButtonSize(ImageButtonSize.COMPACT);
		group1.setPreferredSize(new Dimension(26, 26));
		group2.setPreferredSize(new Dimension(26, 26));
		group = new ButtonGroup();
		group.add(group1);
		group.add(group2);
		linker = new ButtonLinker(group1, group2);
		
		Runnable onSelect = () -> {
			setFonts();
			controller.setCorrelationSide(ts, getSide());
		};
		group1.withAction(onSelect);
		group2.withAction(onSelect);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1f, 1f, GridBagConstraints.CENTER, GridBagConstraints.NONE, Spacing.iNone(), 0, 0);
		this.add(linker, c);
	}
	
	private int getSide() {
		return group1.isSelected() ? 1 : 2;
	}
	
	private void setFonts() {
		if (getSide() == 1) {
			group1.setFont(group1.getFont().deriveFont(Font.BOLD));
			group2.setFont(group2.getFont().deriveFont(Font.PLAIN));
		} else {
			group2.setFont(group2.getFont().deriveFont(Font.BOLD));
			group1.setFont(group1.getFont().deriveFont(Font.PLAIN));
		}
	}
	
	@Override
	protected void onSetValue(ITransitionSeries ts) {
		this.ts = ts;
		linker.setVisible(controller.getTransitionSeriesVisibility(ts));
			
		if (controller.getCorrelationSide(ts) == 1) {
			group1.setSelected(true);
		} else {
			group2.setSelected(true);
		}
		setFonts();		
	}
	
}

class AxisRenderer extends ListWidgetTableCellRenderer<ITransitionSeries> {

	public AxisRenderer(ListWidget<ITransitionSeries> widget) {
		super(widget);
	}
	
}

class AxisEditor extends ListWidgetCellEditor<ITransitionSeries> {

	public AxisEditor(ListWidget<ITransitionSeries> widget) {
		super(widget);
	}
	
}