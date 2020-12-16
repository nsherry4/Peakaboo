package org.peakaboo.ui.swing.mapping.components;

import static java.util.stream.Collectors.toList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.Function;

import javax.swing.Box;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.calibration.Concentrations;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentToolbarButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.calibration.concentration.ConcentrationView;
import org.peakaboo.ui.swing.mapping.MapperPanel;

public class MapperToolbar extends JToolBar {

	private FluentToolbarButton	showConcentrations, examineSubset;

	public MapperToolbar(MapperPanel panel, MappingController controller) {


		this.setFloatable(false);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(2, 2, 2, 2);
		
		FluentToolbarButton export = createExportMenuButton(panel);
		this.add(export, c);
		c.gridx++;
		
		this.add(new JToolBar.Separator( null ), c);
		c.gridx++;
		
		
		if (Peakaboo.SHOW_QUANTITATIVE)  {
			showConcentrations = new FluentToolbarButton("Concentration")
					.withIcon("calibration", IconSize.TOOLBAR_SMALL)
					.withTooltip("Get fitting concentration for the selection")
					.withSignificance(true)
					.withAction(() -> {
				
						List<Integer> indexes = controller.getSelection().getLogicalPoints();
		
						List<ITransitionSeries> tss = controller.rawDataController.getMapResultSet().stream().map(r -> r.transitionSeries).collect(toList());
						Function<ITransitionSeries, Float> intensityFunction = ts -> {
							CalibrationProfile profile = controller.getFitting().getCalibrationProfile();
							ReadOnlySpectrum data = controller.rawDataController.getMapResultSet().getMap(ts).getData(profile);
							float sum = 0;
							for (int index : indexes) {
								sum += data.get(index);
							}
							sum /= indexes.size();
							return sum;
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
		
		this.add(createOptionsButton(controller), c);
		c.gridx++;
		
		
		
		
		
		controller.addListener(t -> {
			
			examineSubset.setEnabled(controller.getSelection().isReplottable());
			if (controller.getSelection().isReplottable())
			{
				if (Peakaboo.SHOW_QUANTITATIVE) showConcentrations.setEnabled(!controller.getFitting().getCalibrationProfile().isEmpty());
			} else {
				if (Peakaboo.SHOW_QUANTITATIVE) showConcentrations.setEnabled(false);
			}

		});
		
		
		
	}
	

	public static FluentToolbarButton createOptionsButton(MappingController controller) {
		
		JPopupMenu menu = new MapMenuView(controller);
		
		FluentToolbarButton opts = new FluentToolbarButton()
				.withIcon("menu-view")
				.withTooltip("Map Settings Menu");
		
		opts.withAction(() -> {
			int x = (int)(opts.getWidth() - menu.getPreferredSize().getWidth());
			int y = opts.getHeight();
			menu.show(opts, x, y);
		});
		
		return opts;
	}
	
	
	private FluentToolbarButton createExportMenuButton(MapperPanel panel) {
		FluentToolbarButton exportMenuButton = new FluentToolbarButton().withIcon(StockIcon.DOCUMENT_EXPORT).withTooltip("Export Maps");
		JPopupMenu exportMenu = new MapMenuExport(panel);
		exportMenuButton.withAction(() -> exportMenu.show(exportMenuButton, 0, exportMenuButton.getHeight()));
		return exportMenuButton;
	}
	
}
