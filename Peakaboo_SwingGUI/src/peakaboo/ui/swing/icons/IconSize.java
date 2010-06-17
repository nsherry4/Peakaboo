package peakaboo.ui.swing.icons;

import java.awt.Dimension;



public enum IconSize
{
	BUTTON_SMALL, BUTTON, TOOLBAR_SMALL, TOOLBAR_LARGE, ICON;
	
	public Dimension toDimension()
	{
		int dim = 0;
		
		switch (this)
		{
			case BUTTON_SMALL: dim = 8;
			case BUTTON: dim =  16;
			case TOOLBAR_SMALL: dim =  22;
			case TOOLBAR_LARGE: dim =  32;
			case ICON: dim =  48;
		}
		
		return new Dimension(dim, dim);
	}
	
}
