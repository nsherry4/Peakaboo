package org.peakaboo.framework.stratus.components.dialogs.error;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;

public class ErrorDisplayPanel extends JPanel {

	
	private Optional<Throwable> t;
	
	
	public ErrorDisplayPanel(Throwable t) {
		this(null, t);
	}
	
	public ErrorDisplayPanel(String msg, Throwable t) {
		
		String errorMessage = "";
		String errorType = "";

		if (t != null) {
			errorType = t.getClass().getSimpleName();
			errorMessage = t.getMessage();
			if (errorMessage == null) {
				errorMessage = "";
			}
		}
		
		this.t = Optional.of(t);
		
		
		setLayout(new BorderLayout(Spacing.huge, Spacing.huge));
		setBorder(Spacing.bHuge());
		
		setPreferredSize(new Dimension(600, 350));
		
		
		JTextArea tracer = new JTextArea(getStackTrace());
		tracer.setEditable(false);
		JScrollPane scroller = new JScrollPane(tracer);
		add(scroller, BorderLayout.CENTER);
		
		
		JPanel north = new JPanel(new BorderLayout(Spacing.huge, Spacing.huge));
		
		String title = "<div style='font-size: 115%; padding-top: 0px; padding-bottom: 5px;'>Error of type " + errorType + "</div>";
		if (msg == null) {
			msg = "";
		}
		
		String longMessage = errorMessage;
		if (longMessage.length() > 100) {
			longMessage = longMessage.substring(0, 99) + "…";
		}
		String topText = "<html>" + title + "<div>" + msg + "</div>" + longMessage + "</div></html>";
		JLabel lblMessage = new JLabel(topText);
		lblMessage.setBorder(new EmptyBorder(Spacing.medium, 0, Spacing.medium, 0));
		north.add(lblMessage, BorderLayout.CENTER);
		
		JLabel icon = new JLabel(StockIcon.BADGE_ERROR.toImageIcon(IconSize.ICON));
		north.add(icon, BorderLayout.WEST);
		
		add(north, BorderLayout.NORTH);
		
	}
	
	public String getStackTrace() {
		if (t.isEmpty()) return "";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.get().printStackTrace(pw);
		return sw.toString();
	}
	
}
