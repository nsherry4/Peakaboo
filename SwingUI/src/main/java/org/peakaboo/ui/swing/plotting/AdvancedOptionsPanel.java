package org.peakaboo.ui.swing.plotting;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.peakaboo.common.SelfDescribing;
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
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.options.OptionBlock;
import org.peakaboo.framework.swidget.widgets.options.OptionBlocksPanel;
import org.peakaboo.framework.swidget.widgets.options.OptionCheckBox;
import org.peakaboo.framework.swidget.widgets.options.OptionRadioButton;
import org.peakaboo.framework.swidget.widgets.options.OptionSidebar;
import org.peakaboo.framework.swidget.widgets.options.OptionSidebar.Entry;
import org.peakaboo.tier.Tier;
import org.peakaboo.framework.swidget.widgets.options.OptionSize;
import org.peakaboo.ui.swing.environment.PeakabooIcons;

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
		
		
		String KEY_DETECTOR = "Detector";
		JPanel detectorPanel = makeDetectorPanel(controller);
		body.add(detectorPanel, KEY_DETECTOR);
		OptionSidebar.Entry detectorEntry = new OptionSidebar.Entry(KEY_DETECTOR, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_DETECTOR, IconSize.TOOLBAR_SMALL));
		
		String KEY_PEAKMODEL = "Peak Model";
		JPanel peakPanel = makePeakModelPanel(controller);
		body.add(peakPanel, KEY_PEAKMODEL);
		OptionSidebar.Entry peakEntry = new OptionSidebar.Entry(KEY_PEAKMODEL, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_PEAKMODEL, IconSize.TOOLBAR_SMALL));
		
		String KEY_CURVEFIT = "Curve Fitting";
		JPanel curvefitPanel = makeCurvefitPanel(controller);
		body.add(curvefitPanel, KEY_CURVEFIT);
		OptionSidebar.Entry curvefitEntry = new OptionSidebar.Entry(KEY_CURVEFIT, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_CURVEFIT, IconSize.TOOLBAR_SMALL));
		
		String KEY_OVERLAP = "Overlap Solving";
		JPanel overlapPanel = makeOverlapPanel(controller);
		body.add(overlapPanel, KEY_OVERLAP);
		OptionSidebar.Entry overlapEntry = new OptionSidebar.Entry(KEY_OVERLAP, IconFactory.getImageIcon(PeakabooIcons.OPTIONS_SOLVER, IconSize.TOOLBAR_SMALL));
		
		
		var entries = new ArrayList<Entry>();
		entries.addAll(List.of(detectorEntry, peakEntry, curvefitEntry, overlapEntry));
		
		
		OptionSidebar sidebar = new OptionSidebar(entries, e -> {
			cards.show(body, e.getName());
		});
		sidebar.select(detectorEntry);
		
		ClearPanel outer = new ClearPanel(new BorderLayout());
		outer.add(body, BorderLayout.CENTER);
		outer.add(sidebar, BorderLayout.WEST);
		
		setBody(outer);
		
	}

	
	private JPanel makeDetectorPanel(PlotController controller) {
		
		OptionBlock detector = new OptionBlock();
		
		OptionCheckBox escapeToggle = new OptionCheckBox(detector)
				.withText("Escape Peaks", "Models energy absorbed by a detector being re-emitted")
				.withSize(OptionSize.LARGE)
				.withSelection(controller.fitting().getShowEscapePeaks())
				.withListener(controller.fitting()::setShowEscapePeaks);
				
		escapeToggle.setTextSize(OptionSize.LARGE);
		
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

		
		
		List<FittingFunction> fitters = List.of(
				new PseudoVoigtFittingFunction(),
				new ConvolvingVoigtFittingFunction(),
				new GaussianFittingFunction(),
				new LorentzFittingFunction()
			);
		
		
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
