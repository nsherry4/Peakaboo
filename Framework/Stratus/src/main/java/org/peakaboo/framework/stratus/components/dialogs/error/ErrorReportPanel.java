package org.peakaboo.framework.stratus.components.dialogs.error;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.peakaboo.framework.stratus.api.Spacing;

public class ErrorReportPanel extends JPanel {

	private JTextArea notesArea;
	private JCheckBox logCheck;
	
	public ErrorReportPanel() {
		setLayout(new BorderLayout(Spacing.huge, Spacing.huge));
		setBorder(Spacing.bHuge());
		
		//Notes
		var notesPanel = new JPanel(new BorderLayout());
		var notesLabel = new JLabel("Additional notes or comments");
		notesLabel.setFont(notesLabel.getFont().deriveFont(Font.BOLD));
		notesArea = new JTextArea();
		notesPanel.add(notesLabel, BorderLayout.NORTH);
		notesPanel.add(notesArea, BorderLayout.CENTER);
		this.add(notesPanel, BorderLayout.CENTER);
		
		//Attach logs
		logCheck = new JCheckBox("Attach recent logs to crash report");
		this.add(logCheck, BorderLayout.SOUTH);
		
	}
	
	public String getNotes() {
		return notesArea.getText();
	}
	
	public boolean getAttachLogs() {
		return logCheck.isSelected();
	}
	
}
