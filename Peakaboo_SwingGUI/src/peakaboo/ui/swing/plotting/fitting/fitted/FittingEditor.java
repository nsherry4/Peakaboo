package peakaboo.ui.swing.plotting.fitting.fitted;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.TSWidget;
import scitypes.SigDigits;

/*
 * NOT CURRENTLY USED
 */

public class FittingEditor extends AbstractCellEditor implements TreeCellEditor
{
	
	private TSWidget tswidget;
	private FittingController controller;
	private DefaultTreeCellRenderer renderer;
	

	public FittingEditor(DefaultTreeCellRenderer renderer, FittingController controller)
	{
		
		super();
		
		this.renderer = renderer;
		this.controller = controller;
		
		
		
		tswidget = new TSWidget(true);
		tswidget.setOpaque(true);
		
		tswidget.getCheckBox().addItemListener(new ItemListener() {
		
			public void itemStateChanged(ItemEvent e)
			{
				fireEditingStopped();
			}
		});
		
	}
	


	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row)
	{
			
		if (value instanceof TransitionSeries){
			
			TransitionSeries ts = (TransitionSeries)value;
			Element e = ts.element;
			float intensity = controller.getTransitionSeriesIntensity(ts);
			tswidget.setName(ts.getDescription());
			
			String desc;
			if (ts.mode == TransitionSeriesMode.SUMMATION)
			{
				desc = "Intensity: " + SigDigits.roundFloatTo(intensity, 1);
			} else {
				desc = "Atomic #: " + (e.atomicNumber()) + ", Intensity: " + SigDigits.roundFloatTo(intensity, 1);
			}
			tswidget.setDescription(desc);

			tswidget.setBackground(renderer.getBackgroundSelectionColor());
			tswidget.setForeground(renderer.getTextSelectionColor());
			tswidget.setBorder(new EmptyBorder(1, 1, 1, 1));

			return tswidget;
			
		}
		
		return null;
		
	}


	public Object getCellEditorValue()
	{
		return tswidget.isSelected();
	}

	@Override
	public boolean isCellEditable(EventObject arg0)
	{
		return true;
	}


}
