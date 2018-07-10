package peakaboo.ui.swing.plotting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.LeastSquaresCurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.OptimizingCurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import peakaboo.curvefit.curve.fitting.solver.OptimizingFittingSolver;
import peakaboo.curvefit.peak.escape.EscapePeak;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.fitting.FittingFunction;
import peakaboo.curvefit.peak.fitting.functions.ConvolvingVoigtFittingFunction;
import peakaboo.curvefit.peak.fitting.functions.GaussianFittingFunction;
import peakaboo.curvefit.peak.fitting.functions.LorentzFittingFunction;
import peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import swidget.icons.StockIcon;
import swidget.widgets.HeaderBox;
import swidget.widgets.ImageButton;
import swidget.widgets.SettingsPanel;
import swidget.widgets.SettingsPanel.LabelPosition;
import swidget.widgets.Spacing;

public class AdvancedOptionsPanel extends JPanel {
	
	public AdvancedOptionsPanel(PlotPanel parent, PlotController controller) {

		SettingsPanel master = new SettingsPanel();
		master.addSetting(peakFitting(controller));
		master.setBorder(Spacing.bLarge());
		
		this.setLayout(new BorderLayout());
		this.add(master, BorderLayout.CENTER);
		
		
		
		
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", true);
		close.addActionListener(e -> {
			parent.popModalComponent();
		});

		HeaderBox box = new HeaderBox(null, "Advanced Options", close);
		
		this.add(box, BorderLayout.NORTH);
		
	}

	
	private <T> JComboBox<T> makeCombo(Predicate<T> matchesCurrent, Consumer<T> onSelect, T... items) {
		
		JComboBox<T> comboBox = new JComboBox<>();
		Consumer<T> addItem = fitter -> {
			comboBox.addItem(fitter);
			if (matchesCurrent.test(fitter)) {
				comboBox.setSelectedItem(fitter);
			}
		};
		for (T item : items) {
			addItem.accept(item);
		}
		comboBox.addActionListener(e -> {
			onSelect.accept((T) comboBox.getSelectedItem());
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
		
	

		
		JComboBox<EscapePeak> escapePeakBox = makeCombo(
				e -> e.type() == controller.fitting().getEscapeType(),
				e -> controller.fitting().setEscapeType(e.type()),
				EscapePeakType.NONE.get(),
				EscapePeakType.SILICON.get(),
				EscapePeakType.GERMANIUM.get()
			);
		build(panel, escapePeakBox, "Escape Peaks", "Escape peaks result when some of the energy absorbed by a detector is re-emitted.", true);
		
		
		JComboBox<FittingFunction> peakModelBox = makeCombo(
				f -> f.getClass() == controller.fitting().getFittingFunction(),
				f -> controller.fitting().setFittingFunction(f.getClass()),
				new PseudoVoigtFittingFunction(),
				new ConvolvingVoigtFittingFunction(),
				new GaussianFittingFunction(),
				new LorentzFittingFunction()
			);
		build(panel, peakModelBox, "Peak Model", "The mathematical function used to model an individual peak.", true);
		
		
		
		
		JComboBox<CurveFitter> fittersBox = makeCombo(
				f -> f.getClass() == controller.fitting().getCurveFitter().getClass(),
				f -> controller.fitting().setCurveFitter(f),
				new UnderCurveFitter(),
				new OptimizingCurveFitter(),
				new LeastSquaresCurveFitter()
				
			);
		build(panel, fittersBox, "Single-Curve Fitting", "The strategy used to fit a single element's emission curve to data.", true);

		
		
		
		
		JComboBox<FittingSolver> solversBox = makeCombo(
				f -> f.getClass() == controller.fitting().getFittingSolver().getClass(), 
				f -> controller.fitting().setFittingSolver(f), 
				new GreedyFittingSolver(),
				new OptimizingFittingSolver()
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

	
	private JComponent titled(JComponent component, String title) {
		JPanel titled = new JPanel(new BorderLayout());
		titled.add(component, BorderLayout.WEST);
		titled.setBorder(new CompoundBorder(Spacing.bSmall(), new TitledBorder(title)));
		return titled;
	}
	
}
