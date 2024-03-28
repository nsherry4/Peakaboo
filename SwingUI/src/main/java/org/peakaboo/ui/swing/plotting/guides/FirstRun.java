package org.peakaboo.ui.swing.plotting.guides;

import java.awt.FlowLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.ui.swing.app.DesktopSettings;

public class FirstRun extends HeaderLayer {

	private List<ImageIcon> slides = new ArrayList<>();
	private JLabel slideView;
	private FluentButton next, back;
	private JCheckBox reporting;
	private int index = 0;
	private static final int SLIDE_COUNT = 5;
	
	public FirstRun(LayerPanel owner) {	
		super(owner, true);
		next = new FluentButton("Next").withStateDefault().withAction(this::next);
		back = new FluentButton("Back").withAction(this::back);
		
		var nextbox = new ClearPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
		nextbox.setBorder(Spacing.bNone());
		reporting = new JCheckBox();
		reporting.addItemListener(e -> {
			DesktopSettings.setCrashAutoreporting(reporting.isSelected());
		});
		reporting.setBorder(new EmptyBorder(0, 0, 0, Spacing.large));
		reporting.setVisible(false);
		nextbox.add(reporting);
		nextbox.add(next);
		
		
		getHeader().setRight(nextbox);
		getHeader().setLeft(back);
		getHeader().setCentre("Welcome to Peakaboo!");
		
		loadSlides();
		
		slideView = new JLabel("", slides.get(0), SwingConstants.CENTER);
		slideView.setVisible(true);
		setBody(slideView);	
		
		index = -1;
		next();
		
	}
	
	private void next() {
		index++;
		if (index >= slides.size()) {
			this.remove();
			return;
		}
		updateWidgets();
	}
	
	private void back() {
		index--;
		updateWidgets();
	}
	
	private void updateWidgets() {
		if (index == slides.size() - 1) {
			next.setText("Done");
		} else {
			next.setText("Next");
		}
		reporting.setVisible(index == 4);
		
		back.setEnabled(index != 0);
		showSlide(index);
	}
	
	private void loadSlides() {
		for (int i = 1; i <= SLIDE_COUNT; i++) {
			URL slideURL = FirstRun.class.getResource("/org/peakaboo/ui/swing/firstrun/slide" + i + ".png");
			ImageIcon image = new ImageIcon(slideURL);
			slides.add(image);
		}
	}
	
	private void showSlide(int index) {
		slideView.setIcon(slides.get(index));
	}
	
}
