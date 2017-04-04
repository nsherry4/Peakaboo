package peakaboo.ui.javafx.widgets.dialogheader;

import java.io.IOException;

import peakaboo.ui.javafx.change.IChangeController;
import peakaboo.ui.javafx.util.FXUtil;
import peakaboo.ui.javafx.util.IActofUIController;


public class DialogHeader extends IActofUIController {
	
	public enum Response {
		YES, NO;
	}
	
	private Runnable accept, cancel;
	
	public void onAccept(Runnable accept) {
		this.accept = accept;
	}
	public void onCancel(Runnable cancel) {
		this.cancel = cancel;
	}
	
	
	public void accept() {
		accept.run();
	}
	
	public void cancel() {
		cancel.run();
	}

	@Override
	public void ready() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	public static DialogHeader load() throws IOException {
		return load(null, null);
	}
    public static DialogHeader load(Runnable accept, Runnable cancel) throws IOException {
        DialogHeader header = FXUtil.load(DialogHeader.class, "DialogHeader.fxml", new IChangeController());
        header.onAccept(accept);
        header.onCancel(cancel);
        return header;
    }

	
}
