package peakaboo.ui.swing.widgets.listcontrols;

import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.listcontrols.ListControls.ElementCount;


public abstract class ListControlButton extends ImageButton
{

	public ListControlButton(String filename, String text, String tooltip)
	{
		super(filename, text, tooltip, Layout.IMAGE);
		// TODO Auto-generated constructor stub
	}
	
	public abstract void setEnableState(ElementCount ec);

}
