package peakaboo.ui.swing.mapping;

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

import cyclops.ReadOnlySpectrum;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.calibration.Concentrations;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.settings.AreaSelection;
import peakaboo.controller.mapper.settings.MapViewSettings;
import peakaboo.controller.mapper.settings.PointsSelection;
import peakaboo.controller.settings.SavedSession;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.datasource.model.internal.SubsetDataSource;
import peakaboo.ui.swing.Peakaboo;
import peakaboo.ui.swing.calibration.concentration.ConcentrationView;
import peakaboo.ui.swing.plotting.PlotPanel;
import peakaboo.ui.swing.plotting.toolbar.PlotMenuExport;
import peakaboo.ui.swing.plotting.toolbar.PlotMenuView;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.buttons.ToolbarImageButton;

class MapperToolbar extends JToolBar {

	private ToolbarImageButton	showConcentrations, examineSubset;
	
	private JCheckBoxMenuItem	monochrome, logview;
	private JMenuItem			title, spectrum, coords, dstitle, scalebar;
	
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
				
				List<Integer> indexes = new ArrayList<>();
				
				AreaSelection areaSelection = controller.getSettings().getAreaSelection();
				PointsSelection pointsSelection = controller.getSettings().getPointsSelection();
				
				if (areaSelection.hasSelection()) {
					indexes.addAll(areaSelection.getPoints());
				} else if (pointsSelection.hasSelection()) {
					indexes.addAll(pointsSelection.getPoints());
				}
				
				List<ITransitionSeries> tss = controller.mapsController.getAreaMapSet().stream().map(r -> r.transitionSeries).collect(toList());
				Function<ITransitionSeries, Float> intensityFunction = ts -> {
					CalibrationProfile profile = controller.getSettings().getMapFittings().getCalibrationProfile();
					ReadOnlySpectrum data = controller.mapsController.getAreaMapSet().getMap(ts).getData(profile);
					float sum = 0;
					for (int index : indexes) {
						sum += data.get(index);
					}
					return sum /= indexes.size();
				};
				Concentrations ppm = Concentrations.calculate(tss, controller.getSettings().getMapFittings().getCalibrationProfile(), intensityFunction);
				
				ConcentrationView concentrations = new ConcentrationView(ppm, panel);
				panel.pushLayer(concentrations);
								
			});
			this.add(showConcentrations, c);
			c.gridx++;
		}
		
		
		examineSubset = new ToolbarImageButton("Plot Selection", "view-subset");
		examineSubset.withSignificance(true).withTooltip("Plot the selection as a new data set");
		
		examineSubset.addActionListener(e -> {
			
			AreaSelection areaSelection = controller.getSettings().getAreaSelection();
			PointsSelection pointSelection = controller.getSettings().getPointsSelection();
			
			SubsetDataSource sds;
			if (areaSelection.hasSelection()) {
				sds = controller.getDataSourceForSubset(areaSelection.getStart(), areaSelection.getEnd());
			} else {
				sds = controller.getDataSourceForSubset(pointSelection.getPoints());
			}
			
			SavedSession settings = controller.getSavedSettings();
			
			//update the bad scan indexes to match the new data source's indexing scheme
			//TODO: Is there a better way to do this?
			settings.data.discards = settings.data.discards.stream()
					.map(index -> sds.getUpdatedIndex(index))
					.filter(index -> index > 0)
					.collect(Collectors.toList()
				);
		
			PlotPanel subplot = new PlotPanel(panel.parentPlotter);
			subplot.loadExistingDataSource(sds, settings.serialize());
			panel.parentPlotter.addActiveTab(subplot);
			//Focus and un-minimize
			JFrame plotWindow = panel.parentPlotter.getWindow();
			plotWindow.toFront();
			int windowState = plotWindow.getExtendedState();
			if ((windowState & JFrame.ICONIFIED) == JFrame.ICONIFIED) {
				plotWindow.setExtendedState(windowState ^ JFrame.ICONIFIED);
			}
						
		});
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
			logview.setSelected(controller.getSettings().getMapFittings().isLogView());
			monochrome.setSelected(controller.getSettings().getView().getMonochrome());
			spectrum.setSelected(controller.getSettings().getView().getShowSpectrum());
			coords.setSelected(controller.getSettings().getView().getShowCoords());
			
			if (controller.getSettings().getAreaSelection().hasSelection() || controller.getSettings().getPointsSelection().hasSelection())
			{
				if (Peakaboo.SHOW_QUANTITATIVE) showConcentrations.setEnabled(!controller.getSettings().getMapFittings().getCalibrationProfile().isEmpty());
				examineSubset.setEnabled(true);
			} else {
				if (Peakaboo.SHOW_QUANTITATIVE) showConcentrations.setEnabled(false);
				examineSubset.setEnabled(false);
			}

		});
		
		
		
	}
	

	private ToolbarImageButton createOptionsButton(MapperPanel panel, MappingController controller) {
		
		ToolbarImageButton opts = new ToolbarImageButton();
		opts.withIcon("menu-view").withTooltip("Map Settings Menu");
		
		JPopupMenu menu = new JPopupMenu();
		
		title = new JCheckBoxMenuItem("Show Elements List");
		dstitle = new JCheckBoxMenuItem("Show Dataset Title");
		spectrum = new JCheckBoxMenuItem("Show Spectrum");
		coords = new JCheckBoxMenuItem("Show Coordinates");
		scalebar = new JCheckBoxMenuItem("Show Scale Bar");
		monochrome = new JCheckBoxMenuItem("Monochrome");
		logview = new JCheckBoxMenuItem("Log Scale");
		

		MapViewSettings viewSettings = controller.getSettings().getView();
		title.setSelected(viewSettings.getShowTitle());
		spectrum.setSelected(viewSettings.getShowSpectrum());
		coords.setSelected(viewSettings.getShowCoords());
		dstitle.setSelected(viewSettings.getShowDatasetTitle());
		scalebar.setSelected(viewSettings.getShowScaleBar());

		spectrum.addActionListener(e -> viewSettings.setShowSpectrum(spectrum.isSelected()));
		coords.addActionListener(e -> viewSettings.setShowCoords(coords.isSelected()));
		title.addActionListener(e -> viewSettings.setShowTitle(title.isSelected()));
		dstitle.addActionListener(e -> viewSettings.setShowDatasetTitle(dstitle.isSelected()));
		scalebar.addActionListener(e -> viewSettings.setShowScaleBar(scalebar.isSelected()));
		monochrome.addActionListener(e -> viewSettings.setMonochrome(monochrome.isSelected()));
		logview.addActionListener(e -> controller.getSettings().getMapFittings().setLogView(logview.isSelected()));
		
		
		menu.add(title);
		menu.add(dstitle);
		menu.add(spectrum);
		menu.add(coords);
		menu.add(scalebar);
		menu.addSeparator();
		menu.add(logview);
		menu.add(monochrome);

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
