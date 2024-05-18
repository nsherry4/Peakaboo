package org.peakaboo.framework.stratus.api;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StratusText {

	public static String lineWrapHTML(Component c, String text) {
		return "<html>" + lineWrapHTMLInline(c, text) + "</html>";
	}
	
	public static String lineWrapHTML(Component c, String text, int width) {
		return "<html>" + lineWrapHTMLInline(c, text, width) + "</html>";
	}
	
	public static String lineWrapHTMLInline(Component c, String text) {
		return lineWrap(c, text).replace("\n", "<br/>");
	}
	
	public static String lineWrapHTMLInline(Component c, String text, int width) {
		return lineWrap(c, text, width).replace("\n", "<br/>");
	}
	
	public static String lineWrap(Component c, String text) {
		return lineWrap(c, text, 400);
	}
	
	public static String lineWrap(Component c, String text, int width) {
		if (text.contains("\n")) {
			String[] lines = text.split("\n");
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				sb.append(lineWrap(c, line, width));
				sb.append("\n");
			}
			return sb.toString();
		}
		
		List<String> lines = new ArrayList<String>();
		
		Font font = c.getFont();
		FontMetrics metrics = c.getFontMetrics(font);
				
		String line = "";
		Graphics g = c.getGraphics();
		
		List<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));
		
		
		lines.clear();
		while (words.size() > 0) {
		
			while ( metrics.getStringBounds(line, g).getWidth() < width ) {
				if (words.size() == 0) break;
				if (!line.equals("")) line += " ";
				line = line + words.remove(0);
			}
			
			lines.add(line);
			line = "";
			
		}
		
		Optional<String> str = lines.stream().reduce((a, b) -> a + "\n" + b);
		return str.orElse("");
	}
	
}
