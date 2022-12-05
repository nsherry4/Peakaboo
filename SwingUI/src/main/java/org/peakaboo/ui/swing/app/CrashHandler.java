package org.peakaboo.ui.swing.app;

import org.peakaboo.app.Env;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Version;
import org.peakaboo.datasink.plugin.DataSinkPluginManager;
import org.peakaboo.datasource.plugin.DataSourcePluginManager;
import org.peakaboo.filter.model.FilterPluginManager;
import org.peakaboo.framework.stratus.components.dialogs.error.ErrorDialog;
import org.peakaboo.mapping.filter.model.MapFilterPluginManager;
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
		this.bugsnag = new Bugsnag("4b9ef1b9c7b6851433ddaceb7155e2db", /*autosubmit=*/false);
	}
	
	public void handle(String message, Throwable throwable) {
		ErrorDialog errorDialog = new ErrorDialog(null, "Peakaboo Error", message, throwable, reportData -> {
			bugsnag.notify(throwable, Severity.ERROR, report -> {
				report.addToTab("Peakaboo", "heapsize", Env.maxHeapBytes());
				report.addToTab("Peakaboo", "version", Version.longVersionNo);
				report.addToTab("Peakaboo", "build-date", Version.buildDate);
				report.addToTab("Peakaboo", "tier", Tier.provider().tierName());
				
				report.addToTab("User", "notes", reportData.notes());
				//TODO: add logs if the user chose to include them
				if (reportData.includeLogs()) {
					String logs = PeakabooLog.getRecentLogs();
					report.addToTab("User", "logs", logs);
				}
				
				report.addToTab("Plugins", "filters", FilterPluginManager.system().infodump());
				report.addToTab("Plugins", "datasources", DataSourcePluginManager.system().infodump());
				report.addToTab("Plugins", "datasinks", DataSinkPluginManager.system().infodump());
				report.addToTab("Plugins", "mapfilters", MapFilterPluginManager.system().infodump());						
				
			});
		});
		
		errorDialog.setVisible(true);
	}
	
}
