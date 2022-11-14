package org.peakaboo.framework.autodialog.view.swing.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.view.swing.editors.options.OptionRadioEditor;

public class SwingEditorFactory {

	private static Map<String, Supplier<SwingEditor<?>>> styleProviders = new HashMap<>();
	
	static {
		registerStyleProvider("file-name", FilenameEditor::new);
		registerStyleProvider("integer-slider", IntegerSliderEditor::new);
		registerStyleProvider("option-radio", OptionRadioEditor::new);
	}
	
	private SwingEditorFactory() {
		// Not Constructable
	}
	
	
	
	public static void registerStyleProvider(String style, Supplier<SwingEditor<?>> provider) {
		styleProviders.put(style, provider);
	}
	
	public static List<SwingEditor<?>> forParameters(Collection<Parameter<?>> parameters) {
		return parameters.stream().map(SwingEditorFactory::forParameter).collect(Collectors.toList());
	}
	
	public static <T> SwingEditor<T> forParameter(Parameter<T> parameter) {

		SwingEditor<T> editor = null;
		
		for (String key : styleProviders.keySet()) {
			if (key.equals(parameter.getStyle().getStyle())) {
				editor = (SwingEditor<T>) styleProviders.get(key).get();
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
	
	@SuppressWarnings("unchecked")
	private static <T> SwingEditor<T> fallback(CoreStyle fallbackStyle) {
		switch (fallbackStyle) {
			case BOOLEAN: return (SwingEditor<T>) new BooleanEditor();
			case TEXT_VALUE: return (SwingEditor<T>) new TextBoxEditor();
			case TEXT_AREA: return (SwingEditor<T>) new TextAreaEditor();
			case INTEGER: return (SwingEditor<T>) new IntegerEditor();
			case FLOAT: return (SwingEditor<T>) new FloatEditor();
			case LIST: return new ListEditor<>();
			case SPACING: return (SwingEditor<T>) new SeparatorEditor();
			case LABEL_VALUE: return (SwingEditor<T>) new LabelEditor();
			default: return null;
		}
	}
	
}
