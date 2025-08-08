package org.peakaboo.ui.swing.options;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.peakaboo.app.Settings;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.detector.DetectorMaterial;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.SelfDescribing;
import org.peakaboo.framework.autodialog.view.swing.layouts.SwingLayoutFactory;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.cyclops.visualization.palette.Gradient;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.stencil.StencilListCellRenderer;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlocksPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionCheckBox;
import org.peakaboo.framework.stratus.components.ui.options.OptionColours;
import org.peakaboo.framework.stratus.components.ui.options.OptionCustomComponent;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;
import org.peakaboo.framework.stratus.components.ui.options.OptionSidebar;
import org.peakaboo.framework.stratus.components.ui.options.OptionSidebar.Entry;
import org.peakaboo.framework.stratus.components.ui.options.OptionSize;
import org.peakaboo.framework.stratus.laf.theme.Theme.Accent;
import org.peakaboo.mapping.Mapping;
import org.peakaboo.tier.Tier;
import org.peakaboo.tier.TierUIAutoGroup;
import org.peakaboo.ui.swing.app.DesktopSettings;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.mapping.components.MapMenuView;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class AdvancedOptionsPanel extends HeaderLayer {
	
	public AdvancedOptionsPanel(PlotPanel parent, PlotController controller) {
		super(parent, true);
		getHeader().setCentre("Advanced Options");
		
		ClearPanel body = new ClearPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500, 350);
			}

		};
		CardLayout cards = new CardLayout();
		body.setLayout(cards);
		body.setBorder(Spacing.bMedium());
		
		var hasdata = controller.data().hasDataSet();
		final String SETTING_PER_DATASET = "Per-Session Settings";
		final String SETTING_PER_USER = "Global Settings";
		
		String KEY_DETECTOR = "Detector";
		OptionBlocksPanel detectorPanel = makeDetectorPanel(controller);
		detectorPanel.setEnabled(hasdata);
		body.add(wrapSettingsInfo(detectorPanel, SETTING_PER_DATASET), KEY_DETECTOR);
		OptionSidebar.Entry detectorEntry = new OptionSidebar.Entry(KEY_DETECTOR, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_DETECTOR, IconSize.TOOLBAR_SMALL));
		
		String KEY_PEAKMODEL = "Peak Model";
		OptionBlocksPanel peakPanel = makePeakModelPanel(controller);
		peakPanel.setEnabled(hasdata);
		body.add(wrapSettingsInfo(peakPanel, SETTING_PER_DATASET), KEY_PEAKMODEL);
		OptionSidebar.Entry peakEntry = new OptionSidebar.Entry(KEY_PEAKMODEL, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_PEAKMODEL, IconSize.TOOLBAR_SMALL));
		
		String KEY_CURVEFIT = "Curve Fitting";
		OptionBlocksPanel curvefitPanel = makeCurvefitPanel(controller);
		curvefitPanel.setEnabled(hasdata);
		body.add(wrapSettingsInfo(curvefitPanel, SETTING_PER_DATASET), KEY_CURVEFIT);
		OptionSidebar.Entry curvefitEntry = new OptionSidebar.Entry(KEY_CURVEFIT, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_CURVEFIT, IconSize.TOOLBAR_SMALL));
		
		String KEY_OVERLAP = "Overlap Solving";
		OptionBlocksPanel overlapPanel = makeOverlapPanel(controller);
		overlapPanel.setEnabled(hasdata);
		body.add(wrapSettingsInfo(overlapPanel, SETTING_PER_DATASET), KEY_OVERLAP);
		OptionSidebar.Entry overlapEntry = new OptionSidebar.Entry(KEY_OVERLAP, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_SOLVER, IconSize.TOOLBAR_SMALL));
				
		var entries = new ArrayList<Entry>();
		entries.addAll(List.of(detectorEntry, peakEntry, curvefitEntry, overlapEntry));
		
		for (TierUIAutoGroup<PlotController> item : Tier.provider().getAdvancedOptions()) {
			Group group = item.getValue();
			String groupKey = group.getName();
			JComponent groupPanel = SwingLayoutFactory.forGroup(group).getComponent();
			body.add(wrapSettingsInfo(groupPanel, SETTING_PER_DATASET), groupKey);
			OptionSidebar.Entry itemEntry = new OptionSidebar.Entry(groupKey, IconFactory.getImageIcon(Tier.provider().iconPath(), item.getIconPath(), IconSize.TOOLBAR_SMALL));
			entries.add(itemEntry);
		}

		//separator between dataset-focused options and app options
		entries.get(entries.size()-1).trailingSeparator = true;
		
		
		String KEY_APP = "Appearance";
		OptionBlocksPanel appPanel = makeAppPanel(controller);
		body.add(wrapSettingsInfo(appPanel, SETTING_PER_USER), KEY_APP);
		OptionSidebar.Entry appEntry = new OptionSidebar.Entry(KEY_APP, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_APPEARANCE, IconSize.TOOLBAR_SMALL));
		entries.add(appEntry);
		
		
		String KEY_PERFORMANCE = "Performance";
		OptionBlocksPanel perfPanel = makePerformancePanel(controller);
		body.add(wrapSettingsInfo(perfPanel, SETTING_PER_USER), KEY_PERFORMANCE);
		OptionSidebar.Entry perfEntry = new OptionSidebar.Entry(KEY_PERFORMANCE, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_PERFORMANCE, IconSize.TOOLBAR_SMALL));
		entries.add(perfEntry);

		
		String KEY_ERRORS = "Errors";
		OptionBlocksPanel errorsPanel = makeErrorsPanel(controller);
		body.add(wrapSettingsInfo(errorsPanel, SETTING_PER_USER), KEY_ERRORS);
		OptionSidebar.Entry errorsEntry = new OptionSidebar.Entry(KEY_ERRORS, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_ERRORS, IconSize.TOOLBAR_SMALL));
		entries.add(errorsEntry);
		
		
		
		//Create the UI components from the model
		OptionSidebar sidebar = new OptionSidebar(entries, e -> {
			cards.show(body, e.getName());
		});
		sidebar.select(hasdata? detectorEntry : appEntry);
		
		ClearPanel outer = new ClearPanel(new BorderLayout());
		outer.add(body, BorderLayout.CENTER);
		outer.add(sidebar, BorderLayout.WEST);
		
		setBody(outer);
		
	}

	
	private OptionBlocksPanel makePerformancePanel(PlotController controller) {
		OptionBlock datasets = new OptionBlock();
		
		
		OptionBlock multithreading = new OptionBlock();
		OptionCustomComponent corecount = new OptionThreadCount(multithreading, Settings::getThreadCount, Settings::setThreadCount)
				.withText("Parallel Processing Threads", "Take advantage of multi-core CPUs, requires restart")
				.withSize(OptionSize.LARGE);
		multithreading.add(corecount);
		
		
		OptionCheckBox diskbacked = new OptionCheckBox(datasets)
				.withText("Disk Backing", "Stores datasets in a compressed temp file on disk, lowers memory use")
				.withSize(OptionSize.LARGE)
				.withSelection(Settings.isDiskstore())
				.withListener(Settings::setDiskstore);
		datasets.add(diskbacked);

		
		OptionBlock heapBlock = new OptionBlock();
		ButtonGroup heapGroup = new ButtonGroup();
		OptionRadioButton heapPercent = new OptionHeapSize(heapBlock, heapGroup, Settings::getHeapSizePercent, Settings::setHeapSizePercent)
				.withText("Java Heap Size (Percentage)", "Limit memory use to portion of total available")
				.withSize(OptionSize.LARGE)
				.withSelection(Settings.isHeapSizePercent())
				.withListener(() -> Settings.setHeapSizeIsPercent(true));
		heapBlock.add(heapPercent);
		OptionRadioButton heapMegabytes = new OptionHeapSize(heapBlock, heapGroup, Settings::getHeapSizeMegabytes, Settings::setHeapSizeMegabytes)
				.withText("Java Heap Size (Megabytes)", "Limit memory use to fixed amount")
				.withSize(OptionSize.LARGE)
				.withSelection(!Settings.isHeapSizePercent())
				.withListener(() -> Settings.setHeapSizeIsPercent(false));
		heapBlock.add(heapMegabytes);

				
		
		
		return new OptionBlocksPanel(multithreading, datasets, heapBlock);
	}
	
	private OptionBlocksPanel makeAppPanel(PlotController controller) {
	

		OptionBlock mapsBlock = new OptionBlock();
		
		var stencil = new Stencil<Gradient>() {

			private JLabel label;
			
			{
				this.label = new JLabel();
				this.setLayout(new BorderLayout());
				this.add(label, BorderLayout.CENTER);
				this.label.setBorder(Spacing.bLarge());
				this.label.setIconTextGap(Spacing.large);
			}
			
			@Override
			protected void onSetValue(Gradient g, boolean selected) {
				this.label.setText(g.getName());
				this.label.setIcon(MapMenuView.gradientToIcon(g));
			}
		};
		
		var paletteCombo = new JComboBox<Gradient>(Mapping.MAP_PALETTES.toArray(new Gradient[] {}));
		paletteCombo.setRenderer(new StencilListCellRenderer<>(stencil));
		paletteCombo.setSelectedItem(Settings.getDefaultMapPalette());
		paletteCombo.addActionListener(e -> {
			Gradient sel = (Gradient) paletteCombo.getSelectedItem();
			Settings.setDefaultMapPalette(sel);
		});
		var palettes = new OptionCustomComponent(mapsBlock, paletteCombo, false);
		palettes.withTitle("Map Palette").withSize(OptionSize.LARGE);
		mapsBlock.add(palettes);
		
		
		OptionBlock uxBlock = new OptionBlock();
		
		
		var accentColour = Accent.forName(DesktopSettings.getAccentColour());
		var theme = Stratus.getTheme();
		var accents = theme.getAccents();
		OptionColours accent = new OptionColours(uxBlock, new ArrayList<>(accents.values()), theme.getAccent(accentColour))
				.withListener(c -> DesktopSettings.setAccentColour(theme.getColourAccentName(c)))
				.withText("Accent Colour", "Requires restart")
				.withSize(OptionSize.LARGE);
		uxBlock.add(accent);
		
		OptionCheckBox darkmode = new OptionCheckBox(uxBlock)
				.withText("Dark Mode (Experimental)", "Use a dark user interface theme, requires restart")
				.withSize(OptionSize.LARGE)
				.withSelection(DesktopSettings.isDarkMode())
				.withListener(DesktopSettings::setDarkMode);
		uxBlock.add(darkmode);
		
				
		
		OptionBlock startup = new OptionBlock();
		OptionCheckBox firstrun = new OptionCheckBox(startup)
				.withText("Show First Run Introduction", "Toggles the first-run introduction screen")
				.withSize(OptionSize.LARGE)
				.withSelection(DesktopSettings.isFirstrun())
				.withListener(DesktopSettings::setFirstrun);
		startup.add(firstrun);

		return new OptionBlocksPanel(mapsBlock, uxBlock, startup);
				
	}

	
	private OptionBlocksPanel makeErrorsPanel(PlotController controller) {
		
		OptionBlock reporting = new OptionBlock();
		
		OptionCheckBox verbose = new OptionCheckBox(reporting)
				.withText("Verbose Logging", "Include more details in Peakaboo's logs")
				.withSize(OptionSize.LARGE)
				.withSelection(Settings.isVerboseLogging())
				.withListener(Settings::setVerboseLogging);
		reporting.add(verbose);
		
		OptionCheckBox autoreport = new OptionCheckBox(reporting)
				.withText("Automatic Crash Reports", "Automatically send crash reports to Peakaboo's developers")
				.withSize(OptionSize.LARGE)
				.withSelection(DesktopSettings.isCrashAutoreporting())
				.withListener(DesktopSettings::setCrashAutoreporting);
		reporting.add(autoreport);
		
		
		return new OptionBlocksPanel(reporting);
				
	}
	

	private OptionBlocksPanel makeDetectorPanel(PlotController controller) {
		
		OptionBlock detector = new OptionBlock();
		
		OptionCheckBox escapeToggle = new OptionCheckBox(detector)
				.withText("Escape Peaks", "Models energy absorbed by a detector being re-emitted")
				.withSize(OptionSize.LARGE)
				.withSelection(controller.fitting().getShowEscapePeaks())
				.withListener(controller.fitting()::setShowEscapePeaks);

		detector.add(escapeToggle);
		

		
		List<DetectorMaterial> materials = List.of(
				DetectorMaterialType.SILICON.get(), 
				DetectorMaterialType.GERMANIUM.get()
			);
		
		FittingController fits = controller.fitting();
		OptionBlock materialBlock = makeRadioBlock(materials, 
				() -> fits.getDetectorMaterial().get(), 
				m -> fits.setDetectorMaterial(m.type())
			);
		
		
		return new OptionBlocksPanel(detector, materialBlock);
	}
	
	
	private OptionBlocksPanel makeCurvefitPanel(PlotController controller) {
		
		List<CurveFitter> fitters = CurveFitterRegistry.system().getPlugins()
				.stream()
				.map(p -> p.create())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		
		FittingController fits = controller.fitting();
		OptionBlock fitBlock = makeRadioBlockForPlugins(fitters, fits::getCurveFitter, fits::setCurveFitter);

		return new OptionBlocksPanel(fitBlock);
		
	}
	
	private OptionBlocksPanel makePeakModelPanel(PlotController controller) {

		FittingController fits = controller.fitting();
		Supplier<PluginDescriptor<FittingFunction>> getter = fits::getFittingFunction;
		Consumer<PluginDescriptor<FittingFunction>> setter = fits::setFittingFunction;
		List<PluginDescriptor<FittingFunction>> fitters = FittingFunctionRegistry.system().getPlugins();
		
		OptionBlock fitBlock = this.makeRadioBlockForFitFns(fitters, getter, setter);

		return new OptionBlocksPanel(fitBlock);
	}
	
	private <T extends BoltPlugin> OptionBlock makeRadioBlockForPlugins(List<T> instances, Supplier<T> getter, Consumer<T> setter) {
		
		OptionBlock block = new OptionBlock();
		ButtonGroup group = new ButtonGroup();
		
		for (T solver : instances) {
			
			OptionRadioButton radio = new OptionRadioButton(block, group)
					.withText(solver.pluginName(), solver.pluginDescription())
					.withSelection(solver.getClass() == getter.get().getClass())
					.withListener(() -> setter.accept(solver))
					.withSize(OptionSize.LARGE);
			
			block.add(radio);
			
		}
		
		return block;
		
	}
	
	private <T extends FittingFunction> OptionBlock makeRadioBlockForFitFns(List<PluginDescriptor<FittingFunction>> fitters, Supplier<PluginDescriptor<FittingFunction>> getter, Consumer<PluginDescriptor<FittingFunction>> setter) {
		
		OptionBlock block = new OptionBlock();
		ButtonGroup group = new ButtonGroup();
		
		for (var proto : fitters) {
			
			OptionRadioButton radio = new OptionRadioButton(block, group)
					.withText(proto.getName(), proto.getDescription())
					.withSelection(proto.getUUID().equals(getter.get().getUUID()))
					.withListener(() -> setter.accept(proto))
					.withSize(OptionSize.LARGE);
			
			block.add(radio);
			
		}
		
		return block;
		
	}
	
	private <T extends SelfDescribing> OptionBlock makeRadioBlock(List<T> instances, Supplier<T> getter, Consumer<T> setter) {
		
		OptionBlock block = new OptionBlock();
		ButtonGroup group = new ButtonGroup();
		
		for (T solver : instances) {
			
			OptionRadioButton radio = new OptionRadioButton(block, group)
					.withText(solver.name(), solver.description())
					.withSelection(solver.getClass() == getter.get().getClass())
					.withListener(() -> setter.accept(solver))
					.withSize(OptionSize.LARGE);
			
			block.add(radio);
			
		}
		
		return block;
		
	}


	private ClearPanel wrapSettingsInfo(JComponent panel, String info) {
		var label = new JLabel(info, SwingConstants.CENTER);
		label.setForeground(Stratus.getTheme().getPalette().getColour("Dark", "1"));
		label.setFont(label.getFont().deriveFont(11f));
		
		var wrap = new ClearPanel(new BorderLayout());
		wrap.add(panel, BorderLayout.CENTER);
		wrap.add(label, BorderLayout.SOUTH);
		return wrap;
	}

	private OptionBlocksPanel makeOverlapPanel(PlotController controller) {

		List<FittingSolver> solvers = FittingSolverRegistry.system().newInstances();
		
		OptionBlock overlap = makeRadioBlockForPlugins(solvers, 
				() -> controller.fitting().getFittingSolver(),
				controller.fitting()::setFittingSolver
			);
				
		return new OptionBlocksPanel(overlap);
		
	}


	
}
