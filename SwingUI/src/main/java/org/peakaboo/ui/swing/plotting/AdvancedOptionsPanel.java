package org.peakaboo.ui.swing.plotting;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.curvefit.curve.fitting.fitter.LeastSquaresCurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.OptimizingCurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.MultisamplingOptimizingFittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.OptimizingFittingSolver;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.functions.ConvolvingVoigtFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.GaussianFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.LorentzFittingFunction;
import org.peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel.LabelPosition;
import org.peakaboo.framework.swidget.widgets.settings.OptionBlock;
import org.peakaboo.framework.swidget.widgets.settings.OptionBox;
import org.peakaboo.framework.swidget.widgets.settings.OptionLabel;

public class AdvancedOptionsPanel extends HeaderLayer {
	
	private List<OptionBlock> blocks = new ArrayList<>();
	
	public AdvancedOptionsPanel(PlotPanel parent, PlotController controller) {
		super(parent, true);
		getHeader().setCentre("Advanced Options");
		
		makeUI(controller);
		
		ClearPanel main = new ClearPanel();
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
		setBody(main);
		
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
	


	
	private void makeUI(PlotController controller) {
		
		
		OptionBlock detector = new OptionBlock();
		blocks.add(detector);
		
		
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
		

		
		
		OptionBlock fitting = new OptionBlock();
		blocks.add(fitting);
		
		JComboBox<?> peakModelBox = makeCombo(
				f -> f.getClass() == controller.fitting().getFittingFunction(),
				f -> controller.fitting().setFittingFunction(f.getClass()),
				null,
				new PseudoVoigtFittingFunction(),
				new ConvolvingVoigtFittingFunction(),
				new GaussianFittingFunction(),
				new LorentzFittingFunction()
			);
		build(fitting, peakModelBox, "Peak Model", "Function used to model individual peaks", true);
		
		
		
		
		JComboBox<?> fittersBox = makeCombo(
				f -> f.getClass() == controller.fitting().getCurveFitter().getClass(),
				f -> controller.fitting().setCurveFitter(f),
				null,
				new UnderCurveFitter(),
				new OptimizingCurveFitter(),
				new LeastSquaresCurveFitter()
				
			);
		build(fitting, fittersBox, "Single-Curve Fitting", "Technique used to fit a single curve to signal", true);

		
		
		
		
		JComboBox<?> solversBox = makeCombo(
				f -> f.getClass() == controller.fitting().getFittingSolver().getClass(), 
				f -> controller.fitting().setFittingSolver(f), 
				null,
				new GreedyFittingSolver(),
				new OptimizingFittingSolver(),
				new MultisamplingOptimizingFittingSolver()
			);
		build(fitting, solversBox, "Multi-Curve Solver", "Technique to fit overlapping curves to signal", true);
		
		
		
	}

//	public void build(SettingsPanel panel, JComponent component, String title, String tooltip, boolean fill) {
//		JLabel label = new JLabel(title);
//		
//		component.setToolTipText(tooltip);
//		label.setToolTipText(tooltip);
//		
//		panel.addSetting(component, label, LabelPosition.BESIDE, false, fill);
//	}

	public void build(OptionBlock block, JComponent component, String title, String tooltip, boolean fill) {
		OptionBox box = new OptionBox(block);
		OptionLabel lbl = new OptionLabel(title, tooltip);		
		box.add(lbl);
		box.addSpacer();
		box.add(component);
		block.add(box);
		
	}
	
}
