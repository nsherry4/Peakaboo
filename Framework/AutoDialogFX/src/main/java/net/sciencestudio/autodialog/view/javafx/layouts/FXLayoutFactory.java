package net.sciencestudio.autodialog.view.javafx.layouts;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.sciencestudio.autodialog.model.Group;


public class FXLayoutFactory {

	
	private static Map<String, Supplier<FXLayout>> styleProviders = new HashMap<>();
	
	static {
		registerStyleProvider("layout-tabs", TabbedFXLayout::new);
		registerStyleProvider("layout-column", SimpleFXLayout::new);
		registerStyleProvider("layout-frames", FramesFXLayout::new);
	}
	
	
	
	
	
	public static void registerStyleProvider(String style, Supplier<FXLayout> provider) {
		styleProviders.put(style, provider);
	}
	
	
	public static FXLayout forGroup(Group group) {

		FXLayout editor = null;
		
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
	
	private static FXLayout fallback() {
		return new SimpleFXLayout();
	}
	
}
