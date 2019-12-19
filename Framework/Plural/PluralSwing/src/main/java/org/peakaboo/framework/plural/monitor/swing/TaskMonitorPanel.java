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
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.ModalLayer;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;

public class TaskMonitorPanel extends JPanel {

	private FluentButton cancel; 
	
	public TaskMonitorPanel(String title, TaskMonitor<?>... monitors) {
		this(title, Arrays.asList(monitors).stream().map(TaskMonitorView::new).collect(Collectors.toList()));
	}
	
	public TaskMonitorPanel(String title, TaskMonitorView... observerViews) {
		this(title, Arrays.asList(observerViews));
	}

	public TaskMonitorPanel(String t, List<TaskMonitorView> observerViews) {

		this.setLayout(new BorderLayout());
		
		cancel = new FluentButton("Cancel")
				.withStateCritical()
				.withAction(() -> {
					List<TaskMonitorView> reversed = new ArrayList<>(observerViews);
					Collections.reverse(reversed);
					for (TaskMonitorView v : reversed) {
						v.getExecutor().abort();
					}
				});

		
		HeaderBox header = new HeaderBox(null, t, cancel);
		this.add(header, BorderLayout.NORTH);
		
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(Spacing.bHuge());
		
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
		center.add(lineItems, BorderLayout.CENTER);
		
		
		
		JProgressBar progress = new JProgressBar();
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setValue(0);
		center.add(progress, BorderLayout.SOUTH);

		
		
		this.add(center, BorderLayout.CENTER);
		
		


		

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

	}
	
	public static <T> void onLayerPanel(TaskMonitor<T> monitor, LayerPanel parent) {
		TaskMonitorPanel progressPanel = new TaskMonitorPanel("Downloading", monitor);
		ModalLayer layer = new ModalLayer(parent, progressPanel);
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
