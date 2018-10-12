package peakaboo.ui.swing.calibration;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.JPanel;

import peakaboo.mapping.calibration.CalibrationReference;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.layout.HeaderBoxPanel;

public class ReferencePicker extends JPanel {

	private Consumer<CalibrationReference> onOK;
	private Runnable onCancel;
	private ReferenceList reflist;
	
	public ReferencePicker() {
		setLayout(new BorderLayout());
		
		reflist = new ReferenceList(ref -> onOK.accept(ref));
		
		ImageButton ok = new ImageButton("OK").withStateDefault().withAction(() -> onOK.accept(reflist.getSelectedReference()));
		ImageButton cancel = new ImageButton("Cancel").withAction(() -> onCancel.run());
		HeaderBox header = new HeaderBox(cancel, "Select Calibration Reference", ok);
		
		
		HeaderBoxPanel panel = new HeaderBoxPanel(header, reflist);
		this.add(panel, BorderLayout.CENTER);
		
		reflist.focusTable();
		
	}

	public void setOnOK(Consumer<CalibrationReference> onOK) {
		this.onOK = onOK;
	}

	public void setOnCancel(Runnable onCancel) {
		this.onCancel = onCancel;
	}
	
	
	public void focus() {
		reflist.focusTable();
	}
	
	
	
}
