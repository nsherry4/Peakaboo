package org.peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.controller.mapper.fitting.modes.RatioModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.ratio.RatioColour;
import org.peakaboo.display.map.modes.ratio.RatioMapMode;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.stencil.StencilCellEditor;
import org.peakaboo.framework.stratus.components.stencil.StencilTableCellRenderer;
import org.peakaboo.framework.stratus.components.ui.colour.ColourChooser;
import org.peakaboo.framework.stratus.components.ui.colour.ColourView;
import org.peakaboo.framework.stratus.components.ui.colour.ColourView.Settings;
import org.peakaboo.ui.swing.mapping.sidebar.MapFittingRenderer;
import org.peakaboo.ui.swing.mapping.sidebar.ScaleModeWidget;


class RatioUI extends JPanel {

	private MapFittingController viewController;

	
	public RatioUI(MapFittingController viewController) {

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

	
	private RatioModeController modeController() {
		return (RatioModeController) viewController.getModeController(RatioMapMode.MODE_NAME).get();
	}
	
	private JPanel createScaleOptions() {
		return new ScaleModeWidget(viewController, "Colour", "All", true);
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
					case 2: return "Ratio Sets";
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

		
		RatioRenderer renderer = new RatioRenderer(new RatioWidget(viewController));
		RatioEditor editor = new RatioEditor(new RatioWidget(viewController));
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
	

	class RatioWidget extends Stencil<ITransitionSeries> {
	
		
		
		Color cRed = new Color(RatioColour.RED.toARGB(), true);
		Color cBlue = new Color(RatioColour.BLUE.toARGB(), true);
		ColourChooser chooser;
		
		ITransitionSeries ts;
		MapFittingController controller;
		
		public RatioWidget(MapFittingController controller) {
			this.controller = controller;
			
			chooser = new ColourChooser(List.of(cRed, cBlue), cRed, 2, false, new Settings(24, 2f, ColourView.DEFAULT_PAD));
			chooser.addItemListener(i -> {
				var mc = modeController();
				var newside = getSide();
				var oldside = mc.getSide(ts);
				if (newside != oldside) {
					modeController().setSide(ts, getSide());
				}
			});
			
			this.setLayout(new BorderLayout());
			this.add(chooser, BorderLayout.CENTER);
		}
		
		private int getSide() {
			return chooser.getSelected() == cRed ? 1 : 2;
		}

		
		@Override
		protected void onSetValue(ITransitionSeries ts, boolean selected) {
			this.ts = ts;
			chooser.setVisible(modeController().getVisibility(ts));

			if (modeController().getSide(ts) == 1) {
				chooser.setSelected(cRed);
			} else {
				chooser.setSelected(cBlue);
			}
		}
		
	}
	
	
	class RatioRenderer extends StencilTableCellRenderer<ITransitionSeries> {
		
		public RatioRenderer(Stencil<ITransitionSeries> widget) {
			super(widget);
		}
		
	}
	
	class RatioEditor extends StencilCellEditor<ITransitionSeries> {
	
		public RatioEditor(Stencil<ITransitionSeries> widget) {
			super(widget);
		}
		
	}

}
