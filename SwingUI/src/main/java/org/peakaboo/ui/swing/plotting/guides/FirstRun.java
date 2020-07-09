package org.peakaboo.ui.swing.plotting.guides;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;

public class FirstRun extends HeaderLayer {

	private List<ImageIcon> slides = new ArrayList<>();
	private JLabel slideView;
	private FluentButton next, back;
	private int index = 0;
	private static final int slideCount = 4;
	
	public FirstRun(LayerPanel owner) {	
		super(owner, true);
		next = new FluentButton("Next").withStateDefault().withAction(this::next);
		back = new FluentButton("Back").withAction(this::back);
		getHeader().setRight(next);
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
		back.setEnabled(index != 0);
		showSlide(index);
	}
	
	private void loadSlides() {
		for (int i = 1; i <= slideCount; i++) {
			URL slideURL = FirstRun.class.getResource("/org/peakaboo/ui/swing/firstrun/slide" + i + ".png");
			ImageIcon image = new ImageIcon(slideURL);
			slides.add(image);
		}
	}
	
	private void showSlide(int index) {
		slideView.setIcon(slides.get(index));
	}
	
}
