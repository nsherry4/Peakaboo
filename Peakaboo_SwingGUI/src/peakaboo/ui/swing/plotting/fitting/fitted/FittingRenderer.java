package peakaboo.ui.swing.plotting.fitting.fitted;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import peakaboo.controller.plotter.FittingController;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.peaktable.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.TSWidget;
import scitypes.SigDigits;


public class FittingRenderer extends DefaultTableCellRenderer
{

	private TSWidget tswidget;
	private FittingController controller;

	
	
	public FittingRenderer(FittingController controller){
		
		
		
		this.controller = controller;
		
		tswidget = new TSWidget(true);		
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
		
		Element e;
		float intensity;
		
		if (table.getRowHeight() < tswidget.getPreferredSize().height) {
			table.setRowHeight(tswidget.getPreferredSize().height);
		}
		
		if (value instanceof TransitionSeries){
			TransitionSeries ts = (TransitionSeries)value;
			e = ts.element; 
			intensity = controller.getTransitionSeriesIntensity(ts);
			tswidget.setName(ts.getDescription());
			
			String desc;
			if (ts.mode == TransitionSeriesMode.SUMMATION)
			{
				desc = "Intensity: " + SigDigits.roundFloatTo(intensity, 1);
			} else {
				desc = "Atomic # " + (e.atomicNumber()) + ", Intensity: " + SigDigits.roundFloatTo(intensity, 1);
			}
			tswidget.setDescription(desc);
			
			tswidget.setSelected(controller.getTransitionSeriesVisibility(ts));
			tswidget.setMinimumSize(new Dimension(0, 100));
						
			return tswidget;
		} 
		
		return this;
	}
	

}
