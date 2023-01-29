package org.peakaboo.ui.swing.options;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Settings;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterPluginManager;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.MultisamplingOptimizingFittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.OptimizingFittingSolver;
import org.peakaboo.curvefit.peak.detector.DetectorMaterial;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.ConvolvingVoigtFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.GaussianFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.LorentzFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.SelfDescribing;
import org.peakaboo.framework.autodialog.view.swing.layouts.SwingLayoutFactory;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlocksPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionCheckBox;
import org.peakaboo.framework.stratus.components.ui.options.OptionColours;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;
import org.peakaboo.framework.stratus.components.ui.options.OptionSidebar;
import org.peakaboo.framework.stratus.components.ui.options.OptionSize;
import org.peakaboo.framework.stratus.components.ui.options.OptionSidebar.Entry;
import org.peakaboo.tier.Tier;
import org.peakaboo.tier.TierUIAutoGroup;
import org.peakaboo.ui.swing.app.AccentedTheme;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.app.DesktopSettings;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class AdvancedOptionsPanel extends HeaderLayer {
	
	public AdvancedOptionsPanel(PlotPanel parent, PlotController controller) {
		super(parent, true);
		getHeader().setCentre("Advanced Options");
		
		ClearPanel body = new ClearPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500, 300);
			}

		};
		CardLayout cards = new CardLayout();
		body.setLayout(cards);
		body.setBorder(Spacing.bMedium());
		
		var hasdata = controller.data().hasDataSet();
		
		String KEY_DETECTOR = "Detector";
		JPanel detectorPanel = makeDetectorPanel(controller);
		detectorPanel.setEnabled(hasdata);
		body.add(detectorPanel, KEY_DETECTOR);
		OptionSidebar.Entry detectorEntry = new OptionSidebar.Entry(KEY_DETECTOR, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_DETECTOR, IconSize.TOOLBAR_SMALL));
		
		String KEY_PEAKMODEL = "Peak Model";
		JPanel peakPanel = makePeakModelPanel(controller);
		peakPanel.setEnabled(hasdata);
		body.add(peakPanel, KEY_PEAKMODEL);
		OptionSidebar.Entry peakEntry = new OptionSidebar.Entry(KEY_PEAKMODEL, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_PEAKMODEL, IconSize.TOOLBAR_SMALL));
		
		String KEY_CURVEFIT = "Curve Fitting";
		JPanel curvefitPanel = makeCurvefitPanel(controller);
		curvefitPanel.setEnabled(hasdata);
		body.add(curvefitPanel, KEY_CURVEFIT);
		OptionSidebar.Entry curvefitEntry = new OptionSidebar.Entry(KEY_CURVEFIT, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_CURVEFIT, IconSize.TOOLBAR_SMALL));
		
		String KEY_OVERLAP = "Overlap Solving";
		JPanel overlapPanel = makeOverlapPanel(controller);
		overlapPanel.setEnabled(hasdata);
		body.add(overlapPanel, KEY_OVERLAP);
		OptionSidebar.Entry overlapEntry = new OptionSidebar.Entry(KEY_OVERLAP, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_SOLVER, IconSize.TOOLBAR_SMALL));
				
		var entries = new ArrayList<Entry>();
		entries.addAll(List.of(detectorEntry, peakEntry, curvefitEntry, overlapEntry));
		
		for (TierUIAutoGroup<PlotController> item : Tier.provider().getAdvancedOptions()) {
			Group group = item.getValue();
			String groupKey = group.getName();
			JComponent groupPanel = SwingLayoutFactory.forGroup(group).getComponent();
			body.add(groupPanel, groupKey);
			OptionSidebar.Entry itemEntry = new OptionSidebar.Entry(groupKey, IconFactory.getImageIcon(Tier.provider().iconPath(), item.getIconPath(), IconSize.TOOLBAR_SMALL));
			entries.add(itemEntry);
		}

		//separator between dataset-focused options and app options
		entries.get(entries.size()-1).trailingSeparator = true;
		
		
		String KEY_PERFORMANCE = "Performance";
		JPanel perfPanel = makePerformancePanel(controller);
		body.add(perfPanel, KEY_PERFORMANCE);
		OptionSidebar.Entry perfEntry = new OptionSidebar.Entry(KEY_PERFORMANCE, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_PERFORMANCE, IconSize.TOOLBAR_SMALL));
		entries.add(perfEntry);
		
		String KEY_APP = "Appearance";
		JPanel appPanel = makeAppPanel(controller);
		body.add(appPanel, KEY_APP);
		OptionSidebar.Entry appEntry = new OptionSidebar.Entry(KEY_APP, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_APPEARANCE, IconSize.TOOLBAR_SMALL));
		entries.add(appEntry);
		
		String KEY_ERRORS = "Errors";
		JPanel errorsPanel = makeErrorsPanel(controller);
		body.add(errorsPanel, KEY_ERRORS);
		OptionSidebar.Entry errorsEntry = new OptionSidebar.Entry(KEY_ERRORS, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_ERRORS, IconSize.TOOLBAR_SMALL));
		entries.add(errorsEntry);
		
		
		
		//Create the UI components from the model
		OptionSidebar sidebar = new OptionSidebar(entries, e -> {
			cards.show(body, e.getName());
		});
		sidebar.select(detectorEntry);
		
		ClearPanel outer = new ClearPanel(new BorderLayout());
		outer.add(body, BorderLayout.CENTER);
		outer.add(sidebar, BorderLayout.WEST);
		
		setBody(outer);
		
	}

	
	private JPanel makePerformancePanel(PlotController controller) {
		OptionBlock datasets = new OptionBlock();
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

				
		
		
		return new OptionBlocksPanel(datasets, heapBlock);
	}
	
	private JPanel makeAppPanel(PlotController controller) {
	
		OptionBlock uxBlock = new OptionBlock();
		var colours = AccentedTheme.accentColours;
		Color accentColour = colours.get(DesktopSettings.getAccentColour()); 
		if (accentColour == null) {
			accentColour = colours.get("Blue");
		}
		OptionColours accent = new OptionColours(uxBlock, new ArrayList<>(colours.values()), accentColour)
				.withListener(c -> DesktopSettings.setAccentColour(colours.getKey(c)))
				.withText("Accent Colour", "Requires restart")
				.withSize(OptionSize.LARGE);
		uxBlock.add(accent);
				
		
		OptionBlock startup = new OptionBlock();
		OptionCheckBox firstrun = new OptionCheckBox(startup)
				.withText("Show First Run Introduction", "Toggles the first-run introduction screen")
				.withSize(OptionSize.LARGE)
				.withSelection(DesktopSettings.isFirstrun())
				.withListener(DesktopSettings::setFirstrun);
		startup.add(firstrun);

		return new OptionBlocksPanel(uxBlock, startup);
				
	}

	
	private JPanel makeErrorsPanel(PlotController controller) {
		
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
	

	private JPanel makeDetectorPanel(PlotController controller) {
		
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
	
	
	private JPanel makeCurvefitPanel(PlotController controller) {
		
		List<CurveFitter> fitters = CurveFitterPluginManager.system().getPlugins().stream().map(p -> p.create()).collect(Collectors.toList());
		
		FittingController fits = controller.fitting();
		OptionBlock fitBlock = makeRadioBlockForPlugins(fitters, fits::getCurveFitter, fits::setCurveFitter);

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

	
	private JPanel makePeakModelPanel(PlotController controller) {

		
		
		List<FittingFunction> fitters = new ArrayList<>(List.of(
				new PseudoVoigtFittingFunction(),
				new ConvolvingVoigtFittingFunction(),
				new GaussianFittingFunction(),
				new LorentzFittingFunction()
			));
		
		fitters.addAll(Tier.provider().getFittingFunctions());
		
		
		FittingController fitter = controller.fitting();
		//TODO: maybe change this so that the settings just stores the fitting function instance instead of the class?
		OptionBlock peakBlock = makeRadioBlock(fitters, 
				() -> {
					try {
						return fitter.getFittingFunction().getDeclaredConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}, 
				i -> fitter.setFittingFunction(i.getClass())
			);
		return new OptionBlocksPanel(peakBlock);
				
	}
	
	

	private JPanel makeOverlapPanel(PlotController controller) {

		List<FittingSolver> solvers = List.of(
				new GreedyFittingSolver(), 
				new OptimizingFittingSolver(), 
				new MultisamplingOptimizingFittingSolver()
			);
		
		OptionBlock overlap = makeRadioBlock(solvers, 
				() -> controller.fitting().getFittingSolver(),
				controller.fitting()::setFittingSolver
			);
				
		return new OptionBlocksPanel(overlap);
		
	}


	
}
