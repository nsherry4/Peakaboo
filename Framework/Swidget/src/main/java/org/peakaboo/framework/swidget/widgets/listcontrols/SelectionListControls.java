package org.peakaboo.framework.swidget.widgets.listcontrols;

import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;



public abstract class SelectionListControls extends HeaderBox
{
	
	public SelectionListControls(String name, String title){
		
		super();
		
		FluentButton add = new FluentButton(StockIcon.CHOOSE_OK)
				.withTooltip("Add Selected " + name)
				.withBordered(false)
				.withAction(() -> approve());
		
		FluentButton cancel = new FluentButton(StockIcon.CHOOSE_CANCEL)
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
