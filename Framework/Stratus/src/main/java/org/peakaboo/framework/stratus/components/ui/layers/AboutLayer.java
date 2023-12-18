package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.JTextLabel;
import org.peakaboo.framework.stratus.components.layouts.CenteringLayout;
import org.peakaboo.framework.stratus.components.panels.PropertyPanel;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.header.HeaderTabBuilder;

public class AboutLayer extends HeaderLayer {

	public static class Contents {
		public String name;
		public String description;
		public String copyright;
		public String licence;
		public String credits;
		public ImageIcon logo;
		public String version;
		public String longVersion;
		public String releaseDescription;
		public String date;
		public String linktext;
		public Runnable linkAction;
	}
	
	public AboutLayer(LayerPanel owner, Contents contents) {
		super(owner, true);
		
		Component about = aboutPanel(contents);
		Component credits = creditsPanel(contents);
		Component licence = licencePanel(contents);
			
		HeaderTabBuilder tb = new HeaderTabBuilder();
		tb.addTab("About", about);
		tb.addTab("Credits", credits);
		tb.addTab("Licence", licence);
		
		
		
		Component body = tb.getBody();
		body.setPreferredSize(new Dimension(610, 280));
		getHeader().setCentre(tb.getTabStrip());
		setBody(body);
		
		
	}

	private static Component creditsPanel(Contents contents) {
		
		Map<String, String> credits = new LinkedHashMap<>();
		for (String credit : contents.credits.split("\n")) {
			if (credit.trim().length() == 0) {
				continue;
			}
			String[] creditParts = credit.split(": ");
			credits.put(creditParts[0], creditParts[1]);
		}
		PropertyPanel creditsPanel = new PropertyPanel(credits, false, 0);
		JPanel container = new JPanel(new CenteringLayout());
		container.add(creditsPanel);
		return container;
	}
	
	private static Component licencePanel(Contents contents) {
		return Stratus.scrolled(new JTextLabel(contents.licence));
	}

	public static JPanel aboutPanel(Contents contents) {

		JPanel infopanel = new JPanel(new GridBagLayout());
		infopanel.setBackground(Stratus.getTheme().getRecessedControl());
		//infopanel.setBorder(new EmptyBorder(0, 50, Spacing.large, 50));		
		infopanel.setBorder(Spacing.bHuge());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 0.0;
		gc.weighty = 1.0;
		gc.gridheight = 3;
		gc.insets = Spacing.iHuge();
		
		JLabel iconLabel = new JLabel(contents.logo);
		//iconLabel.setOpaque(true);
		//iconLabel.setBackground(Color.black);
		infopanel.add(iconLabel, gc);

		
		//gc.gridy += 1;
		gc.gridy = 0;
		gc.gridx++;
		gc.gridheight = 1;
		
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		
		JLabel title = new JLabel();
		title.setFont(title.getFont().deriveFont(Font.PLAIN));
		title.setText(
			"<html><div style='text-align: left; width: 250px;'>" +
				"<span style='font-size: 250%;'>" + contents.name + " " + contents.version + "</span>" +
				(("".equals(contents.releaseDescription)) ? "" : "<br><font color=\"#c00000\">" + contents.releaseDescription + "</font>") +  
			"</div></html>");
		
		gc.anchor = GridBagConstraints.NORTHWEST;
		infopanel.add(title, gc);

	
		JLabel text = new JLabel();
		text.setFont(text.getFont().deriveFont(Font.PLAIN));
		text.setText(
			"<html><div style='text-align: left; width: 250px;'>" +
				"<br>" +
				"<br>" +
				contents.description + 
				"<br>" +
				"<font size=\"-2\">" +
					"<font color=\"#777777\">Version " + contents.longVersion +
					"<br>" + 
					"Build Date: " + contents.date + 
					"</font>" +
					"<br>" +
					"<br>" +
					"Copyright &copy; " + contents.copyright +
				"</font>" +
				"<br>" +
				"<br>" +
			"</div></html>");
		
		gc.gridy++;
		gc.anchor = GridBagConstraints.NORTHWEST;
		infopanel.add(text, gc);
		
		
		if (contents.linktext != null && contents.linkAction != null) {
			JLabel weblabel = new JLabel("<html><u>" + contents.linktext + "</u></html>");
			weblabel.addMouseListener(new MouseListener() {
				
				public void mouseReleased(MouseEvent e){}
			
				public void mousePressed(MouseEvent e){}
			
				public void mouseExited(MouseEvent e){}
				
				public void mouseEntered(MouseEvent e){}
				
				public void mouseClicked(MouseEvent e){
					contents.linkAction.run();
				}
			});
			weblabel.setForeground(Color.blue);
			weblabel.setBackground(Color.black);
			gc.gridy++;
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.SOUTHWEST;
			infopanel.add(weblabel, gc);
		}
		
		return infopanel;
	}
	


}
