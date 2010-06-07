package peakaboo.ui.swing.icons;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import peakaboo.ui.swing.widgets.ImageButton;

public class IconFactory {

	
	public static ImageIcon getImageIcon(String imageName){
		return getImageIcon(imageName, null);
	}
	

	public static ImageIcon getMenuIcon(String imageName)
	{
		return getImageIcon(imageName, IconSize.BUTTON);
	}
	
	public static ImageIcon getImageIcon(String imageName, IconSize size){
		
	
		URL url = getImageIconURL(imageName, size);

		
		ImageIcon image;
		if (url == null){
			image = new ImageIcon();
		} else {
			image = new ImageIcon(url);
		}
		
		return image;
		
	}
	
	public static URL getImageIconURL(String imageName, IconSize size)
	{
		String iconDir = "";
		if (size == IconSize.BUTTON_SMALL){
			iconDir = "8/";
		} else if (size == IconSize.BUTTON){
			iconDir = "16/";
		} else if (size == IconSize.TOOLBAR_SMALL){
			iconDir = "22/";
		} else if (size == IconSize.TOOLBAR_LARGE){
			iconDir = "32/";
		} else if (size == IconSize.ICON){
			iconDir = "48/";
		}
		
		return ImageButton.class.getResource("/peakaboo/ui/swing/icons/" + iconDir + imageName + ".png");
		
	}
	
	public static Image getImage(String imageName)
	{
		
		InputStream is = null;
		is = IconFactory.class.getResourceAsStream("/peakaboo/ui/swing/icons/" + imageName + ".png");
		
		Image image;

		if (is == null) return null;
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {
			image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
		
		
		return image;
		
	}
	
}
