package org.peakaboo.framework.swidget.widgets.listcontrols;

import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;



public abstract class SelectionListControls extends HeaderBox
{
	
	public SelectionListControls(String name, String title){
		
		super();
		
		ImageButton add = new ImageButton(StockIcon.CHOOSE_OK)
				.withTooltip("Add Selected " + name)
				.withBordered(false)
				.withAction(() -> approve());
		
		ImageButton cancel = new ImageButton(StockIcon.CHOOSE_CANCEL)
				.withTooltip("Discard Selections")
				.withBordered(false)
				.withAction(() -> cancel());

		setBorder(Spacing.bSmall());
		
		setDragable(false);
		setComponents(cancel, title, add);

	}
	
	protected abstract void approve();
	protected abstract void cancel();
	
}
