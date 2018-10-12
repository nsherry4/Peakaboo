package peakaboo.ui.swing.calibration;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.mapping.calibration.CalibrationPluginManager;
import peakaboo.mapping.calibration.CalibrationProfile;
import stratus.StratusLookAndFeel;
import swidget.widgets.ButtonBox;
import swidget.widgets.HeaderBox;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ToggleImageButton;

public class ProfileViewPanel extends JPanel {

	private HeaderBox header;
	private JPanel plots;
	
	public ProfileViewPanel(CalibrationProfile profile, Runnable onAccept, Runnable onReject) {
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 350));
		
		//plot views
		plots = new JPanel();
		CardLayout cardlayout = new CardLayout();
		plots.setLayout(cardlayout);
		
		ProfilePlot kplot = new ProfilePlot(profile, TransitionSeriesType.K);
		ProfilePlot lplot = new ProfilePlot(profile, TransitionSeriesType.L);
		ProfilePlot mplot = new ProfilePlot(profile, TransitionSeriesType.M);
		
		plots.add(kplot, TransitionSeriesType.K.toString());
		plots.add(lplot, TransitionSeriesType.L.toString());
		plots.add(mplot, TransitionSeriesType.M.toString());
		cardlayout.show(plots, TransitionSeriesType.K.toString());
		
		this.add(plots, BorderLayout.CENTER);
		
		
		
		//header
		ImageButton ok = new ImageButton("Accept").withStateDefault().withAction(onAccept);
		ImageButton cancel = new ImageButton("Cancel").withAction(onReject);
		
		ButtonGroup seriesGroup = new ButtonGroup();
		
		ToggleImageButton kseries = new ToggleImageButton("K Series");
		kseries.addActionListener((e) -> {
			System.out.println("K");
			cardlayout.show(plots, TransitionSeriesType.K.toString());
		});
		seriesGroup.add(kseries);
		
		ToggleImageButton lseries = new ToggleImageButton("L Series");
		lseries.addActionListener((e) -> {
			System.out.println("L");
			cardlayout.show(plots, TransitionSeriesType.L.toString());
		});
		seriesGroup.add(lseries);
		
		ToggleImageButton mseries = new ToggleImageButton("M Series");
		mseries.addActionListener((e) -> {
			cardlayout.show(plots, TransitionSeriesType.M.toString());
		});
		seriesGroup.add(mseries);
		
		ButtonBox center = new ButtonBox(Spacing.small, false);
		center.addCentre(kseries);
		center.addCentre(lseries);
		center.addCentre(mseries);
		center.setBorder(Spacing.bNone());
		center.setOpaque(false);
		
		cardlayout.show(plots, TransitionSeriesType.K.toString());
		kseries.setSelected(true);
		
		header = new HeaderBox(cancel, center, ok);
		this.add(header, BorderLayout.NORTH);
		
		

		
		
	}
	
	public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(new StratusLookAndFeel());
		
		CalibrationPluginManager.init(new File("/home/nathaniel/Desktop/PBCP/"));
		CalibrationProfile p = CalibrationProfile.load(new File("/home/nathaniel/Desktop/nist610sigray-14.pbcp").toPath());
		
		ProfileViewPanel view = new ProfileViewPanel(p, () -> {}, () -> {});
		
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(view, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
		
	}
	
	
}
