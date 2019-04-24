package org.peakaboo.framework.swidget.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

public class ErrorPanel extends JPanel {

	private Throwable t;
	
	
	public ErrorPanel(Throwable t) {
		this(null, t);
	}
	
	public ErrorPanel(String msg, Throwable t) {
		if (t == null) {
			throw new RuntimeException("Error Panel cannot be shown without exception");
		}
		this.t = t;
		setLayout(new BorderLayout(Spacing.huge, Spacing.huge));
		setBorder(Spacing.bHuge());
		
		setPreferredSize(new Dimension(600, 350));
		
		
		JTextArea tracer = new JTextArea(getStackTrace());
		tracer.setEditable(false);
		tracer.setBackground(Color.WHITE);
		JScrollPane scroller = new JScrollPane(tracer);
		add(scroller, BorderLayout.CENTER);
		
		
		JPanel north = new JPanel(new BorderLayout(Spacing.huge, Spacing.huge));
		
		String realText = "";
		if (t.getMessage() != null) {
			realText = "<div style='font-size: 115%; padding-top: 0px; padding-bottom: 5px;'>" + t.getMessage() + "</div>";
		}
		if (msg == null) {
			msg = "The problem is of type " + t.getClass().getSimpleName();
		}
		realText += "<div>" + msg + "</div>";
		realText = "<html>" + realText + "</html>";
		JLabel lblMessage = new JLabel(realText);
		lblMessage.setBorder(new EmptyBorder(Spacing.medium, 0, Spacing.medium, 0));
		north.add(lblMessage, BorderLayout.CENTER);
		
		JLabel icon = new JLabel(StockIcon.BADGE_ERROR.toImageIcon(IconSize.ICON));
		north.add(icon, BorderLayout.WEST);
		
		add(north, BorderLayout.NORTH);
		
	}
	
	public String getStackTrace() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	
}
