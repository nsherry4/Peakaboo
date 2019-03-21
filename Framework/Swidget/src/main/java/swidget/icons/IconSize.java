package swidget.icons;

public enum IconSize
{
	BUTTON, TOOLBAR_SMALL, TOOLBAR_LARGE, ICON;
	
	public int size()
	{	
		switch (this)
		{
			case BUTTON: return 16;
			case TOOLBAR_SMALL: return 24;
			case TOOLBAR_LARGE: return 32;
			case ICON: return 48;
		}
		
		return 16;
	}
	
}
