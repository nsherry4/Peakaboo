package org.peakaboo.framework.stratus.components.dialogs.error;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderDialog;
import org.peakaboo.framework.stratus.components.ui.layers.ToastLayer;

public class ErrorDialog extends HeaderDialog {

	private ErrorDisplayPanel error;
	private ErrorReportPanel report;
	private Consumer<Feedback> onReport;
	private Throwable throwable;
	
	public static record Feedback(Throwable t, String notes, boolean includeLogs) {};
	
	public ErrorDialog(Window parent, String title, String message, Throwable t) {
		this(parent, title, message, t, null);
	}
	
	public ErrorDialog(Window parent, String title, String message, Throwable t, Consumer<Feedback> onReport) {
		super(parent);
		this.onReport = onReport;
		this.throwable = t;
		this.error = new ErrorDisplayPanel(message, throwable);
		this.report = new ErrorReportPanel();
		
		
		
		setPreferredSize(new Dimension(600, 350));
		
		getHeader().setCentre(title);
		setBody(error);
		repaint();

		getHeader().setLeft(errorButtons());
		
		pack();
		setLocationRelativeTo(parent);
		
	}
	
	private ComponentStrip errorButtons() {
		var buttons = new ArrayList<JComponent>();
		FluentButton copy = new FluentButton()
				.withIcon(StockIcon.EDIT_COPY, Stratus.getTheme().getControlText())
				.withText("Copy")
				.withTooltip("Copy error to clipboard")
				.withAction(this::copyData);
		buttons.add(copy);
		
		if (this.onReport != null) {
			FluentButton submit = new FluentButton()
					.withIcon(StockIcon.DOCUMENT_EXPORT_SYMBOLIC, Stratus.getTheme().getControlText())
					.withText("Report")
					.withTooltip("Send crash report to developers")
					.withAction(() -> {
						setBody(report);
						getHeader().setLeft(reportButtons());
						repaint();
					});
			buttons.add(submit);
		}
		

		ComponentStrip linker = new ComponentStrip(buttons);
		return linker;
	}
	
	private ComponentStrip reportButtons() {
		var buttons = new ArrayList<JComponent>();
		FluentButton back = new FluentButton(StockIcon.GO_PREVIOUS)
				.withText("Back")
				.withTooltip("Back to error message")
				.withAction(() -> {
					setBody(error);
					getHeader().setLeft(errorButtons());
					repaint();
				});
		buttons.add(back);
		
		if (this.onReport != null) {
			FluentButton send = new FluentButton(StockIcon.DOCUMENT_EXPORT_SYMBOLIC)
					.withText("Send")
					.withTooltip("Send crash report to developers")
					.withAction(() -> {
						ErrorDialog.this.close();
						onReport.accept(new Feedback(throwable, report.getNotes(), report.getAttachLogs()));
					});
			buttons.add(send);
		}
		

		ComponentStrip linker = new ComponentStrip(buttons);
		return linker;
	}
	
	private void copyData() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection sel = new StringSelection(error.getStackTrace());
		clipboard.setContents(sel, null);
		
		ToastLayer toast = new ToastLayer(this.getLayerRoot(), "Data copied to clipboard");
		this.getLayerRoot().pushLayer(toast);
		
	}
	
	

	
	
}
