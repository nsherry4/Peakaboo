package org.peakaboo.ui.swing.plotting.fitting.fitted;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionSeriesMode;
import org.peakaboo.framework.cyclops.SigDigits;


class FittingRenderer extends DefaultTableCellRenderer
{

	private FittedWidget tswidget;
	private FittingController controller;

	
	
	FittingRenderer(FittingController controller){
		this.controller = controller;
		tswidget = FittedWidget.large();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus,
			int row, int column)
	{
		
		super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
		

		if (selected){
			tswidget.setOpaque(true);
			tswidget.setBackground(table.getSelectionBackground());
			tswidget.setForeground(table.getSelectionForeground());
		} else {
			tswidget.setOpaque(false);
			tswidget.setBackground(table.getBackground());
			tswidget.setForeground(table.getForeground());			
		}
		
		float intensity;
		
		if (table.getRowHeight() < tswidget.getPreferredSize().height) {
			table.setRowHeight(tswidget.getPreferredSize().height);
		}
		
		if (value instanceof ITransitionSeries ts){
			intensity = controller.getTransitionSeriesIntensity(ts);
			tswidget.setName(ts.toString());
			
			tswidget.setIntensity(SigDigits.roundFloatTo(intensity, 1));
			tswidget.setFlag(controller.hasAnnotation(ts));
			String tooltip = "";
			if (ts.getMode() != TransitionSeriesMode.SUMMATION){
				tswidget.setAtomicNumber(ts.getElement().atomicNumber());
				tooltip = ts.getElement().toString();
			} else {
				List<Element> elements = ts.getPrimaryTransitionSeries().stream().map(ITransitionSeries::getElement).toList();
				tswidget.setAtomicNumbers(elements.stream().map(Element::atomicNumber).toList());
				tooltip = elements.stream().map(Element::toString).reduce((a, b) -> a + ", " + b).orElse("");
			}
			if (controller.hasAnnotation(ts)) {
				tooltip += " - " + controller.getAnnotation(ts);
			}
			tswidget.setToolTipText(tooltip);
			
			return tswidget;
		} 
		
		return this;
	}
	

}
