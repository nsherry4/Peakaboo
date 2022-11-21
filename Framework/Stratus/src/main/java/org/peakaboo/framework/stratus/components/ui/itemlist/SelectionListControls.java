package org.peakaboo.framework.stratus.components.ui.itemlist;

import java.awt.Color;
import java.awt.Paint;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderBox;



public abstract class SelectionListControls extends HeaderBox
{
	
	public SelectionListControls(String name, String title){
		
		super();
		
		FluentButton add = new FluentButton()
				.withIcon(StockIcon.CHOOSE_OK)
				.withTooltip("Add Selected " + name)
				.withBordered(false)
				.withAction(() -> approve());
		
		FluentButton cancel = new FluentButton()
				.withIcon(StockIcon.CHOOSE_CANCEL)
				.withTooltip("Discard Selections")
				.withBordered(false)
				.withAction(() -> cancel());

		setBorder(Spacing.bSmall());
		
		setDragable(false);
		setComponents(cancel, title, add);

	}
	
	protected abstract void approve();
	protected abstract void cancel();
	
	protected Paint getBackgroundPaint() {
		return new Color(0x00000000, true);
	}
	
}
