package peakaboo.ui.swing.plotting.fitting.fitted;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.TSWidget;


public class FittingRenderer extends DefaultTreeCellRenderer
{

	private TSWidget tswidget;
	private FittingController controller;
	
	
	public FittingRenderer(FittingController controller){
		
		
		this.controller = controller;
		
		
		tswidget = new TSWidget(true);
		
		
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{

		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		

		if (selected){
			
			tswidget.setBackground(getBackgroundSelectionColor());
			tswidget.setForeground(getTextSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBorderSelectionColor()));

		} else {
			tswidget.setBackground(getBackgroundNonSelectionColor());
			tswidget.setForeground(getTextNonSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBackgroundNonSelectionColor()));
			
		}
		
		Element e;
		float intensity;
		
		
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
				desc = "Atomic #: " + (e.atomicNumber()) + ", Intensity: " + SigDigits.roundFloatTo(intensity, 1);
			}
			tswidget.setDescription(desc);
			
			tswidget.setSelected(controller.getTransitionSeriesVisibility(ts));
			//element.setPreferredSize(new Dimension(0, element.getPreferredSize().height));
						
			return tswidget;
		}
		return this;
		
		
	}



}
