package org.peakaboo.framework.plural.monitor.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.eventful.EventfulEnumListener;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderBox;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.layers.ModalLayer;

public class TaskMonitorPanel extends JPanel {

	
	private HeaderBox header;
	private JPanel body;
	
	public TaskMonitorPanel(String title, TaskMonitor<?>... monitors) {
		this(title, Arrays.asList(monitors).stream().map(TaskMonitorView::new).collect(Collectors.toList()));
	}
	
	public TaskMonitorPanel(String title, TaskMonitorView... observerViews) {
		this(title, Arrays.asList(observerViews));
	}

	public TaskMonitorPanel(String title, List<TaskMonitorView> observerViews) {

		this.setLayout(new BorderLayout());
		
		header = makeHeader(title, observerViews);
		this.add(header, BorderLayout.NORTH);
		
		body = makeBody(observerViews);
		this.add(body, BorderLayout.CENTER);
		
	}
	
	
	static HeaderBox makeHeader(String title) {
		return makeHeader(title, Collections.emptyList());
	}
	
	static HeaderBox makeHeader(String title, List<TaskMonitorView> observerViews) {
		
		FluentButton cancel = new FluentButton("Cancel")
				.withStateCritical()
				.withAction(() -> {
					List<TaskMonitorView> reversed = new ArrayList<>(observerViews);
					Collections.reverse(reversed);
					for (TaskMonitorView v : reversed) {
						v.getExecutor().abort();
					}
				});
		
		return new HeaderBox(null, title, cancel);
	}
	
	static JPanel makeBody() {
		return makeBody(Collections.emptyList());
	}
	
	static JPanel makeBody(List<TaskMonitorView> observerViews) {
		
		JPanel body = new JPanel(new BorderLayout());
		body.setBorder(Spacing.bHuge());
		
		JPanel lineItems = new JPanel();
		lineItems.setBorder(new EmptyBorder(0, Spacing.huge, Spacing.huge, Spacing.huge));
		LayoutManager layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		lineItems.setLayout(layout);


		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		for (TaskMonitorView obsv : observerViews) {
			lineItems.add(obsv, c);
			c.gridy += 1;
		}
		body.add(lineItems, BorderLayout.CENTER);
		
		
		
		JProgressBar progress = new JProgressBar();
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setValue(0);
		body.add(progress, BorderLayout.SOUTH);
		
		for (TaskMonitorView v : observerViews) {
			v.getExecutor().addListener(event -> {
				TaskMonitor<?> exec = v.getExecutor();
				if (exec.getSize() <= 0) {
					progress.setIndeterminate(true);
				} else {
					progress.setIndeterminate(false);
					progress.setValue((int)(exec.getPercent()*100));
				}
			});
		}
		
		return body;
		
	}
	
	public static <T> void onLayerPanel(TaskMonitor<T> monitor, LayerPanel parent) {
		TaskMonitorLayer layer = new TaskMonitorLayer(parent, "Downloading", monitor);
		parent.pushLayer(layer);
		
		//listener which removes the progress panel and the listener if the state changes from RUNNING
		EventfulEnumListener<TaskMonitor.Event> listener = new EventfulEnumListener<TaskMonitor.Event>() {
			boolean handled = false;
			@Override
			public void change(Event message) {
				if (handled) { return; }
				if (monitor.getState() != TaskMonitor.State.RUNNING) {
					monitor.removeListener(this);
					parent.removeLayer(layer);
					handled = true;
				}
			}
		}; 
		monitor.addListener(listener);
		
		monitor.start();

	}
	
}
