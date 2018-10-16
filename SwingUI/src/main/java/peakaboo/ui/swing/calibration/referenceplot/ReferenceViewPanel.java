package peakaboo.ui.swing.calibration.referenceplot;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.mapping.calibration.CalibrationReference;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ToggleImageButton;
import swidget.widgets.layout.ButtonBox;
import swidget.widgets.layout.HeaderBox;

public class ReferenceViewPanel extends JPanel {

	private HeaderBox header;
	private JPanel plots;
	private Runnable onClose;
	
	public ReferenceViewPanel(CalibrationReference reference) {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 350));
		
		//plot views
		plots = new JPanel();
		CardLayout cardlayout = new CardLayout();
		plots.setLayout(cardlayout);
		
		
		JPanel infopanel = new JPanel(new BorderLayout());
		JLabel info = new JLabel("<html>" + reference.pluginDescription() + "</html>");
		info.setVerticalAlignment(SwingConstants.TOP);
		info.setBorder(Spacing.bHuge());
		infopanel.add(info, BorderLayout.CENTER);
		ReferencePlot kplot = new ReferencePlot(reference, TransitionSeriesType.K);
		ReferencePlot lplot = new ReferencePlot(reference, TransitionSeriesType.L);
		ReferencePlot mplot = new ReferencePlot(reference, TransitionSeriesType.M);
		
		plots.add(infopanel, "info");
		plots.add(kplot, TransitionSeriesType.K.toString());
		plots.add(lplot, TransitionSeriesType.L.toString());
		plots.add(mplot, TransitionSeriesType.M.toString());
		cardlayout.show(plots, TransitionSeriesType.K.toString());
		
		this.add(plots, BorderLayout.CENTER);
		
		
		
		//header
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE).withTooltip("Close").withAction(() -> this.onClose.run());
		
		ButtonGroup seriesGroup = new ButtonGroup();

		ToggleImageButton details = new ToggleImageButton("Details");
		details.addActionListener((e) -> {
			cardlayout.show(plots, "info");
		});
		seriesGroup.add(details);
		
		ToggleImageButton kseries = new ToggleImageButton("K Series");
		kseries.addActionListener((e) -> {
			cardlayout.show(plots, TransitionSeriesType.K.toString());
		});
		seriesGroup.add(kseries);
		
		ToggleImageButton lseries = new ToggleImageButton("L Series");
		lseries.addActionListener((e) -> {
			cardlayout.show(plots, TransitionSeriesType.L.toString());
		});
		seriesGroup.add(lseries);
		
		ToggleImageButton mseries = new ToggleImageButton("M Series");
		mseries.addActionListener((e) -> {
			cardlayout.show(plots, TransitionSeriesType.M.toString());
		});
		seriesGroup.add(mseries);
		
		ButtonBox center = new ButtonBox(Spacing.small, false);
		center.addCentre(details);
		center.addCentre(kseries);
		center.addCentre(lseries);
		center.addCentre(mseries);
		center.setBorder(Spacing.bNone());
		center.setOpaque(false);
		
		cardlayout.show(plots, "info");
		details.setSelected(true);
		
		header = new HeaderBox(null, center, close);
		this.add(header, BorderLayout.NORTH);

		
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}
	
	
	
}
