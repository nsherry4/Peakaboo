package org.peakaboo.framework.stratus.components.dialogs.error;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.Box;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.header.HeaderDialog;
import org.peakaboo.framework.stratus.components.ui.layers.ToastLayer;

public class ErrorDialog extends HeaderDialog {

	private ErrorDisplayPanel error;
	private ErrorReportPanel report;
	private Consumer<ErrorReport> onReport;
	private Throwable throwable;
	
	public static record ErrorReport(Throwable t, String notes, boolean includeLogs) {};
	
	public ErrorDialog(Window parent, String title, String message, Throwable t) {
		this(parent, title, message, t, null);
	}
	
	public ErrorDialog(Window parent, String title, String message, Throwable t, Consumer<ErrorReport> onReport) {
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
	
	private ButtonLinker errorButtons() {
		var buttons = new ArrayList<AbstractButton>();
		FluentButton copy = new FluentButton(StockIcon.EDIT_COPY)
				.withText("Copy")
				.withTooltip("Copy error to clipboard")
				.withAction(this::copyData);
		buttons.add(copy);
		
		if (this.onReport != null) {
			FluentButton submit = new FluentButton(StockIcon.DOCUMENT_EXPORT_SYMBOLIC)
					.withText("Report")
					.withTooltip("Send crash report to developers")
					.withAction(() -> {
						setBody(report);
						getHeader().setLeft(reportButtons());
						repaint();
					});
			buttons.add(submit);
		}
		

		ButtonLinker linker = new ButtonLinker(buttons);
		return linker;
	}
	
	private ButtonLinker reportButtons() {
		var buttons = new ArrayList<AbstractButton>();
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
						onReport.accept(new ErrorReport(throwable, report.getNotes(), report.getAttachLogs()));
					});
			buttons.add(send);
		}
		

		ButtonLinker linker = new ButtonLinker(buttons);
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