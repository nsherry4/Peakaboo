package org.peakaboo.framework.plural.monitor.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.peakaboo.framework.eventful.EventfulConfig;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;

public class TaskMonitorView extends JPanel {

	
	private TaskMonitor<?> exec;
	
	public TaskMonitorView(TaskMonitor<?> exec) {
		super();
		
		this.exec = exec;
		setLayout(new BorderLayout(8, 8));
		setBorder(Spacing.bSmall());
		
		JLabel icon = new JLabel();
		Dimension d = new Dimension(16, 16);
		icon.setMinimumSize(d);
		icon.setMaximumSize(d);
		icon.setPreferredSize(d);
		this.add(icon, BorderLayout.WEST);
		
		JLabel text = new JLabel(exec.getName());
		this.add(text, BorderLayout.CENTER);
		
		exec.addListener(event -> {
			if (event == Event.COMPLETED) {
				icon.setIcon(StockIcon.PROCESS_COMPLETED.toImageIcon(IconSize.BUTTON));
			}
			if (event == Event.PROGRESS && exec.getCount() > 0) {
				icon.setIcon(StockIcon.GO_NEXT.toImageIcon(IconSize.BUTTON));
			}
		});
		
		
	}
	
	public TaskMonitor<?> getExecutor() {
		return exec;
	}
	
}
