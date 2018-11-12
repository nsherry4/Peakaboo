package peakaboo.ui.swing.mapping;

import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import peakaboo.calibration.CalibrationProfile;
import peakaboo.common.PeakabooLog;
import cyclops.Pair;
import cyclops.ReadOnlySpectrum;
import cyclops.SigDigits;

import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.settings.AreaSelection;
import peakaboo.controller.mapper.settings.MapViewSettings;
import peakaboo.controller.mapper.settings.PointsSelection;
import peakaboo.controller.settings.SavedSession;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.datasource.model.internal.SubsetDataSource;
import peakaboo.mapping.Mapping;
import peakaboo.mapping.correction.Corrections;
import peakaboo.mapping.correction.CorrectionsManager;
import peakaboo.mapping.results.MapResult;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ToolbarImageButton;
import swidget.widgets.layerpanel.LayerDialog;
import swidget.widgets.layerpanel.ModalLayer;
import swidget.widgets.layout.ButtonBox;
import swidget.widgets.layout.PropertyPanel;
import swidget.widgets.layout.TitledPanel;

class MapperToolbar extends JToolBar {

	private ToolbarImageButton	readIntensities, examineSubset;
	
	private JCheckBoxMenuItem	monochrome, logview;
	private JMenuItem			title, spectrum, coords, dstitle;
	
