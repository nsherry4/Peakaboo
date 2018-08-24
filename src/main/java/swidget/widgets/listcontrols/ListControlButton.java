package swidget.widgets.listcontrols;

import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.listcontrols.ListControls.ElementCount;


public abstract class ListControlButton extends ImageButton implements ListControlWidget
{

	public ListControlButton(String filename, String tooltip)
	{
		super();
		super.withIcon(filename)
			.withTooltip(tooltip)
			.withLayout(Layout.IMAGE)
			.withBordered(false);
		
	}
	
	public ListControlButton(StockIcon stock, String tooltip)
	{
		super();
		super.withIcon(stock)
			.withTooltip(tooltip)
			.withBordered(false);
	}
	
	public abstract void setEnableState(ElementCount ec);

}
