package org.peakaboo.framework.plural.monitor.swing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;

public class TaskMonitorLayer extends HeaderLayer {

	public TaskMonitorLayer(LayerPanel owner, String title) {
		this(owner, title, Collections.emptyList());
	}

	public TaskMonitorLayer(LayerPanel owner, String title, TaskMonitor<?>... monitors) {
		this(owner, title, Arrays.asList(monitors).stream().map(TaskMonitorView::new).collect(Collectors.toList()));
	}
		
	public TaskMonitorLayer(LayerPanel owner, String title, TaskMonitorView observerView) {
		this(owner, title, List.of(observerView));		
	}
	
	public TaskMonitorLayer(LayerPanel owner, String title, List<TaskMonitorView> observerViews) {
		super(owner, TaskMonitorPanel.makeHeader(title, observerViews), TaskMonitorPanel.makeBody(observerViews));		
	}
	
}
