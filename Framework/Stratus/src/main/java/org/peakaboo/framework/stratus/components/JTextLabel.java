package org.peakaboo.framework.stratus.components;

import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Painter;
import javax.swing.UIDefaults;

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

		// Outside a scroll pane, text areas get drawn like text fields, which a label
		// shouldn't look like
		UIDefaults overrides = new UIDefaults();
		Painter<JTextLabel> none = (Graphics2D g, JTextLabel object, int width, int height) -> {};
		overrides.put("TextArea[Enabled+NotInScrollPane].backgroundPainter", none);
		overrides.put("TextArea[Focused+NotInScrollPane].backgroundPainter", none);
		overrides.put("TextArea[Enabled+NotInScrollPane].borderPainter", none);
		overrides.put("TextArea[Focused+NotInScrollPane].borderPainter", none);
		putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
		putClientProperty("Nimbus.Overrides", overrides);
	}

	public JTextLabel(String text) {
		this();
		setText(text);
	}

}
