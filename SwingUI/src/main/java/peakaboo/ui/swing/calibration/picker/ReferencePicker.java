package peakaboo.ui.swing.calibration.picker;

import java.util.function.Consumer;


import peakaboo.calibration.CalibrationReference;
import peakaboo.ui.swing.calibration.referenceplot.ReferenceViewPanel;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.layerpanel.HeaderLayer;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;

public class ReferencePicker extends HeaderLayer {

	private Consumer<CalibrationReference> onOK;
	private ReferenceList reflist;
	private LayerPanel parent;
	
	private ImageButton ok, cancel;
	
	public ReferencePicker(LayerPanel parent) {
		super(parent);
		this.parent = parent;

		reflist = new ReferenceList(
				ref -> onOK.accept(ref), 
				this::showReferencePlot);
		
		ok = new ImageButton("OK").withStateDefault().withAction(() -> onOK.accept(reflist.getSelectedReference()));
		cancel = new ImageButton("Cancel").withAction(() -> removeLayer());
				
		getHeader().setComponents(cancel, "Select Z-Calibration Reference", ok);
		setBody(reflist);
		
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
	
	public void focus() {
		reflist.focusTable();
	}
	
	
	
}
