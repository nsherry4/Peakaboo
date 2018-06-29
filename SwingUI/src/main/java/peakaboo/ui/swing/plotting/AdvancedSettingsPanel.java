package peakaboo.ui.swing.plotting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.settings.SettingsSerializer;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.LeastSquaresCurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import peakaboo.curvefit.peak.fitting.functions.ConvolvingVoigtFittingFunction;
import peakaboo.curvefit.peak.fitting.functions.GaussianFittingFunction;
import peakaboo.curvefit.peak.fitting.functions.IdaFittingFunction;
import peakaboo.curvefit.peak.fitting.functions.LorentzFittingFunction;
import peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ImageButton;
import swidget.widgets.SettingsPanel;
import swidget.widgets.Spacing;

public class AdvancedSettingsPanel extends JPanel {
	
	public AdvancedSettingsPanel(PlotPanel parent, PlotController controller) {

		SettingsPanel master = new SettingsPanel();
		master.addSetting(peakFitting(controller));
		master.addSetting(curveFitter(controller));
		master.setBorder(Spacing.bLarge());
		
		this.setLayout(new BorderLayout());
		this.add(master, BorderLayout.CENTER);
		
		
		
		ButtonBox box = new ButtonBox(true);
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", true);
		close.addActionListener(e -> {
			parent.popModalComponent();
		});
		box.addRight(close);
		
		this.add(box, BorderLayout.SOUTH);
		
	}

	
	private JComponent curveFitter(PlotController controller) {
		
		SettingsPanel fitters = new SettingsPanel();
		ButtonGroup fittersGroup = new ButtonGroup();
		
		Consumer<CurveFitter> addFitter = fitter -> {
			JRadioButton option = new JRadioButton(fitter.name());
			option.setSelected(controller.fitting().getCurveFitter().getClass() == fitter.getClass());
			option.addActionListener(e -> {
				controller.fitting().setCurveFitter(fitter);
			});
			fittersGroup.add(option);
			
			fitters.addSetting(option);
		};
		
		addFitter.accept(new UnderCurveFitter());
		addFitter.accept(new LeastSquaresCurveFitter());
		
		return titled(fitters, "Single-Curve Fitting");
		
	}
	
	private JComponent peakFitting(PlotController controller) {
		

		SettingsPanel peakwidth = new SettingsPanel();
		peakwidth.setOpaque(false);
		peakwidth.setBorder(Spacing.bMedium());

		JSpinner fwhmBase = new JSpinner();
		fwhmBase.setModel(new SpinnerNumberModel(controller.settings().getFWHMBase()*1000, 0.0, 1000.0, 0.1));
		fwhmBase.getEditor().setPreferredSize(new Dimension(72, (int)fwhmBase.getPreferredSize().getHeight()));
		fwhmBase.getEditor().setOpaque(false);
		fwhmBase.addChangeListener(e -> {
			
			float base = ((Number) fwhmBase.getValue()).floatValue()/1000;
			controller.settings().setFWHMBase(base);
			
		});
		peakwidth.addSetting(fwhmBase, "FWHM Noise (eV)");
	
		
		
		ButtonGroup functionGroup = new ButtonGroup();
		
		
		JRadioButton pseudovoigt = new JRadioButton("Pseudo-Voigt");
		pseudovoigt.setSelected(controller.settings().getFittingFunction() == PseudoVoigtFittingFunction.class);
		pseudovoigt.addActionListener(e -> {
			controller.settings().setFittingFunction(PseudoVoigtFittingFunction.class);
		});
		functionGroup.add(pseudovoigt);
		peakwidth.addSetting(pseudovoigt);
		
		
		JRadioButton voigt = new JRadioButton("Test Voigt");
		voigt.setSelected(controller.settings().getFittingFunction() == ConvolvingVoigtFittingFunction.class);
		voigt.addActionListener(e -> {
			controller.settings().setFittingFunction(ConvolvingVoigtFittingFunction.class);
		});
		functionGroup.add(voigt);
		peakwidth.addSetting(voigt);
		
		
		JRadioButton gaussian = new JRadioButton("Gaussian");
		gaussian.setSelected(controller.settings().getFittingFunction() == GaussianFittingFunction.class);
		gaussian.addActionListener(e -> {
			controller.settings().setFittingFunction(GaussianFittingFunction.class);
		});
		functionGroup.add(gaussian);
		peakwidth.addSetting(gaussian);
		
		
		JRadioButton ida = new JRadioButton("Ida");
		ida.setSelected(controller.settings().getFittingFunction() == IdaFittingFunction.class);
		ida.addActionListener(e -> {
			controller.settings().setFittingFunction(IdaFittingFunction.class);
		});
		functionGroup.add(ida);
		peakwidth.addSetting(ida);
		
		
		JRadioButton lorentz = new JRadioButton("Lorentz");
		lorentz.setSelected(controller.settings().getFittingFunction() == LorentzFittingFunction.class);
		lorentz.addActionListener(e -> {
			controller.settings().setFittingFunction(LorentzFittingFunction.class);
		});
		functionGroup.add(lorentz);
		peakwidth.addSetting(lorentz);
		
		
		return titled(peakwidth, "Peak Fitting");
		
	}

	private JComponent titled(JComponent component, String title) {
		JPanel titled = new JPanel(new BorderLayout());
		titled.add(component, BorderLayout.WEST);
		titled.setBorder(new CompoundBorder(Spacing.bSmall(), new TitledBorder(title)));
		return titled;
	}
	
}
