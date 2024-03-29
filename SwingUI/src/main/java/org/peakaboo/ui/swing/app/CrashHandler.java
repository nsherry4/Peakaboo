package org.peakaboo.ui.swing.app;

import java.util.function.Consumer;

import org.peakaboo.app.Env;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Version;
import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.stratus.components.dialogs.error.ErrorDialog;
import org.peakaboo.framework.stratus.components.dialogs.error.ErrorDialog.Feedback;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.tier.Tier;

import com.bugsnag.Bugsnag;
import com.bugsnag.Severity;

public class CrashHandler {

	private static CrashHandler handler;
	
	public static void init() {
		if (handler != null) return; //subsequent calls do nothing
		handler = new CrashHandler();
	}
	
	public static CrashHandler get() {
		return handler;
	}
	
	/* ---------------------------------------- */
	
	private Bugsnag bugsnag;
	
	public CrashHandler() {
		var devbuild = Version.releaseType != Version.ReleaseType.RELEASE;
		boolean autosubmit=DesktopSettings.isCrashAutoreporting() || devbuild;

		//Create a new BugSnag instance with our id
		this.bugsnag = new Bugsnag("4b9ef1b9c7b6851433ddaceb7155e2db", autosubmit);
		this.bugsnag.setReleaseStage(Version.releaseType.toString());
		this.bugsnag.setAppVersion(Version.longVersionNo);
		this.bugsnag.setSendThreads(true);		
	}

	public void handle(String message, Throwable throwable) {
		
		var reported = new Mutable<Boolean>(false);
		
		Consumer<Feedback> doReport = (feedback) -> {
			bugsnag.notify(throwable, Severity.ERROR, report -> {
				report.addToTab("Peakaboo", "heapsize", Env.maxHeapBytes());
				report.addToTab("Peakaboo", "version", Version.longVersionNo);
				report.addToTab("Peakaboo", "build-date", Version.buildDate);
				report.addToTab("Peakaboo", "tier", Tier.provider().tierName());
				
				report.addToTab("Plugins", "filters", FilterRegistry.system().infodump());
				report.addToTab("Plugins", "datasources", DataSourceRegistry.system().infodump());
				report.addToTab("Plugins", "datasinks", DataSinkRegistry.system().infodump());
				report.addToTab("Plugins", "mapfilters", MapFilterRegistry.system().infodump());						

				if (feedback != null) {
					report.addToTab("User", "notes", feedback.notes());
					//TODO: add logs if the user chose to include them
					if (feedback.includeLogs()) {
						String logs = PeakabooLog.getRecentLogs();
						report.addToTab("User", "logs", logs);
					}
				}
				
				reported.set(true);
				
			});
		};
		
		
		ErrorDialog errorDialog = new ErrorDialog(null, "Peakaboo Error", message, throwable, doReport);
		
		errorDialog.setModal(true);
		errorDialog.setVisible(true); //stalls here until dialog closes
		
		if (! reported.get() && DesktopSettings.isCrashAutoreporting()) {
			//When autoreporting is on, we report even when the user doesn't send additional feedback
			doReport.accept(null);
		}
	}

	
}
