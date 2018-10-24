package peakaboo.ui.swing.calibration.profileplot;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import eventful.EventfulConfig;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.classinfo.StringClassInfo;
import net.sciencestudio.autodialog.model.style.Style;
import net.sciencestudio.autodialog.model.style.editors.TextBoxStyle;
import net.sciencestudio.autodialog.view.swing.SwingAutoPanel;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.mapping.calibration.CalibrationPluginManager;
import peakaboo.mapping.calibration.CalibrationProfile;
import stratus.StratusLookAndFeel;
import stratus.controls.ToggleButtonLinker;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ToggleImageButton;
import swidget.widgets.layout.ButtonBox;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.layout.HeaderTabBuilder;

public class ProfileViewPanel extends JPanel {

	private HeaderBox header;
	private JPanel body;
	protected List<ProfilePlot> profileplots = new ArrayList<>();

	protected ProfileViewPanel() {
		
	}
	
	public ProfileViewPanel(CalibrationProfile profile, Runnable onClose) {
		
		ImageButton cancel = new ImageButton(StockIcon.WINDOW_CLOSE).withTooltip("Close").withBordered(false).withAction(onClose);
		
		
		init(profile, null, null, cancel);
		
	}
	
	//view-only mode
	public ProfileViewPanel(CalibrationProfile profile, Runnable onAccept, Runnable onReject) {
		
		ImageButton ok = new ImageButton("Accept").withStateDefault().withAction(onAccept);
		ImageButton cancel = new ImageButton("Cancel").withAction(onReject);
		
		init(profile, null, cancel, ok);

	}
	
	protected void init(CalibrationProfile profile, File source, JComponent left, JComponent right) {
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 350));
		
		body = new JPanel(new BorderLayout());
		this.add(body, BorderLayout.CENTER);
		
		//plot views
		HeaderTabBuilder tabBuilder = new HeaderTabBuilder();
		
		ProfilePlot kplot = new ProfilePlot(profile, source, TransitionSeriesType.K);
		ProfilePlot lplot = new ProfilePlot(profile, source, TransitionSeriesType.L);
		ProfilePlot mplot = new ProfilePlot(profile, source, TransitionSeriesType.M);
		profileplots.add(kplot);
		profileplots.add(lplot);
		profileplots.add(mplot);

		
		
		tabBuilder.addTab("K Series", kplot);
		tabBuilder.addTab("L Series", lplot);
		tabBuilder.addTab("M Series", mplot);
		
		body.add(tabBuilder.getBody(), BorderLayout.CENTER);
				
		header = new HeaderBox(left, tabBuilder.getTabStrip(), right);
		this.add(header, BorderLayout.NORTH);
	}
	
	public void addNorth(JComponent component) {
		body.add(component, BorderLayout.NORTH);
	}
	
	public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException {
		EventfulConfig.uiThreadRunner = SwingUtilities::invokeLater;
		
		UIManager.setLookAndFeel(new StratusLookAndFeel());
		
		CalibrationPluginManager.init(new File("/home/nathaniel/Desktop/PBCP/"));
		CalibrationProfile p = CalibrationProfile.load(new File("/home/nathaniel/Desktop/nist610sigray-14.pbcp").toPath());
		p.setName("Test Name");
		
		System.out.println(p.getName());
		Parameter<String> name = new Parameter<>("Name", new TextBoxStyle().setHorizontalExpand(true), p.getName(), v -> v.getValue().length() > 0);
		SwingAutoPanel namePanel = new SwingAutoPanel(name);
		namePanel.setBorder(Spacing.bLarge());
		
		ProfileViewPanel view = new ProfileViewPanel(p, () -> {}, () -> {});
		view.addNorth(namePanel);
		
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(view, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
		
	}
	
	
}
