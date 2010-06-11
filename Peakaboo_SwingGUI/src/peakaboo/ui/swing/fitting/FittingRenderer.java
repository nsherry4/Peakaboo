package peakaboo.ui.swing.fitting;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.ui.swing.widgets.Spacing;


public class FittingRenderer extends DefaultTreeCellRenderer
{

	private ElementWidget element;
	private JCheckBox tsName;
	private FittingController controller;
	
	
	public FittingRenderer(FittingController controller){
		
		
		this.controller = controller;
		
		
		element = new ElementWidget();
		
		tsName = new JCheckBox("");
		tsName.setBorder(Spacing.bNone());
		tsName.setOpaque(false);
		tsName.setFont(tsName.getFont().deriveFont(Font.PLAIN));
		
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{

		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		

		if (selected){
			
			element.setBackground(getBackgroundSelectionColor());
			element.setForeground(getTextSelectionColor());
			element.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBorderSelectionColor()));
			
			tsName.setBackground(getBackgroundSelectionColor());
			tsName.setForeground(getTextSelectionColor());
			tsName.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBorderSelectionColor()));
			
		} else {
			//element.setBorder(c.getBorder());
			element.setBackground(getBackgroundNonSelectionColor());
			element.setForeground(getTextNonSelectionColor());
			element.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBackgroundNonSelectionColor()));
			
			//ts.setBorder(c.getBorder());
			tsName.setBackground(getBackgroundNonSelectionColor());
			tsName.setForeground(getTextNonSelectionColor());
			tsName.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBackgroundNonSelectionColor()));
		}
		
		Element e;
		double intensity;
		if (value instanceof Element){
			
			e = (Element)value;
			intensity = controller.getIntensityForElement(e);
			element.setName(e.toString());
			element.setDescription("Atomic #: " + (e.ordinal() + 1) + ", Intensity: " + SigDigits.roundFloatTo(intensity, 1));
			element.setSelected(controller.getElementVisibility(e));
			//element.setPreferredSize(new Dimension(0, element.getPreferredSize().height));
						
			return element;
			
		} else if (value instanceof TransitionSeries){
			
			TransitionSeries t = (TransitionSeries)value;
			e = t.element;
			intensity = controller.getTransitionSeriesIntensityForElement(e, t.type);
			
			tsName.setText(t.toString() + " (" + SigDigits.roundFloatTo(intensity, 1) + ")");
			tsName.setSelected(t.visible);
			
			tsName.setPreferredSize(new Dimension(1000, tsName.getPreferredSize().height));

			
			return tsName;
			
		}
		
		return this;
		
	}



}
