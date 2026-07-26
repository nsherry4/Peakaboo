package org.peakaboo.framework.stratus.api.icons;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.FluentConfig;
import org.peakaboo.framework.stratus.laf.theme.Theme;


public class IconFactory {

	public static ImageIcon getImageIcon(FluentConfig config){
		return getImageIcon(config.imagepath, config.imagename, config.size, config.imagecolour);
	}
	
	public static ImageIcon getImageIcon(IconSet icon){
		return getImageIcon(icon.path(), icon.toIconName(), null, null);
	}
	
	public static ImageIcon getImageIcon(String path, String imageName){
		return getImageIcon(path, imageName, null, null);
	}

	
	public static ImageIcon getImageIcon(IconSet icon, IconSize size){
		return getImageIcon(icon.path(), icon.toIconName(), size, null);
	}
	
	public static ImageIcon getImageIcon(String path, String imageName, IconSize size) {
		return getImageIcon(path, imageName, size, null);
	}

	public static ImageIcon getImageIcon(String path, String imageName, Color colour) {
		return getImageIcon(path, imageName, null, colour);
	}
	
	public static ImageIcon getImageIcon(String path, String imageName, IconSize size, Color colour) {
	
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
		}

		var image = new ImageIcon(url);
		if (colour != null) {
			return recolour(image, colour);
		}

		// Icons can load before the look and feel is configured, eg on the splash screen
		Theme theme = Stratus.getTheme();
		if (theme == null) {
			return image;
		}

		BufferedImage bi = toBufferedImage(image);
		if (!isSymbolic(url, bi)) {
			return image;
		}
		return tint(bi, theme.getControlText());
	}

	private static BufferedImage toBufferedImage(ImageIcon icon) {
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		bi.createGraphics().drawImage(icon.getImage(), 0, 0, null);
		return bi;
	}
	
	
	private static final Map<String, Boolean> symbolicCache = new ConcurrentHashMap<>();
	/**
	 * Test an icon to determine if it is symbolic. We examine the opaque pixels to
	 * look for any hint of colour or shading. Generally, symbolic icons will not have
	 * any of this.
	 */
	private static boolean isSymbolic(URL url, BufferedImage bi) {
		return symbolicCache.computeIfAbsent(url.toString(), k -> {
			int min = 255;
			int max = 0;
			for (int y = 0; y < bi.getHeight(); y++) {
				for (int x = 0; x < bi.getWidth(); x++) {
					int argb = bi.getRGB(x, y);
					// Ignore anti-aliased edges, they say nothing about the icon's colour
					int a = (argb >> 24) & 0xff;
					if (a <= 200) { continue; }
					int r = (argb >> 16) & 0xff;
					int g = (argb >> 8) & 0xff;
					int b = argb & 0xff;
					// Look for anything with any colour saturation
					if (r != g || g != b) { return false; }
					min = Math.min(min, r);
					max = Math.max(max, r);
					// Look for any greyscale shading, too
					if (max - min > 8) { return false; }
				}
			}
			return true;
		});
	}
	
	public static URL getImageIconURL(String path, String imageName, IconSize size) {
		String iconDir = "";
		if (size != null) iconDir = size.size() + "/";
		
		String location = path + iconDir + imageName + ".png";
		//otherwise loading from jars breaks...
		location = location.replace("//", "/");
		
		URL url = IconFactory.class.getResource(location);
		if (url == null) {
			OneLog.log(Level.FINE, "Failed to locate: " + path + iconDir + imageName + ".png");
		}
		return url;
		
	}
	
	public static Image getImage(String path, String imageName) {
		return getImageIcon(path, imageName, null, null).getImage();
	}
	
	public static boolean hasImage(String imageName, IconSize size, String path) {
		URL url = getImageIconURL(path, imageName, size);
		return url != null;
	}
	
	
	public static ImageIcon recolour(ImageIcon icon, Color c) {
		return tint(toBufferedImage(icon), c);
	}

	/**
	 * Repaints every pixel in the given colour, keeping the image's alpha. Consumes
	 * the image it is handed.
	 */
	private static ImageIcon tint(BufferedImage bi, Color c) {
		int[] argb = new int[4];
		WritableRaster raster = bi.getRaster();

		int a = c.getAlpha();
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();

		for (int y = 0; y < bi.getHeight(); y++) {
			for (int x = 0; x < bi.getWidth(); x++) {
				raster.getPixel(x, y, argb);
				argb[0] = r;
				argb[1] = g;
				argb[2] = b;
				//divide by 255, not >>8: the latter leaves a fully opaque pixel at 254
				argb[3] = (argb[3] * a) / 255;
				raster.setPixel(x, y, argb);
			}
		}

		return new ImageIcon(bi);
	}
	
}
