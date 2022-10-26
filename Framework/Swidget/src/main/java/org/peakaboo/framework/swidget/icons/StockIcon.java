package org.peakaboo.framework.swidget.icons;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import javax.swing.ImageIcon;

public enum StockIcon {

	CHOOSE_OK,
	CHOOSE_CANCEL,
	
	ACTION_REFRESH,
	MENU_MAIN,
	MENU_SETTINGS,
	
	BADGE_INFO,
	BADGE_WARNING,
	BADGE_HELP,
	BADGE_ERROR,
	
	DEVICE_COMPUTER,
	DEVICE_HARDDISK,
	DEVICE_MONITOR,
	DEVICE_CAMERA,
	DEVICE_PRINTER,
	
	DOCUMENT_EXPORT,
	DOCUMENT_EXPORT_ARCHIVE,
	DOCUMENT_IMPORT,
	DOCUMENT_NEW,
	DOCUMENT_OPEN,
	DOCUMENT_SAVE_AS,
	DOCUMENT_SAVE,
	
	EDIT_CUT,
	EDIT_COPY,
	EDIT_PASTE,
	EDIT_ADD,
	EDIT_CLEAR,
	EDIT_REMOVE,
	EDIT_DELETE,
	EDIT_REDO,
	EDIT_UNDO,
	EDIT_EDIT,
	EDIT_SORT_ASC,
	EDIT_SORT_DES,
	
	SELECTION_ALL,
	SELECTION_NONE,
	
	OBJECT_FLIP_VERTICAL,
	OBJECT_FLIP_HORIZONTAL,
	OBJECT_INVERSE,
	OBJECT_ROTATE_LEFT,
	OBJECT_ROTATE_RIGHT,
	
	FIND,
	FIND_REPLACE,
	FIND_SELECT_ALL,
	
	GO_BOTTOM,
	GO_DOWN,
	GO_FIRST,
	GO_LAST,
	GO_NEXT,
	GO_PREVIOUS,
	GO_TOP,
	GO_UP,
	
	MIME_PDF,
	MIME_RASTER,
	MIME_SVG,
	MIME_TEXT,
	
	MISC_ABOUT,
	MISC_PREFERENCES,
	MISC_PROPERTIES,
	MISC_EXECUTABLE,
	MISC_LOCKED,
	
	PLACE_DESKTOP,
	PLACE_FOLDER_OPEN,
	PLACE_FOLDER,
	PLACE_FOLDER_NEW,
	PLACE_HOME,
	PLACE_REMOTE,
	PLACE_TRASH,
	
	WINDOW_CLOSE,
	WINDOW_NEW,
	WINDOW_TAB_NEW,
	
	ZOOM_BEST_FIT,
	ZOOM_IN,
	ZOOM_ORIGINAL,
	ZOOM_OUT, 
	
	PROCESS_COMPLETED,
	PROCESS_ERROR,

	
	
	;
	
	@Override
	public String toString()
	{
		return this.name().replace("_", "-").toLowerCase();
	}
	
	public String toIconName()
	{
		return toString();
	}
	
	public ImageIcon toImageIcon(IconSize size) {
		return IconFactory.getImageIcon(toString(), size);
	}
	
	public ImageIcon toSymbolicIcon(IconSize size) {
		return IconFactory.getSymbolicIcon(toString(), size);
	}
	
	public ImageIcon toSymbolicIcon(IconSize size, Color c) {
		ImageIcon icon = toSymbolicIcon(size);
		
		//Get a BufferedImage from this icon
		Image image = icon.getImage();
		BufferedImage bi = new BufferedImage(size.size(), size.size(), BufferedImage.TYPE_INT_ARGB);
		bi.createGraphics().drawImage(image, 0, 0, null);
		
		
		//Go through pixel by pixel and update all the rgb elements to match the given colour, leaving alpha alone
		int[] argb = new int[4];
		WritableRaster raster = bi.getRaster();
		
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		
		for (int y = 0; y < bi.getHeight(); y++) {
			for (int x = 0; x < bi.getWidth(); x++) {
				raster.getPixel(x, y, argb);
				argb[0] = r;
				argb[1] = g;
				argb[2] = b;
				raster.setPixel(x, y, argb);
			}
		}
	
		
		return new ImageIcon(bi);
		
	}
		
}
