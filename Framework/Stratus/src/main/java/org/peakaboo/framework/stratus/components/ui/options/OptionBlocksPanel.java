package org.peakaboo.framework.stratus.components.ui.options;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class OptionBlocksPanel extends ClearPanel {

	public OptionBlocksPanel(OptionBlock... blocks) {
		this(OptionSize.LARGE, blocks);
	}
	
	public OptionBlocksPanel(OptionSize size, OptionBlock... blocks) {

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.setBorder(switch(size) {
			case LARGE -> Spacing.bHuge();
			case MEDIUM -> Spacing.bMedium();
			case SMALL -> Spacing.bTiny();
			default -> Spacing.bLarge();
		});
		
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
