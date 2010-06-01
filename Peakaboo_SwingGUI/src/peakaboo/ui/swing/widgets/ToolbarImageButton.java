package peakaboo.ui.swing.widgets;

import java.awt.Insets;

import javax.swing.border.Border;

import peakaboo.ui.swing.icons.IconSize;

public class ToolbarImageButton extends ImageButton {

	protected static Layout defaultLayout = Layout.IMAGE;
	protected static Layout significantLayout = Layout.IMAGE_ON_SIDE;
	protected static IconSize defaultSize = IconSize.TOOLBAR_SMALL;
	protected static Insets defaultInsets = Spacing.iMedium();
	protected static Border defaultBorder = Spacing.bLarge();
	
	public ToolbarImageButton(String filename, String text)
	{
		super(filename, text, text, defaultLayout, false, defaultSize, defaultInsets, defaultBorder );
	}
	
	public ToolbarImageButton(String filename, String text, String tooltip)
	{
		super(filename, text, tooltip, defaultLayout, false, defaultSize, defaultInsets, defaultBorder );
	}
	
	public ToolbarImageButton(String filename, String text, String tooltip, boolean isSignificant)
	{
		super(filename, text, tooltip, (isSignificant ? significantLayout : defaultLayout), false, defaultSize, defaultInsets, defaultBorder );
	}
	
}
