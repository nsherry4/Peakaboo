package swidget.dialogues;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import swidget.icons.StockIcon;
import swidget.widgets.ErrorPanel;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonSize;
import swidget.widgets.layerpanel.ToastLayer;

public class ErrorDialog extends HeaderDialog {

	private ErrorPanel error;
	
	public ErrorDialog(Window parent, String title, Throwable t) {
		super(parent);
		
		getHeader().setCentre(title);
		error = new ErrorPanel(t);
		setBody(error);
		
		ImageButton copy = new ImageButton(StockIcon.EDIT_COPY)
				.withButtonSize(ImageButtonSize.LARGE)
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
