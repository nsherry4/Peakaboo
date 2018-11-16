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
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.fitted.FittedWidget;

public class MapFittingRenderer extends DefaultTableCellRenderer
{

	private FittedWidget tswidget;
	private Predicate<LegacyTransitionSeries> enabled;
	
	
	public MapFittingRenderer(Predicate<LegacyTransitionSeries> enabled){
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
		
		if (value instanceof LegacyTransitionSeries){
			LegacyTransitionSeries ts = (LegacyTransitionSeries)value;
			tswidget.setName(ts.toString());
			tswidget.setEnabled(enabled.test(ts));
			
			String tooltip = "";
			if (ts.getMode() != TransitionSeriesMode.SUMMATION){
				tooltip = ts.getElement().toString();
			} else {
				List<Element> elements = ts.getBaseTransitionSeries().stream().map(t -> t.getElement()).collect(Collectors.toList());
				tooltip = elements.stream().map(e -> e.toString()).reduce((a, b) -> a + ", " + b).get();
			}
			tswidget.setToolTipText(tooltip);
			
			tswidget.setMinimumSize(new Dimension(0, 100));
			return tswidget;
		} 
		
		return this;
	}
	

}