	MapperToolbar(MapperPanel panel, MappingController controller) {


		this.setFloatable(false);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(4, 4, 4, 4);
		
		ToolbarImageButton savePicture = new ToolbarImageButton("Save Image", StockIcon.DEVICE_CAMERA).withTooltip("Save the current map as an image");
		savePicture.addActionListener(e -> panel.actionSavePicture());
		this.add(savePicture, c);
		c.gridx++;
		
		
		ToolbarImageButton saveText = new ToolbarImageButton("Export as Text", StockIcon.DOCUMENT_EXPORT).withTooltip("Export the current map as a comma separated value file");
		saveText.addActionListener(e -> panel.actionSaveCSV());
		this.add(saveText, c);
		c.gridx++;
		
		
		this.addSeparator();
		
		
		readIntensities = new ToolbarImageButton("Concentrations")
				.withIcon("calibration", IconSize.TOOLBAR_SMALL)
				.withTooltip("Get fitting concentrations for the selection")
				.withSignificance(true);
		
		readIntensities.addActionListener(e -> {
			
			Map<String, String> fittings = new HashMap<String, String>();
			List<Integer> indexes = new ArrayList<>();
			
			AreaSelection areaSelection = controller.getSettings().getAreaSelection();
			PointsSelection pointsSelection = controller.getSettings().getPointsSelection();
			
			if (areaSelection.hasSelection()) {
				indexes.addAll(areaSelection.getPoints());
			} else if (pointsSelection.hasSelection()) {
				indexes.addAll(pointsSelection.getPoints());
			}
			
			List<TransitionSeries> tss = controller.mapsController.getMapResultSet().stream().map(r -> r.transitionSeries).collect(toList());
			Function<TransitionSeries, Float> intensityFunction = ts -> {
				CalibrationProfile profile = controller.getSettings().getMapFittings().getCalibrationProfile();
				ReadOnlySpectrum data = controller.mapsController.getMapResultSet().getMap(ts).getData(profile);
				float sum = 0;
				for (int index : indexes) {
					sum += data.get(index);
				}
				return sum /= indexes.size();
			};
			Map<Element, Float> ppm = Mapping.concentrations(tss, intensityFunction);
			
			Map<String, String> properties = new LinkedHashMap<>();
			NumberFormat format = new DecimalFormat("0.0");
			for (TransitionSeries ts : tss) {
				properties.put(ts.element.toString(), format.format(ppm.get(ts.element) / 10000) + "%" );
			}
			
			LayerDialog dialog = new LayerDialog("Element Concentrations", new PropertyPanel(properties), LayerDialog.MessageType.INFO);
			dialog.showIn(panel);
			
//			List<Pair<TransitionSeries, Float>> averages = controller.mapsController.getMapResultSet().stream().map((MapResult r) -> {
//				float sum = 0;
//				ReadOnlySpectrum data = r.getData(controller.getSettings().getMapFittings().getCalibrationProfile());
//				for (int index : indexes) {
//					sum += data.get(index);
//				}
//				return new Pair<TransitionSeries, Float>(r.transitionSeries, sum / indexes.size());
//			}).collect(toList());
//			
//			
//			//get the total of all of the corrected values
//			float total = averages.stream().map(p -> p.second).reduce(0f, (a, b) -> a + b);
//			
//			for (Pair<TransitionSeries, Float> p : averages)
//			{
//				float average = p.second;
//				Float corrFactor = corr.getCorrection(p.first);
//				String corrected = "(-)";
//				if (corrFactor != null) corrected = "(~" + SigDigits.toIntSigDigit((average*corrFactor/total*100), 1) + "%)";
//				
//				fittings.put(p.first.getDescription(), SigDigits.roundFloatTo(average, 2) + " " + corrected);
//			}
//			
//			TitledPanel correctionsPanel = new TitledPanel(new PropertyPanel(fittings));
//			
//			
//			JPanel corrections = new JPanel(new BorderLayout());
//			JPanel contentPanel = new JPanel(new BorderLayout());
//			corrections.add(contentPanel, BorderLayout.CENTER);
//			
//			contentPanel.add(new JLabel("Concentrations accurate to a factor of 5", JLabel.CENTER), BorderLayout.SOUTH);
//			contentPanel.add(correctionsPanel, BorderLayout.CENTER);
//			contentPanel.setBorder(Spacing.bHuge());
//			
//			ButtonBox bbox = new ButtonBox();
//			ImageButton close = new ImageButton("Close").withIcon(StockIcon.WINDOW_CLOSE).withTooltip("Close this window").withBordered(true);
//			close.addActionListener(new ActionListener() {
//				
//				public void actionPerformed(ActionEvent e)
//				{
//					panel.popLayer();
//				}
//			});
//			bbox.addRight(close);
//			corrections.add(bbox, BorderLayout.SOUTH);
//			
//			
//			panel.pushLayer(new ModalLayer(panel, corrections));
//			
				
				
		});
		this.add(readIntensities, c);
		c.gridx++;
		
		
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
		
		
		readIntensities.setEnabled(false);
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
				readIntensities.setEnabled(true);
				examineSubset.setEnabled(true);
			} else {
				readIntensities.setEnabled(false);
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
		monochrome = new JCheckBoxMenuItem("Monochrome");
		logview = new JCheckBoxMenuItem("Log Scale");

		MapViewSettings viewSettings = controller.getSettings().getView();
		title.setSelected(viewSettings.getShowTitle());
		spectrum.setSelected(viewSettings.getShowSpectrum());
		coords.setSelected(viewSettings.getShowCoords());
		dstitle.setSelected(viewSettings.getShowDatasetTitle());

		spectrum.addActionListener(e -> viewSettings.setShowSpectrum(spectrum.isSelected()));
		coords.addActionListener(e -> viewSettings.setShowCoords(coords.isSelected()));
		title.addActionListener(e -> viewSettings.setShowTitle(title.isSelected()));
		dstitle.addActionListener(e -> viewSettings.setShowDatasetTitle(dstitle.isSelected()));
		monochrome.addActionListener(e -> viewSettings.setMonochrome(monochrome.isSelected()));
		logview.addActionListener(e -> controller.getSettings().getMapFittings().setLogView(logview.isSelected()));
		
		
		menu.add(title);
		menu.add(dstitle);
		menu.add(spectrum);
		menu.add(coords);
		menu.addSeparator();
		menu.add(logview);
		menu.add(monochrome);

		opts.addActionListener(e -> menu.show(opts, (int)(opts.getWidth() - menu.getPreferredSize().getWidth()), opts.getHeight()));
		
		return opts;
	}
	
}
