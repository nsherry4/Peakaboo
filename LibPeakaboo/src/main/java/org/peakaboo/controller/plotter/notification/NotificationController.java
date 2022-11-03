package org.peakaboo.controller.plotter.notification;

import org.peakaboo.framework.eventful.EventfulType;

public class NotificationController extends EventfulType<NotificationController.Notice> {

	public record Notice(String message, Runnable action) {}
	
	

}
