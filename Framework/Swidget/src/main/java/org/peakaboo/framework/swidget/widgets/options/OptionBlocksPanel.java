package org.peakaboo.framework.swidget.widgets.options;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;

public class OptionBlocksPanel extends ClearPanel {

	public OptionBlocksPanel(OptionBlock... blocks) {

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(Spacing.bHuge());
		
		boolean first = true;
		for (OptionBlock block : blocks) {
			if (!first) {
				this.add(Box.createVerticalStrut(Spacing.huge));
			}
			this.add(block);
			first = false;
		}
		
		this.add(Box.createVerticalGlue());
	
	}
	
	
}
