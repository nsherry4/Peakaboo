package org.peakaboo.framework.stratus.api.icons;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import org.peakaboo.framework.stratus.api.StratusLog;


public class IconFactory {

	public static ImageIcon getImageIcon(IconSet icon){
		return getImageIcon(icon.path(), icon.toIconName(), null);
	}
	
	public static ImageIcon getImageIcon(String path, String imageName){
		return getImageIcon(path, imageName, null);
	}

	
	public static ImageIcon getImageIcon(IconSet icon, IconSize size){
		return getImageIcon(icon.path(), icon.toIconName(), size);
	}
	
	public static ImageIcon getImageIcon(String path, String imageName, IconSize size) {
	
		if (path == null) {
			path = StockIcon.PATH;
		}
		
		URL url = getImageIconURL(path, imageName, size);

		if (url == null){
			if (!  (imageName == null || "".equals(imageName))  ) {
				System.out.println("Image not found: " + imageName + "(" + path + ", " + imageName + ", " + (size == null ? "nosize" : size.toString()) + ")");
			}
			url = getImageIconURL(path, "notfound", null);
		}

		if (url == null) {
			return new ImageIcon();
		} else {
			return new ImageIcon(url);
		}
		
	}
	
	public static URL getImageIconURL(String path, String imageName, IconSize size) {
		String iconDir = "";
		if (size != null) iconDir = size.size() + "/";
		
		String location = path + iconDir + imageName + ".png";
		//otherwise loading from jars breaks...
		location = location.replace("//", "/");
		
		URL url = IconFactory.class.getResource(location);
		if (url == null) {
			StratusLog.get().log(Level.FINE, "Failed to locate: " + path + iconDir + imageName + ".png");
		}
		return url;
		
	}
	
	public static Image getImage(String path, String imageName) {
		return getImageIcon(path, imageName, null).getImage();
	}
	
	public static boolean hasImage(String imageName, IconSize size, String path) {
		URL url = getImageIconURL(path, imageName, size);
		return url != null;
	}
	
	
	public static ImageIcon recolour(ImageIcon icon, Color c) {
		//Get a BufferedImage from this icon
		Image image = icon.getImage();
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
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
