package org.peakaboo.ui.swing.plotting;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;

import org.peakaboo.common.SelfDescribing;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.LeastSquaresCurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.OptimizingCurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.MultisamplingOptimizingFittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.OptimizingFittingSolver;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.ConvolvingVoigtFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.GaussianFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.LorentzFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.options.OptionBlock;
import org.peakaboo.framework.swidget.widgets.options.OptionBox;
import org.peakaboo.framework.swidget.widgets.options.OptionLabel;
import org.peakaboo.framework.swidget.widgets.options.OptionSidebar;

public class AdvancedOptionsPanel extends HeaderLayer {
	
	private List<OptionBlock> blocks = new ArrayList<>();
	
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
		OptionSidebar.Entry detectorEntry = new OptionSidebar.Entry(KEY_DETECTOR, IconFactory.getImageIcon("options-detector", IconSize.BUTTON));
		
		String KEY_PEAKMODEL = "Peak Model";
		JPanel peakPanel = makePeakModelPanel(controller);
		body.add(peakPanel, KEY_PEAKMODEL);
		OptionSidebar.Entry peakEntry = new OptionSidebar.Entry(KEY_PEAKMODEL, IconFactory.getImageIcon("options-peakmodel", IconSize.BUTTON));
		
		String KEY_CURVEFIT = "Curve Fitting";
		JPanel curvefitPanel = makeCurvefitPanel(controller);
		body.add(curvefitPanel, KEY_CURVEFIT);
		OptionSidebar.Entry curvefitEntry = new OptionSidebar.Entry(KEY_CURVEFIT, IconFactory.getImageIcon("options-curvefit", IconSize.BUTTON));
		
		String KEY_OVERLAP = "Overlap Solving";
		JPanel overlapPanel = makeOverlapPanel(controller);
		body.add(overlapPanel, KEY_OVERLAP);
		OptionSidebar.Entry overlapEntry = new OptionSidebar.Entry(KEY_OVERLAP, IconFactory.getImageIcon("options-solver", IconSize.BUTTON));
		
		
		OptionSidebar sidebar = new OptionSidebar(List.of(detectorEntry, peakEntry, curvefitEntry, overlapEntry), e -> {
			cards.show(body, e.getName());
		});
		sidebar.select(detectorEntry);
		
		ClearPanel outer = new ClearPanel(new BorderLayout());
		outer.add(body, BorderLayout.CENTER);
		outer.add(sidebar, BorderLayout.WEST);
		
