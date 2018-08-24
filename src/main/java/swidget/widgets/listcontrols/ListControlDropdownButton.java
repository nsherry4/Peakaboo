package swidget.widgets.listcontrols;

import javax.swing.JPopupMenu;

import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.DropdownImageButton;
import swidget.widgets.ImageButton.Layout;
import swidget.widgets.listcontrols.ListControls.ElementCount;


public abstract class ListControlDropdownButton extends DropdownImageButton implements ListControlWidget
{

	public ListControlDropdownButton(StockIcon stock, String tooltip, JPopupMenu menu)
	{
		super(stock, "", tooltip, IconSize.BUTTON, Layout.IMAGE, menu);
	}
	
	public abstract void setEnableState(ElementCount ec);

	
}
