package org.peakaboo.framework.swidget.dialogues;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog.MessageType;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;
import org.peakaboo.framework.swidget.widgets.layout.PropertyPanel;
import org.peakaboo.framework.swidget.widgets.layout.TitledPanel;


public class AboutDialogue extends JDialog
{
	
	Window owner;

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

	public AboutDialogue(Window owner, Contents contents)
	{
		super(owner, "About " + contents.name, ModalityType.DOCUMENT_MODAL);
		init(owner, contents);
	}
		
	private void init(Window owner, Contents contents)
	{
		this.owner = owner;
		
		setResizable(false);
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		JPanel panel = new JPanel(new BorderLayout());
		c.add(panel, BorderLayout.CENTER);
		
		
		
		
		
		ButtonBox bbox = new ButtonBox();
		
		ImageButton btnCredit = new ImageButton()
				.withIcon(StockIcon.MISC_ABOUT, IconSize.BUTTON)
				.withText("Credits")
				.withTooltip("View Credits")
				.withAction(() -> {
					TitledPanel creditsTitledPanel = new TitledPanel(creditsPanel(contents), true);
					new PropertyDialogue("Credits", AboutDialogue.this, creditsTitledPanel);
					
				});
		bbox.addLeft(btnCredit);
		
		
		ImageButton btnLicence = new ImageButton()
				.withIcon(StockIcon.MIME_TEXT, IconSize.BUTTON)
				.withText("Licence")
				.withTooltip("View Licence")
				.withAction(() -> {
					new LayerDialog(contents.name + " Licence", textForJOptionPane(contents.licence), MessageType.INFO).showInWindow(AboutDialogue.this);
				});

		bbox.addLeft(btnLicence);	


		ImageButton btnClose = new ImageButton().withIcon(StockIcon.WINDOW_CLOSE, IconSize.BUTTON).withText("Close").withTooltip("Close this window");
		btnClose.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent arg0)
			{
				setVisible(false);
			}
		});
		bbox.addRight(btnClose);
		
		
		
		
		panel.add(aboutPanel(contents), BorderLayout.CENTER);
		panel.add(bbox, BorderLayout.SOUTH);
		
		
		pack();

		
		setLocationRelativeTo(owner);
		setVisible(true);
		
	}
	
	public static PropertyPanel creditsPanel(Contents contents) {
		Map<String, String> credits = new LinkedHashMap<>();
		for (String credit : contents.credits.split("\n")) {
			if (credit.trim().length() == 0) {
				continue;
			}
			String[] creditParts = credit.split(": ");
			credits.put(creditParts[0], creditParts[1]);
		}
		return new PropertyPanel(credits);
	}
	
	public static JPanel aboutPanel(Contents contents) {

		JPanel infopanel = new JPanel(new GridBagLayout());
		infopanel.setBackground(Color.WHITE);
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
	
	
    public JScrollPane textForJOptionPane(String text) {

		JTextArea ta = new JTextArea();
		ta.setText(text);
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		
		ta.setOpaque(false);
		
		
		
		JScrollPane s = new JScrollPane(ta);
		s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		
		s.setOpaque(false);
		
		s.setPreferredSize(new Dimension(500, 200));
		s.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		return s;
    	
    }
    

	
}
