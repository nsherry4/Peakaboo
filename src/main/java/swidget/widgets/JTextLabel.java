package swidget.widgets;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class JTextLabel extends JTextArea {

	private static JLabel template = new JLabel();
	
	public JTextLabel() {
		super();
		setWrapStyleWord(true);
		setLineWrap(true);
		setOpaque(false);
		setEditable(false);
		setFocusable(false);
		setBackground(template.getBackground());
		setForeground(template.getForeground());
		setFont(template.getFont());
		setBorder(template.getBorder());
	}
	
}
