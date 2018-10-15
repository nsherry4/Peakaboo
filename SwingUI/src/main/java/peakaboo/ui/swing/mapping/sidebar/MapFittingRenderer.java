package peakaboo.ui.swing.mapping.sidebar;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.fitted.FittedWidget;

public class MapFittingRenderer extends DefaultTableCellRenderer
{

	private FittedWidget tswidget;
	private Predicate<TransitionSeries> enabled;
	
	
	public MapFittingRenderer(Predicate<TransitionSeries> enabled){
		tswidget = new FittedWidget();	
		this.enabled = enabled;
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
			tswidget.setBorder(new EmptyBorder(1, 1, 1, 1));

		} else {
			
			tswidget.setOpaque(false);
			tswidget.setBackground(table.getBackground());
			tswidget.setForeground(table.getForeground());
			tswidget.setBorder(new EmptyBorder(1, 1, 1, 1));
			
		}
		
		
		if (table.getRowHeight() < tswidget.getPreferredSize().height) {
			table.setRowHeight(tswidget.getPreferredSize().height);
		}
		
		if (value instanceof TransitionSeries){
			TransitionSeries ts = (TransitionSeries)value;
			tswidget.setName(ts.getDescription());
			tswidget.setEnabled(enabled.test(ts));
			
			String tooltip = "";
			if (ts.mode != TransitionSeriesMode.SUMMATION){
				tooltip = ts.element.toString();
			} else {
				List<Element> elements = ts.getBaseTransitionSeries().stream().map(t -> t.element).collect(Collectors.toList());
				tooltip = elements.stream().map(e -> e.toString()).reduce((a, b) -> a + ", " + b).get();
			}
			tswidget.setToolTipText(tooltip);
			
			tswidget.setMinimumSize(new Dimension(0, 100));
			return tswidget;
		} 
		
		return this;
	}
	

}