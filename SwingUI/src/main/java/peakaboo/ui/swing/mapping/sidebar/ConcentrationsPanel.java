package peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cyclops.Coord;
import peakaboo.controller.mapper.MappingController;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.mapping.Mapping;
import swidget.widgets.Spacing;
import swidget.widgets.layout.PropertyPanel;
import swidget.widgets.layout.TitledPanel;

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
			List<TransitionSeries> tss = controller.getSettings().getMapFittings().getAllTransitionSeries();
			Map<String, String> properties = new LinkedHashMap<>();
			
			Map<Element, Float> ppm = Mapping.concentrations(tss, ts -> {
				return controller.getSettings().getMapFittings().getMapForTransitionSeries(ts).get(index);
			});
			NumberFormat format = new DecimalFormat("0.0");
			for (TransitionSeries ts : tss) {
				properties.put(ts.element.toString(), format.format(ppm.get(ts.element) / 10000) + "%" );
			}
			view.setProperties(properties);
		} else {
			showEmpty();
		}
	}
	
	private void showEmpty() {
		List<TransitionSeries> tss = controller.getSettings().getMapFittings().getAllTransitionSeries();
		Map<String, String> properties = new LinkedHashMap<>();
		for (TransitionSeries ts : tss) {
			properties.put(ts.element.name(), "-");
		}
		view.setProperties(properties);
		return;
	}
	
}