		setBody(outer);
		
	}

	private JPanel blockToPanel(OptionBlock... blocks) {
		JPanel main = new ClearPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		main.setBorder(Spacing.bHuge());
		
		boolean first = true;
		for (OptionBlock block : blocks) {
			if (!first) {
				main.add(Box.createVerticalStrut(Spacing.huge));
			}
			main.add(block);
			first = false;
		}
		
		main.add(Box.createVerticalGlue());
		
		return main;
	}
	
	private JPanel makeDetectorPanel(PlotController controller) {
		
		OptionBlock detector = new OptionBlock();
		
		JComboBox<?> escapePeakBox = makeCombo(
				b -> b == controller.fitting().getShowEscapePeaks(),
				b -> controller.fitting().setShowEscapePeaks(b),
				b -> b ? "On" : "Off",
				true,
				false
			);
		JCheckBox escapePeakToggle = new JCheckBox();
		escapePeakToggle.setSelected(controller.fitting().getShowEscapePeaks());
		escapePeakToggle.addActionListener(e -> controller.fitting().setShowEscapePeaks(escapePeakToggle.isSelected()));
		
		build(detector, escapePeakToggle, "Escape Peaks", "Models energy absorbed by a detector being re-emitted", true);
		
		
		JComboBox<?> detectorMaterialBox = makeCombo(
				e -> e.type() == controller.fitting().getDetectorMaterial(),
				e -> controller.fitting().setDetectorMaterial(e.type()),
				null,
				DetectorMaterialType.SILICON.get(),
				DetectorMaterialType.GERMANIUM.get()
			);
		build(detector, detectorMaterialBox, "Detector Material", "Changes some properties of the peak model", true);
				
		return blockToPanel(detector);
	}
	
	
	private JPanel makeCurvefitPanel(PlotController controller) {
			
		List<CurveFitter> fitters = List.of(
			new UnderCurveFitter(),
			new OptimizingCurveFitter(),
			new LeastSquaresCurveFitter()
		);
		
		FittingController fits = controller.fitting();
		OptionBlock fitBlock = makeRadioBlock(fitters, fits::getCurveFitter, fits::setCurveFitter);

		
		return blockToPanel(fitBlock);
		
	}
	
	private <T extends SelfDescribing> OptionBlock makeRadioBlock(List<T> instances, Supplier<T> getter, Consumer<T> setter) {
		
		OptionBlock block = new OptionBlock();
		ButtonGroup group = new ButtonGroup();
		
		for (T solver : instances) {
			
			JRadioButton selector = new JRadioButton();
			group.add(selector);
			selector.setSelected(solver.getClass() == getter.get().getClass());
			selector.addChangeListener((ChangeEvent e) -> {
				if (!selector.isSelected()) return;
				setter.accept(solver);
			});
			
						
			OptionBox box = new OptionBox(block);
			box.add(selector);
			box.addSpacer();
			box.add(new OptionLabel(solver.name(), solver.description()));
			box.addExpander();
			block.add(box);
			
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
		return blockToPanel(peakBlock);
		
		
//		OptionBlock peakModel = new OptionBlock();
//		
//		JComboBox<?> peakModelBox = makeCombo(
//				f -> f.getClass() == controller.fitting().getFittingFunction(),
//				f -> controller.fitting().setFittingFunction(f.getClass()),
//				null,
//				new PseudoVoigtFittingFunction(),
//				new ConvolvingVoigtFittingFunction(),
//				new GaussianFittingFunction(),
//				new LorentzFittingFunction()
//			);
//		build(peakModel, peakModelBox, "Peak Model", "Function used to model individual peaks", true);
//		
//				
//		return blockToPanel(peakModel);
		
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
				
		return blockToPanel(overlap);
		
	}
	
	
	

	private class ValueBox<T> {

		public T value;
		public String name;
		
		public ValueBox(T value, Function<T, String> pretty) {
			this.value = value;
			if (pretty != null) {
				this.name = pretty.apply(value);
			} else {
				this.name = value == null ? "" : value.toString();
			}
		}
		
		public String toString() {
			return name;
		}
		
	}
	
	private <T> JComboBox<?> makeCombo(Predicate<T> matchesCurrent, Consumer<T> onSelect, Function<T, String> pretty, T... items) {
		
		List<ValueBox<T>> boxes = Arrays.asList(items).stream().map(t -> new ValueBox<>(t, pretty)).collect(Collectors.toList());
		
		JComboBox<ValueBox<T>> comboBox = new JComboBox<>();
		Consumer<ValueBox<T>> addItem = fitter -> {
			comboBox.addItem(fitter);
			if (matchesCurrent.test(fitter.value)) {
				comboBox.setSelectedItem(fitter);
			}
		};
		for (ValueBox<T> item : boxes) {
			addItem.accept(item);
		}
		comboBox.addActionListener(e -> {
			ValueBox<T> box = (ValueBox<T>) comboBox.getSelectedItem();
			onSelect.accept(box.value);
		});
		
		((JLabel)comboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		return comboBox;
	}
	


	public void build(OptionBlock block, JComponent component, String title, String tooltip, boolean fill) {
		OptionBox box = new OptionBox(block);
		OptionLabel lbl = new OptionLabel(title, tooltip);		
		box.add(lbl);
		box.addExpander();
		box.add(component);
		block.add(box);
		
	}
	
}
