package peakaboo.ui.swing.calibration.referenceplot;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import stratus.controls.ToggleButtonLinker;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ToggleImageButton;
import swidget.widgets.layout.ButtonBox;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.layout.HeaderTabBuilder;

public class ReferenceViewPanel extends JPanel {

	private HeaderBox header;
	private Runnable onClose;
	
	public ReferenceViewPanel(CalibrationReference reference) {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 350));

		
		JPanel infopanel = new JPanel(new BorderLayout());
		JLabel info = new JLabel("<html>" + reference.pluginDescription() + "</html>");
		info.setVerticalAlignment(SwingConstants.TOP);
		info.setBorder(Spacing.bHuge());
		infopanel.add(info, BorderLayout.CENTER);
		ReferencePlot kplot = new ReferencePlot(reference, TransitionSeriesType.K);
		ReferencePlot lplot = new ReferencePlot(reference, TransitionSeriesType.L);
		ReferencePlot mplot = new ReferencePlot(reference, TransitionSeriesType.M);
		
		HeaderTabBuilder tabBuilder = new HeaderTabBuilder();
		tabBuilder.addTab("Details", infopanel);
		tabBuilder.addTab("K Series", kplot);
		tabBuilder.addTab("L Series", lplot);
		tabBuilder.addTab("M Series", mplot);

		
		this.add(tabBuilder.getBody(), BorderLayout.CENTER);
		
		//header
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE).withTooltip("Close").withBordered(false).withAction(() -> this.onClose.run());
		
		header = new HeaderBox(null, tabBuilder.getTabStrip(), close);
		this.add(header, BorderLayout.NORTH);

		
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}
	
	
	
}
