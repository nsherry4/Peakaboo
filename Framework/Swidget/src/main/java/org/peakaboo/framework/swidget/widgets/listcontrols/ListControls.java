package org.peakaboo.framework.swidget.widgets.listcontrols;


import java.awt.BorderLayout;

import javax.swing.JButton;

import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;



public class ListControls extends ClearPanel {

	private JButton add, remove, clear;
	

	public enum ElementCount
	{
		NONE, ONE, MANY
	}
	
	public ListControls(ImageButton add, ImageButton remove, ImageButton clear) {
		this.add = add;
		this.remove = remove;
		this.clear = clear;
		
		configureButton(add);
		configureButton(remove);
		configureButton(clear);
		
		
		setElementCount(ElementCount.NONE);
		
		ButtonBox box = new ButtonBox(Spacing.small, false);
		box.addLeft(add);
		box.addLeft(remove);
		box.addRight(clear);
		this.setLayout(new BorderLayout());
		this.add(box, BorderLayout.CENTER);
		this.setBorder(Spacing.bNone());
		
	}
	
	private void configureButton(ImageButton b) {
		b.withBordered(false);
	}
	
	public void setElementCount(int elements) {
		ElementCount ec = ElementCount.NONE;
		if (elements == 0) {
			ec = ElementCount.NONE;
		} else if (elements == 1) {
			ec = ElementCount.ONE;
		} else if (elements >= 2) {
			ec = ElementCount.MANY;
		}
		setElementCount(ec);
	}
	
	public void setElementCount(ElementCount ec)
	{

		switch (ec) {
		case MANY:
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			break;
		case NONE:
			add.setEnabled(true);
			remove.setEnabled(false);
			clear.setEnabled(false);
			break;
		case ONE:
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			break;
		default:
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			break;
		
		}

	}

	
}
