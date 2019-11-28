package org.peakaboo.ui.swing.plotting;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel.LabelPosition;

public class AdvancedOptionsPanel extends HeaderLayer {
	
	public AdvancedOptionsPanel(PlotPanel parent, PlotController controller) {
		super(parent, true);
		getHeader().setCentre("Advanced Options");
		
		SettingsPanel master = new SettingsPanel();
		master.addSetting(peakFitting(controller));
		master.setBorder(Spacing.bLarge());
		setBody(master);
		
	}


	private class Box<T> {

		public T value;
		public String name;
		
		public Box(T value, Function<T, String> pretty) {
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




		
		List<Box<T>> boxes = Arrays.asList(items).stream().map(t -> new Box<>(t, pretty)).collect(Collectors.toList());
		
		JComboBox<Box<T>> comboBox = new JComboBox<>();
		Consumer<Box<T>> addItem = fitter -> {
			comboBox.addItem(fitter);
			if (matchesCurrent.test(fitter.value)) {
				comboBox.setSelectedItem(fitter);
			}
		};
		for (Box<T> item : boxes) {
			addItem.accept(item);
		}
		comboBox.addActionListener(e -> {
			Box<T> box = (Box<T>) comboBox.getSelectedItem();
			onSelect.accept(box.value);
		});
		
		return comboBox;
	}
	


	
	private JComponent peakFitting(PlotController controller) {
		

		SettingsPanel panel = new SettingsPanel(new Insets(Spacing.tiny, Spacing.medium, Spacing.tiny, Spacing.medium));
		panel.setOpaque(false);
		panel.setBorder(Spacing.bMedium());
		
		JSpinner fwhmBase = new JSpinner();
		fwhmBase.setModel(new SpinnerNumberModel(controller.fitting().getFWHMBase()*1000, 0.0, 1000.0, 0.1));
		fwhmBase.getEditor().setPreferredSize(new Dimension(72, (int)fwhmBase.getPreferredSize().getHeight()));
		fwhmBase.getEditor().setOpaque(false);
		fwhmBase.addChangeListener(e -> {
			
			float base = ((Number) fwhmBase.getValue()).floatValue()/1000;
			controller.fitting().setFWHMBase(base);
			
		});
		
		build(panel, fwhmBase, "FWHM Noise (eV)", "FWHM of Gaussian detector-based component of a peak.", false);
		
	

		
		JComboBox<?> detectorMaterialBox = makeCombo(
				e -> e.type() == controller.fitting().getDetectorMaterial(),
				e -> controller.fitting().setDetectorMaterial(e.type()),
				null,
				DetectorMaterialType.SILICON.get(),
				DetectorMaterialType.GERMANIUM.get()
			);
		build(panel, detectorMaterialBox, "Detector Material", "Detector material is used to determine some of the properties of the peak model used to fit signal.", true);
		
		
		JComboBox<?> escapePeakBox = makeCombo(
				b -> b == controller.fitting().getShowEscapePeaks(),
				b -> controller.fitting().setShowEscapePeaks(b),
				b -> b ? "On" : "Off",
				true,
				false
			);
		build(panel, escapePeakBox, "Escape Peaks", "Escape peaks result when some of the energy absorbed by a detector is re-emitted.", true);
		
		
		JComboBox<?> peakModelBox = makeCombo(
				f -> f.getClass() == controller.fitting().getFittingFunction(),
				f -> controller.fitting().setFittingFunction(f.getClass()),
				null,
				new PseudoVoigtFittingFunction(),
				new ConvolvingVoigtFittingFunction(),
				new GaussianFittingFunction(),
				new LorentzFittingFunction()
			);
		build(panel, peakModelBox, "Peak Model", "The mathematical function used to model an individual peak.", true);
		
		
		
		
		JComboBox<?> fittersBox = makeCombo(
				f -> f.getClass() == controller.fitting().getCurveFitter().getClass(),
				f -> controller.fitting().setCurveFitter(f),
				null,
				new UnderCurveFitter(),
				new OptimizingCurveFitter(),
				new LeastSquaresCurveFitter()
				
			);
		build(panel, fittersBox, "Single-Curve Fitting", "The strategy used to fit a single element's emission curve to data.", true);

		
		
		
		
		JComboBox<?> solversBox = makeCombo(
				f -> f.getClass() == controller.fitting().getFittingSolver().getClass(), 
				f -> controller.fitting().setFittingSolver(f), 
				null,
				new GreedyFittingSolver(),
				new OptimizingFittingSolver(),
				new MultisamplingOptimizingFittingSolver()
			);
		build(panel, solversBox, "Multi-Curve Solver", "The strategy used to determine how overlapping element emission curves coexist.", true);
		
		
		return panel;
		
	}

	public void build(SettingsPanel panel, JComponent component, String title, String tooltip, boolean fill) {
		JLabel label = new JLabel(title);
		
		component.setToolTipText(tooltip);
		label.setToolTipText(tooltip);
		
		panel.addSetting(component, label, LabelPosition.BESIDE, false, fill);
	}
	
}
