package swidget.dialogues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

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
		
		if (isWindows()) {
			//windows doesn't seem to put drop shadows under undecorated windows?
			l.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0x404040)));
		}
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private static boolean isWindows()
	{

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);

	}

}
