package peakaboo.ui.swing.calibration.profileplot;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.mapping.calibration.CalibrationProfile;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ToolbarImageButton;
import swidget.widgets.layout.ButtonBox;

public class ProfileManager extends ProfileViewPanel {

		
	public ProfileManager(PlotPanel parent, FittingController controller, Runnable onClose) {
		super();
		
		ImageButton cancel = new ImageButton(StockIcon.WINDOW_CLOSE).withTooltip("Close").withBordered(false).withAction(onClose);
		
		ButtonBox box = new ButtonBox(Spacing.tiny, false);
		box.setOpaque(false);
		
		ImageButton open = new ImageButton(StockIcon.DOCUMENT_OPEN).withTooltip("Load Z-Calibration").withBordered(false).withAction(() -> {
			parent.actionLoadCalibrationProfile();
		});
		box.addLeft(open);
		
		ImageButton clear = new ImageButton(StockIcon.EDIT_CLEAR).withTooltip("Clear Z-Calibration").withBordered(false).withAction(() -> {
			controller.setCalibrationProfile(new CalibrationProfile(), null);
		});
		box.addLeft(clear);
		
		controller.addListener(t -> {
			for (ProfilePlot plot : profileplots) {
				plot.setCalibrationProfile(controller.getCalibrationProfile(), controller.getCalibrationProfileFile());
			}
		});
		
		init(controller.getCalibrationProfile(), controller.getCalibrationProfileFile(), box, cancel);
		
	}
	
}
