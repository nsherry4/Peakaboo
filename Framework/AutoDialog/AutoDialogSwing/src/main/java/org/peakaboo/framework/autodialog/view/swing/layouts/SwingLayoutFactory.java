package org.peakaboo.framework.autodialog.view.swing.layouts;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.peakaboo.framework.autodialog.model.Group;


public class SwingLayoutFactory {

	
	private static Map<String, Supplier<SwingLayout>> styleProviders = new HashMap<>();
	
	private SwingLayoutFactory() {
		
	}
	
	static {
		registerStyleProvider("layout-tabs", TabbedSwingLayout::new);
		registerStyleProvider("layout-column", SimpleSwingLayout::new);
		registerStyleProvider("layout-frames", FramesSwingLayout::new);
	}
	
	
	
	
	
	public static void registerStyleProvider(String style, Supplier<SwingLayout> provider) {
		styleProviders.put(style, provider);
	}
	
	
	public static SwingLayout forGroup(Group group) {

		SwingLayout editor = null;
		for (String key : styleProviders.keySet()) {
			if (key.equals(group.getStyle().getStyle())) {
				editor = styleProviders.get(key).get();
				break;
			}
		}
		
		if (editor == null) {
			editor = fallback();
		}
		
		editor.initialize(group);
		return editor;
		
		
	}
	
	private static SwingLayout fallback() {
		return new SimpleSwingLayout();
	}
	
}
