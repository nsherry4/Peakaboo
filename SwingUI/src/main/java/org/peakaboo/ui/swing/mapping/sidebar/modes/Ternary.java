package org.peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.controller.mapper.fitting.modes.TernaryModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.panels.SettingsPanel;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.stencil.StencilCellEditor;
import org.peakaboo.framework.stratus.components.stencil.StencilTableCellRenderer;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;

public class Ternary extends JPanel {

	private MapFittingController viewController;
	
	public Ternary(MapFittingController viewController) {

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
		
		JPanel options = new JPanel(new BorderLayout());
		
		
		JCheckBox clip = new JCheckBox();
		clip.setBorder(Spacing.bMedium());
		clip.addActionListener(e -> viewController.ternaryMode().setClip(clip.isSelected()));
		
		JSpinner bins = new JSpinner(new SpinnerNumberModel(100, 25, 250, 1));
		bins.addChangeListener(change -> viewController.ternaryMode().setBins((Integer)bins.getValue()));
		viewController.ternaryMode().addListener(() -> {
			int oldValue = (Integer)bins.getValue();
			int newValue = viewController.ternaryMode().getBins();
			if (oldValue != newValue) {
				bins.setValue(newValue);
			}
		});

		SettingsPanel settings = new SettingsPanel();
		settings.setBorder(Spacing.bMedium());
		settings.addSetting(bins, "Granularity");
		settings.addSetting(clip, "Clip Outliers");		
		options.add(settings, BorderLayout.NORTH);
		
		
				
		return options;
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

					viewController.ternaryMode().setVisibility(ts, bvalue);
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
					case 0: return viewController.ternaryMode().getVisibility(ts);
					case 1: return ts;
					case 2: return ts;
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
					case 2: return "Correlation Set";
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
					case 2: return ITransitionSeries.class;
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

		
		
		AxisRenderer renderer = new AxisRenderer(new AxisWidget(viewController));
		AxisEditor editor = new AxisEditor(new AxisWidget(viewController));
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(renderer);
		column.setCellEditor(editor);
		column.setResizable(false);
		column.setPreferredWidth(90);
		column.setMaxWidth(90);

		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));
		scroll.setBorder(Spacing.bNone());
		
		return scroll;

	}
	

	class AxisWidget extends Stencil<ITransitionSeries> {
	
		FluentToggleButton groupX, groupY, groupO;
		ButtonGroup group;
		ButtonLinker linker;
		MapFittingController controller;
		
		ITransitionSeries ts;
		
		public AxisWidget(MapFittingController controller) {
			this.controller = controller;
			
			groupX = new FluentToggleButton(TernaryModeController.X_AXIS_LABEL).withButtonSize(FluentButtonSize.COMPACT);
			groupY = new FluentToggleButton(TernaryModeController.Y_AXIS_LABEL).withButtonSize(FluentButtonSize.COMPACT);
			groupO = new FluentToggleButton(TernaryModeController.O_AXIS_LABEL).withButtonSize(FluentButtonSize.COMPACT);
			groupX.setPreferredSize(new Dimension(26, 26));
			groupY.setPreferredSize(new Dimension(26, 26));
			groupO.setPreferredSize(new Dimension(26, 26));
			group = new ButtonGroup();
			group.add(groupX);
			group.add(groupY);
			group.add(groupO);
			linker = new ButtonLinker(groupX, groupY, groupO);
			
			Runnable onSelect = () -> {
				setFonts();
				controller.ternaryMode().setSide(ts, getSide());
			};
			groupO.withAction(onSelect);
			groupX.withAction(onSelect);
			groupY.withAction(onSelect);
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1f, 1f, GridBagConstraints.CENTER, GridBagConstraints.NONE, Spacing.iNone(), 0, 0);
			this.add(linker, c);
		}
		
		@Override
		public void setForeground(Color fg) {
			super.setForeground(fg);
			if (groupX != null) {
				groupX.setForeground(fg);
				groupY.setForeground(fg);
				groupO.setForeground(fg);
			}
		}
		
		@Override
		public void setBackground(Color bg) {
			super.setBackground(bg);
			if (groupX != null) {
				groupX.setBackground(bg);
				groupY.setBackground(bg);
				groupO.setBackground(bg);
			}
		}

		private int getSide() {
			if (groupX.isSelected()) { return 1; }
			if (groupY.isSelected()) { return 2; }
			return 3;
		}
		
		private void setFonts() {
			groupX.setFont(groupX.getFont().deriveFont(Font.PLAIN));
			groupY.setFont(groupY.getFont().deriveFont(Font.PLAIN));
			groupO.setFont(groupO.getFont().deriveFont(Font.PLAIN));
			
			int side = getSide();
			FluentToggleButton selected = buttonForSide(side);
			selected.setFont(selected.getFont().deriveFont(Font.BOLD));

		}
		
		@Override
		protected void onSetValue(ITransitionSeries ts, boolean selected) {
			this.ts = ts;
			linker.setVisible(controller.ternaryMode().getVisibility(ts));
			
			int side = controller.ternaryMode().getSide(ts);
			buttonForSide(side).setSelected(true);
			setFonts();		
		}

		private FluentToggleButton buttonForSide(int side) {
			switch (side) {
			case 1: return groupX;
			case 2: return groupY;
			case 3: return groupO;
			default: throw new RuntimeException("Unknown ternary plot group");
			}
		}
		
	}

	
	class AxisRenderer extends StencilTableCellRenderer<ITransitionSeries> {
	
		public AxisRenderer(Stencil<ITransitionSeries> widget) {
			super(widget);
		}
		
	}
	
	class AxisEditor extends StencilCellEditor<ITransitionSeries> {
	
		public AxisEditor(Stencil<ITransitionSeries> widget) {
			super(widget);
		}
		
	}
}
