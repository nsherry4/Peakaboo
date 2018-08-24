package swidget.widgets;

import javax.swing.JLabel;

public class TextWrapping
{
	public static final int DEFAULT_WIDTH = 300;

	public static String wrapTextForMultiline(String text)
	{
		return wrapTextForMultiline(text, DEFAULT_WIDTH);
	}
	
	public static String wrapTextForMultiline(String text, int width)
	{
		return 	"<html><div style='width: " + width + "px'>" + 
				text + 
				"</div></html>";
	}
	
}
