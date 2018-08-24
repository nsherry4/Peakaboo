package swidget.dialogues;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SplashScreen extends JFrame {

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
