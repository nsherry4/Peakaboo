package peakaboo.ui.swing.calibration;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JPanel;

import peakaboo.mapping.calibration.CalibrationReference;
import swidget.widgets.HeaderBox;
import swidget.widgets.HeaderBoxPanel;
import swidget.widgets.ImageButton;

public class ReferencePickerBox extends JPanel {

	private Consumer<CalibrationReference> onOK;
	private Runnable onCancel;
	
	public ReferencePickerBox() {
		setLayout(new BorderLayout());
		
		ReferenceList reflist = new ReferenceList();
		
		ImageButton ok = new ImageButton("OK").withStateDefault().withAction(() -> onOK.accept(reflist.getSelectedReference()));
		ImageButton cancel = new ImageButton("Cancel").withAction(() -> onCancel.run());
		HeaderBox header = new HeaderBox(cancel, "Select Calibration Reference", ok);
		
		
		HeaderBoxPanel panel = new HeaderBoxPanel(header, reflist);
		this.add(panel, BorderLayout.CENTER);
		
		ok.requestFocus();
		ok.grabFocus();
		
	}

	public void setOnOK(Consumer<CalibrationReference> onOK) {
		this.onOK = onOK;
	}

	public void setOnCancel(Runnable onCancel) {
		this.onCancel = onCancel;
	}
	
	
	
	
	
}
