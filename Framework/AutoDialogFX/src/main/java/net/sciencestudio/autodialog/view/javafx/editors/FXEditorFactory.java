package net.sciencestudio.autodialog.view.javafx.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.CoreStyle;

public class FXEditorFactory {

	private static Map<String, Supplier<FXEditor<?>>> styleProviders = new HashMap<>();
	
	static {
		registerStyleProvider("file-name", FilenameEditor::new);
	}
	
	
	
	
	
	public static void registerStyleProvider(String style, Supplier<FXEditor<?>> provider) {
		styleProviders.put(style, provider);
	}
	

	public static <T> FXEditor<T> forParameter(Parameter<T> parameter) {

		FXEditor<T> editor = null;
		
		for (String key : styleProviders.keySet()) {
			if (key.equals(parameter.getStyle().getStyle())) {
				editor = (FXEditor<T>) styleProviders.get(key).get();
				break;
			}
		}
		
		if (editor == null) {
			//Fallback to CoreStyle
			editor = fallback(parameter.getStyle().getFallbackStyle());
		}
		
		editor.initialize(parameter);
		return editor;
		
		
	}
	
	private static <T> FXEditor<T> fallback(CoreStyle fallbackStyle) {
		switch (fallbackStyle) {
			case BOOLEAN: return (FXEditor<T>) new BooleanEditor();
			case TEXT_VALUE: return (FXEditor<T>) new TextAreaEditor();
			case TEXT_AREA: return (FXEditor<T>) new TextAreaEditor();
			case INTEGER: return (FXEditor<T>) new IntegerEditor();
			case FLOAT: return (FXEditor<T>) new RealEditor();
			case LIST: return (FXEditor<T>) new ListEditor<T>();
			case SPACING: return (FXEditor<T>) new SeparatorEditor();
			default: return null;
		}
	}
	
}
