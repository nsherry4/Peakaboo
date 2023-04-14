package org.peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
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
import org.peakaboo.controller.mapper.fitting.modes.CorrelationModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.correlation.CorrelationMapMode;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.panels.SettingsPanel;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.stencil.StencilCellEditor;
import org.peakaboo.framework.stratus.components.stencil.StencilTableCellRenderer;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;


class CorrelationUI extends JPanel {

	private MapFittingController viewController;

	
	public CorrelationUI(MapFittingController viewController) {

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
	
	private CorrelationModeController modeController() {
		return (CorrelationModeController) viewController.getModeController(CorrelationMapMode.MODE_NAME).get();
	}
	
	private JPanel createScaleOptions() {
		JPanel options = new JPanel(new BorderLayout());
		
		
		JCheckBox clip = new JCheckBox();
		clip.setBorder(Spacing.bMedium());
		clip.addActionListener(e -> modeController().setClip(clip.isSelected()));
		
		JSpinner bins = new JSpinner(new SpinnerNumberModel(100, 25, 250, 1));
		bins.addChangeListener(change -> modeController().setBins((Integer)bins.getValue()));
		modeController().addListener(() -> {
			int oldValue = (Integer)bins.getValue();
			int newValue = modeController().getBins();
			if (oldValue != newValue) {
				bins.setValue(newValue);
			}
		});
		
		ScaleModeWidget scaleMode = new ScaleModeWidget(viewController, "Axis", "All", false);
		
		
		SettingsPanel settings = new SettingsPanel();
		settings.setBorder(Spacing.bMedium());
		settings.addSetting(bins, "Granularity");
		settings.addSetting(clip, "Clip Outliers");		
		options.add(scaleMode, BorderLayout.CENTER);
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

					modeController().setVisibility(ts, bvalue);
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
		column.setPreferredWidth(60);
		column.setMaxWidth(60);

		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));
		scroll.setBorder(Spacing.bNone());
		
		return scroll;

	}
		
	
	class AxisWidget extends Stencil<ITransitionSeries> {
	
		FluentToggleButton group1, group2;
		ButtonGroup group;
		ButtonLinker linker;
		MapFittingController controller;
		
		ITransitionSeries ts;
		
		public AxisWidget(MapFittingController controller) {
			this.controller = controller;
			
			group1 = new FluentToggleButton("X").withButtonSize(FluentButtonSize.COMPACT);
			group2 = new FluentToggleButton("Y").withButtonSize(FluentButtonSize.COMPACT);
			group1.setPreferredSize(new Dimension(26, 26));
			group2.setPreferredSize(new Dimension(26, 26));
			group = new ButtonGroup();
			group.add(group1);
			group.add(group2);
			linker = new ButtonLinker(group1, group2);
			
			Runnable onSelect = () -> {
				setFonts();
				modeController().setSide(ts, getSide());
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
		protected void onSetValue(ITransitionSeries ts, boolean selected) {
			this.ts = ts;
			linker.setVisible(modeController().getVisibility(ts));
			
			if (selected) {
				group1.setForeground(Stratus.getTheme().getHighlightText());
				group2.setForeground(Stratus.getTheme().getHighlightText());
			} else {
				group1.setForeground(Stratus.getTheme().getControlText());
				group2.setForeground(Stratus.getTheme().getControlText());
			}
			
			if (modeController().getSide(ts) == 1) {
				group1.setSelected(true);
			} else {
				group2.setSelected(true);
			}
			setFonts();		
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
