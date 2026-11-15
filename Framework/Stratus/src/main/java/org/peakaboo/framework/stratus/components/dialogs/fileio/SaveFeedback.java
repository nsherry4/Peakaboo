package org.peakaboo.framework.stratus.components.dialogs.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;

/**
 * Tells the user how a save or export went: a toast when it worked, a plain
 * message naming the file and the reason when it didn't. Ordinary I/O failures
 * like a read-only folder or a full disk aren't crashes, so they're logged as
 * warnings rather than going through the crash report dialog. Safe to call
 * from any thread.
 */
public class SaveFeedback {

	private SaveFeedback() {}

	/**
	 * Shows a toast confirming that the given file was saved
	 */
	public static void succeeded(LayerPanel owner, File file) {
		onEDT(() -> owner.showToast("Saved " + file.getName()));
	}

	/**
	 * Logs the failure as a warning and shows a message naming the file and the
	 * reason it couldn't be saved. Only pass ordinary failures to write the file
	 * here; a bug should still go to the crash report dialog.
	 */
	public static void failed(LayerPanel owner, File file, Exception e) {
		OneLog.log(Level.WARNING, "Failed to save " + file.getAbsolutePath(), e);

		String folder = file.getAbsoluteFile().getParent();
		String body = "Couldn't save " + file.getName()
				+ (folder == null ? "" : " in " + folder) + ".\n"
				+ "Reason: " + reason(e);
		onEDT(() -> new LayerDialog("Save Failed", body, StockIcon.BADGE_ERROR).showIn(owner));
	}

	/**
	 * Turns a failed write into a short reason a user can act on
	 */
	static String reason(Exception e) {
		// Wrapping exceptions (eg from data sinks) usually say less than their cause
		if (!(e instanceof IOException) && e.getCause() instanceof IOException cause) {
			e = cause;
		}
		String message = e.getMessage();

		// FileOutputStream reports this as "/some/path (Permission denied)"
		if (e instanceof FileNotFoundException && message != null) {
			int open = message.lastIndexOf(" (");
			if (open >= 0 && message.endsWith(")")) {
				return message.substring(open + 2, message.length() - 1);
			}
		}

		// NIO exceptions put the path in the message, not the reason
		if (e instanceof AccessDeniedException) {
			return "Permission denied";
		}
		if (e instanceof NoSuchFileException) {
			return "The folder doesn't exist";
		}
		if (e instanceof FileSystemException fse && fse.getReason() != null) {
			return fse.getReason();
		}

		if (message != null && !message.isBlank()) {
			return message;
		}
		return e.getClass().getSimpleName();
	}

	private static void onEDT(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			SwingUtilities.invokeLater(r);
		}
	}

}