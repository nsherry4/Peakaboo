package peakaboo.ui.swing.calibration.picker;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.JPanel;

import peakaboo.calibration.CalibrationReference;
import peakaboo.ui.swing.calibration.referenceplot.ReferenceViewPanel;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.layout.HeaderBoxPanel;

public class ReferencePicker extends JPanel {

	private Consumer<CalibrationReference> onOK;
	private Runnable onCancel;
	private ReferenceList reflist;
	private LayerPanel parent;
	
	public ReferencePicker(LayerPanel parent) {
		this.parent = parent;
		setLayout(new BorderLayout());
		
		reflist = new ReferenceList(ref -> onOK.accept(ref), this::showReferencePlot) ;
		
		ImageButton ok = new ImageButton("OK").withStateDefault().withAction(() -> onOK.accept(reflist.getSelectedReference()));
		ImageButton cancel = new ImageButton("Cancel").withAction(() -> onCancel.run());
		HeaderBox header = new HeaderBox(cancel, "Select Z-Calibration Reference", ok);
		
		
		HeaderBoxPanel panel = new HeaderBoxPanel(header, reflist);
		this.add(panel, BorderLayout.CENTER);
		
		reflist.focusTable();
		
	}

	private void showReferencePlot(CalibrationReference reference) {
		
		ReferenceViewPanel view = new ReferenceViewPanel(reference);
		ModalLayer layer = new ModalLayer(this.parent, view);
		view.setOnClose(() -> parent.removeLayer(layer));
		parent.pushLayer(layer);
		
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
