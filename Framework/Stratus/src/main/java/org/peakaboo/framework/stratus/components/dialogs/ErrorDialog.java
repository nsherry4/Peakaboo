package org.peakaboo.framework.stratus.components.dialogs;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ErrorPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.header.HeaderDialog;
import org.peakaboo.framework.stratus.components.ui.layers.ToastLayer;

public class ErrorDialog extends HeaderDialog {

	private ErrorPanel error;
	
	public ErrorDialog(Window parent, String title, String message, Throwable t) {
		super(parent);
		
		getHeader().setCentre(title);
		error = new ErrorPanel(message, t);
		setBody(error);
		
		
		FluentButton copy = new FluentButton(StockIcon.EDIT_COPY)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Copy error to clipboard")
				.withAction(this::copyData);
		getHeader().setLeft(copy);
		
		pack();
		setLocationRelativeTo(parent);
		
	}
	
	private void copyData() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection sel = new StringSelection(error.getStackTrace());
		clipboard.setContents(sel, null);
		
		ToastLayer toast = new ToastLayer(this.getLayerRoot(), "Data copied to clipboard");
		this.getLayerRoot().pushLayer(toast);
		
	}

	
	
}
