package swidget.dialogues;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import swidget.widgets.LiveFrame;

public class SplashScreen extends LiveFrame {

	public SplashScreen(ImageIcon background, Image icon, String appName) {
		super(appName);
		
		setUndecorated(true);
		setIconImage(icon);
		setAlwaysOnTop(true);
		
		JLabel l = new JLabel("", background, SwingConstants.CENTER);
		getContentPane().add(l, BorderLayout.CENTER);
		l.setVisible(true);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
