package peakaboo.ui.swing.dialogues;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import peakaboo.common.Version;
import swidget.containers.SwidgetContainer;
import swidget.containers.SwidgetDialog;
import swidget.dialogues.fileio.IOCommon;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;


public class AboutDialogue extends SwidgetDialog
{
	
	SwidgetContainer owner;
	
	
	public AboutDialogue(SwidgetContainer owner)
	{
		super(owner, "About " + "Peakaboo", true);
		this.owner = owner;
		
		setResizable(false);
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		
		
		JPanel panel = new JPanel(new GridBagLayout());
		c.add(panel, BorderLayout.CENTER);
		
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.VERTICAL;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		
		panel.add(new JLabel(IconFactory.getImageIcon( Version.logo )), gc);

		
		gc.gridy += 1;
		gc.weighty = 0.0;
		gc.weightx = 1.0;
		
		JLabel text = new JLabel();
		text.setFont(text.getFont().deriveFont(Font.PLAIN));
		text.setText(
			"<html><center>" +
				"<b><big><big>Peakaboo " + Version.versionNo + "</big></big></b>" +
				((Version.release) ? "" : "<br><b><font size=\"+1\" color=\"#c00000\">Development Release</font></b>") +  
				"<br>" +
				"<br>" +
				"XRF Visualisation Program" +
				"<br>" +
				"<font size=\"-2\">" +
					"<font color=\"#777777\">Version " + Version.longVersionNo +
					"<br/>" + 
					"Build Date: " + Version.buildDate + 
					"</font>" +
					"<br>" +
					"<br>" +
					"Copyright &copy; 2009-2010 by<br>The University of Western Ontario and" +
					"<br>" +
					"The Canadian Light Source Inc." +
				"</font>" +
				"<br>" +
				"<br>" +
				"http://www.sciencestudioproject.com" +
				"<br>" +
				"<br>" +
			"</center></html>");
		
		panel.add(text, gc);
		
		

		JPanel buttonPanel = new JPanel();
		
		
		ImageButton button = new ImageButton("about", "Credits", true);
		button.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent arg0)
			{
				String credits = readTextFromJar("/peakaboo/credits.txt");
				JOptionPane.showMessageDialog(AboutDialogue.this, credits, "Credits", JOptionPane.INFORMATION_MESSAGE, IconFactory.getImageIcon("about", IconSize.ICON));
			}
		});
		buttonPanel.add(button);
		
		
		button = new ImageButton("textfile", "Licence", true);
		button.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent arg0)
			{
				String licence = readTextFromJar("/peakaboo/licence.txt");
				JOptionPane.showMessageDialog(AboutDialogue.this, textForJOptionPane(licence), "Peakaboo Licence", JOptionPane.INFORMATION_MESSAGE, IconFactory.getImageIcon("about-large"));
			}
		});
		buttonPanel.add(button);	


		button = new ImageButton("close", "Close", true);
		button.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent arg0)
			{
				setVisible(false);
			}
		});
		buttonPanel.add(button);
		
		
		
		gc.gridy += 1;
		panel.add(buttonPanel, gc);
		
		panel.setBorder(new EmptyBorder(0, 50, Spacing.large, 50));
		//setPreferredSize(new Dimension(300, 300));
		
		
		pack();

		centreOnParent();
		setVisible(true);
		
	}
	
	
    public JScrollPane textForJOptionPane(String text) {

		JTextArea ta = new JTextArea();
		ta.setText(text);
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		
		ta.setOpaque(false);
		
		
		
		JScrollPane s = new JScrollPane(ta);
		s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		
		s.setOpaque(false);
		
		s.setPreferredSize(new Dimension(500, 300));
		s.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		return s;
    	
    }
    
    public String readTextFromJar(String s)
	{
		InputStream is = null;
		BufferedReader br = null;
		String text = "";

		is = getClass().getResourceAsStream(s);
		br = new BufferedReader(new InputStreamReader(is));

		text = IOCommon.readerToString(br);


		return text;
	}


	
}
