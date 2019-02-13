package org.peakaboo.ui.swing.mapping;

import static java.util.stream.Collectors.toList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.calibration.Concentrations;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.selection.AreaSelection;
import org.peakaboo.controller.mapper.selection.PointsSelection;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.controller.plotter.SavedSession;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.calibration.concentration.ConcentrationView;
import org.peakaboo.ui.swing.mapping.controls.PlotSelectionButton;
import org.peakaboo.ui.swing.plotting.PlotPanel;

import cyclops.ReadOnlySpectrum;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.buttons.ToolbarImageButton;
import swidget.widgets.layerpanel.LayerPanel;

class MapperToolbar extends JToolBar {

	private ToolbarImageButton	showConcentrations, examineSubset;

	MapperToolbar(MapperPanel panel, MappingController controller) {


		this.setFloatable(false);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(2, 2, 2, 2);
		
		ToolbarImageButton export = createExportMenuButton(panel);
		this.add(export, c);
		c.gridx++;
		
		this.add(new JToolBar.Separator( null ), c);
		c.gridx++;
		
		
		if (Peakaboo.SHOW_QUANTITATIVE)  {
			showConcentrations = new ToolbarImageButton("Concentration")
					.withIcon("calibration", IconSize.TOOLBAR_SMALL)
					.withTooltip("Get fitting concentration for the selection")
					.withSignificance(true);
			
			showConcentrations.addActionListener(e -> {
				
				List<Integer> indexes = controller.getSelection().getPoints();

				List<ITransitionSeries> tss = controller.rawDataController.getMapResultSet().stream().map(r -> r.transitionSeries).collect(toList());
				Function<ITransitionSeries, Float> intensityFunction = ts -> {
					CalibrationProfile profile = controller.getFitting().getCalibrationProfile();
					ReadOnlySpectrum data = controller.rawDataController.getMapResultSet().getMap(ts).getData(profile);
					float sum = 0;
					for (int index : indexes) {
						sum += data.get(index);
					}
					return sum /= indexes.size();
				};
				Concentrations ppm = Concentrations.calculate(tss, controller.getFitting().getCalibrationProfile(), intensityFunction);
				
				ConcentrationView concentrations = new ConcentrationView(ppm, panel);
				panel.pushLayer(concentrations);
								
			});
			this.add(showConcentrations, c);
			c.gridx++;
		}
		
		
		examineSubset = new PlotSelectionButton(controller, panel.getParentPlotter());
		this.add(examineSubset, c);
		c.gridx++;
		
		
		if (Peakaboo.SHOW_QUANTITATIVE) showConcentrations.setEnabled(false);
		examineSubset.setEnabled(false);
		
		c.weightx = 1.0;
		this.add(Box.createHorizontalGlue(), c);
		c.weightx = 0.0;
		c.gridx++;
		
		this.add(createOptionsButton(panel, controller), c);
		c.gridx++;
		
		
		
		
		
		controller.addListener(s -> {
			
			if (controller.getSelection().hasSelection())
			{
				if (Peakaboo.SHOW_QUANTITATIVE) showConcentrations.setEnabled(!controller.getFitting().getCalibrationProfile().isEmpty());
				examineSubset.setEnabled(true);
			} else {
				if (Peakaboo.SHOW_QUANTITATIVE) showConcentrations.setEnabled(false);
				examineSubset.setEnabled(false);
			}

		});
		
		
		
	}
	

	public static ToolbarImageButton createOptionsButton(LayerPanel panel, MappingController controller) {
		
		ToolbarImageButton opts = new ToolbarImageButton();
		opts.withIcon("menu-view").withTooltip("Map Settings Menu");
		JPopupMenu menu = new MapMenuView(controller);
		opts.addActionListener(e -> menu.show(opts, (int)(opts.getWidth() - menu.getPreferredSize().getWidth()), opts.getHeight()));
		
		return opts;
	}
	
	
	private ToolbarImageButton createExportMenuButton(MapperPanel panel) {
		ToolbarImageButton exportMenuButton = new ToolbarImageButton().withIcon(StockIcon.DOCUMENT_EXPORT).withTooltip("Export Maps");
		JPopupMenu exportMenu = new MapMenuExport(panel);
		exportMenuButton.addActionListener(e -> exportMenu.show(exportMenuButton, 0, exportMenuButton.getHeight()));
		return exportMenuButton;
	}
	
}
