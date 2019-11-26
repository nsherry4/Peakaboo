package org.peakaboo.framework.swidget.icons;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;


public class IconFactory {

	public static final String path = "/swidget/icons/";
	public static String customPath = null;
	
	public static ImageIcon getImageIcon(String imageName){
		return getImageIcon(imageName, null);
	}
	

	public static ImageIcon getMenuIcon(String imageName)
	{
		return getImageIcon(imageName, IconSize.BUTTON);
	}
	
	public static ImageIcon getMenuIcon(StockIcon stock)
	{
		return stock.toImageIcon(IconSize.BUTTON);
	}
	
	
	
	public static ImageIcon getImageIcon(String imageName, IconSize size){
		return getImageIcon(imageName, size, path);
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
	
}
