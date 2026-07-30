package org.peakaboo.framework.plural.monitor.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;


public class TaskMonitorView extends JPanel {

	
	private TaskMonitor<?> exec;
	private Font originalFont;
	
	public TaskMonitorView(TaskMonitor<?> exec) {
		super();
		
		this.exec = exec;
		setLayout(new BorderLayout(8, 8));
		setBorder(Spacing.bSmall());
		
		Dimension d = new Dimension(16, 16);
		
		JLabel text = new JLabel(exec.getName());
		text.setHorizontalAlignment(SwingConstants.CENTER);
		text.setFont(text.getFont().deriveFont(text.getFont().getSize() + 1f));
		this.originalFont = text.getFont();
		
		this.add(text, BorderLayout.CENTER);
		
		
		exec.addListener(event -> {
			var green = Stratus.getTheme().emphasize(
					Stratus.getTheme().getPalette().getColour("Green", "5"), 0.1f
			);
			if (event == Event.COMPLETED) {
				text.setForeground(green);
				text.setFont(originalFont);
			}
			if (event == Event.PROGRESS && exec.getCount() > 0) {
				text.setFont(originalFont.deriveFont(Font.BOLD));
				text.setForeground(green);
			}
		});
		
		
	}
	
	public TaskMonitor<?> getExecutor() {
		return exec;
	}
	
}
