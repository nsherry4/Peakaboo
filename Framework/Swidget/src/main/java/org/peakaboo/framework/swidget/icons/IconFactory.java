package org.peakaboo.framework.swidget.icons;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.net.URL;

import javax.swing.ImageIcon;

import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;


public class IconFactory {

	public static final String PATH_IMAGE = "/swidget/icons/";
	public static final String PATH_SYMBOLIC = "/swidget/icons/symbolic/";
	public static String customPath = null;
	
	public static ImageIcon getImageIcon(String imageName){
		return getImageIcon(imageName, null);
	}

	public static ImageIcon getSymbolicIcon(String imageName){
		return getImageIcon(imageName, null);
	}
	
	public static ImageIcon getImageIcon(String imageName, IconSize size){
		return getImageIcon(imageName, size, PATH_IMAGE);
	}
	
	public static ImageIcon getSymbolicIcon(String imageName, IconSize size){
		return getImageIcon(imageName, size, PATH_SYMBOLIC);
	}
	
	public static ImageIcon getImageIcon(String imageName, IconSize size, String path){
		
	
		URL url = getImageIconURL(imageName, size, path);

		//if we can't find the image, look for it elsewhere
		if (url == null) { url = getImageIconURL(imageName, size, path); }
		if (url == null && customPath != null) { url = getImageIconURL(imageName, size, customPath); }
		
		
		if (url == null){
			if (!  (imageName == null || "".equals(imageName))  )
			{
				System.out.println("Image not found: " + imageName);
			}
			url = getImageIconURL("notfound", null, path);	
		}
		
		
		ImageIcon image;
		image = new ImageIcon(url);
		return image;
		
	}
	
	public static URL getImageIconURL(String imageName, IconSize size, String path)
	{
		String iconDir = "";

		if (size != null) iconDir = size.size() + "/";
						
		return FluentButton.class.getResource(path + iconDir + imageName + ".png");
		
	}
	
	public static Image getImage(String imageName)
	{
		
		return getImageIcon(imageName, null).getImage();
		
	}
	
	public static boolean hasImage(String imageName, IconSize size) {
		
		URL url = getImageIconURL(imageName, size, PATH_IMAGE);

		//if we can't find the image, look for it elsewhere
		if (url == null) { url = getImageIconURL(imageName, size, PATH_IMAGE); }
		if (url == null && customPath != null) { url = getImageIconURL(imageName, size, customPath); }
		
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
