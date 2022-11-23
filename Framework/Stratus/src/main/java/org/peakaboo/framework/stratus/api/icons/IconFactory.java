package org.peakaboo.framework.stratus.api.icons;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
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
	
	public static ImageIcon getImageIcon(String path, String imageName, IconSize size){
	
		if (path == null) {
			path = StockIcon.PATH;
		}
		
		URL url = getImageIconURL(path, imageName, size);
		InputStream stream = getImageIconStream(path, imageName, size);
		
		if (stream == null){
			if (!  (imageName == null || "".equals(imageName))  ) {
				System.out.println("Image not found: " + imageName + "(" + path + ", " + imageName + ", " + (size == null ? "nosize" : size.toString()) + ")");
			}
			stream = getImageIconStream(path, "notfound", null);
		}

		try {
			byte[] bytes = stream.readAllBytes();
			ImageIcon image = new ImageIcon(bytes);
			return image;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				StratusLog.get().log(Level.WARNING, "Failed to close stream for image or icon", e);
			}
		}
		
		
	}
	
	public static URL getImageIconURL(String path, String imageName, IconSize size)
	{
		String iconDir = "";

		if (size != null) iconDir = size.size() + "/";
		
		URL url = IconFactory.class.getResource(path + iconDir + imageName + ".png");
//		if (url == null) {
//			System.out.println("Failed to locate: " + path + iconDir + imageName + ".png");
//		}
		return url;
		
	}
	
	public static InputStream getImageIconStream(String path, String imageName, IconSize size)
	{
		String iconDir = "";

		if (size != null) iconDir = size.size() + "/";
		
		return IconFactory.class.getResourceAsStream(path + iconDir + imageName + ".png");
	}
	
	public static Image getImage(String path, String imageName)
	{
		
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
