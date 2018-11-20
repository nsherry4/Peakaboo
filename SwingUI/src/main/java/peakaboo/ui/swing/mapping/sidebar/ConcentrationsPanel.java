package peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cyclops.Coord;
import peakaboo.calibration.Composition;
import peakaboo.controller.mapper.MappingController;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import swidget.widgets.Spacing;
import swidget.widgets.layout.PropertyPanel;

public class ConcentrationsPanel extends JPanel {

	private MappingController controller;
	private PropertyPanel view;
	
	public ConcentrationsPanel(MappingController controller) {
		this.controller = controller;	
		this.view = new PropertyPanel();
		this.view.setRightAlignedLabels(false);
		this.view.setVerticalCenterd(false);
		view.setBorder(Spacing.bMedium());
		show(null);
		setLayout(new BorderLayout());
		
		JScrollPane scroller = new JScrollPane(view);
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.add(scroller, BorderLayout.CENTER);
	}
	
	public void show(Coord<Integer> mapCoord) {
		
		
		//TODO: do concentrations right
		if (mapCoord == null)
		{
			showEmpty();
			return;
		}
		
		int index = (mapCoord.y * controller.getSettings().getView().getDataWidth() + mapCoord.x) + 1;
		if (controller.getSettings().getView().isValidPoint(mapCoord)) {
			List<ITransitionSeries> tss = controller.getSettings().getMapFittings().getAllTransitionSeries();
			Map<String, String> properties = new LinkedHashMap<>();
			
			Composition ppm = Composition.calculate(tss, controller.getSettings().getMapFittings().getCalibrationProfile(), ts -> {
				return controller.getSettings().getMapFittings().getMapForTransitionSeries(ts).get(index);
			});
			for (ITransitionSeries ts : tss) {
				properties.put(ts.getElement().toString(), ppm.getPercent(ts.getElement()) );
			}
			view.setProperties(properties);
		} else {
			showEmpty();
		}
	}
	
	private void showEmpty() {
		List<ITransitionSeries> tss = controller.getSettings().getMapFittings().getAllTransitionSeries();
		Map<String, String> properties = new LinkedHashMap<>();
		for (ITransitionSeries ts : tss) {
			properties.put(ts.getElement().name(), "-");
		}
		view.setProperties(properties);
		return;
	}
	
}
