package org.peakaboo.framework.autodialog.view.swing.editors.options;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.model.SelfDescribing;
import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;
import org.peakaboo.framework.autodialog.view.swing.editors.AbstractSwingEditor;
import org.peakaboo.framework.swidget.widgets.options.OptionBlock;
import org.peakaboo.framework.swidget.widgets.options.OptionBlocksPanel;
import org.peakaboo.framework.swidget.widgets.options.OptionRadioButton;
import org.peakaboo.framework.swidget.widgets.options.OptionSize;

public class OptionRadioEditor<T extends SelfDescribing> extends AbstractSwingEditor<T> {

	private OptionBlock block;
	private ButtonGroup group;
	private Map<T, OptionRadioButton> buttons;
	private OptionBlocksPanel panel;
	private T editorValue;
	
	public static class OptionRadioStyle<S> extends SimpleStyle<S>{
		public OptionSize size;
		public OptionRadioStyle() {
			this(OptionSize.MEDIUM);
		}
		public OptionRadioStyle(OptionSize size) {
			super("option-radio", CoreStyle.LIST);
			this.size = size;
		}
	}
	
	public OptionRadioEditor() {
		block = new OptionBlock();
		group = new ButtonGroup();
	}
	
	@Override
	public void initialize(Parameter<T> p) {
		this.param = p;
		SelectionParameter<T> selparam = (SelectionParameter<T>) p;
		OptionRadioStyle<T> style = (OptionRadioStyle<T>) p.getStyle();
		buttons = new LinkedHashMap<>();	
		
		for (T t : selparam.getPossibleValues()) {
			OptionRadioButton button = new OptionRadioButton(block, group)
					.withText(t.name(), t.description())
					.withListener(() -> {
						editorValue = t;
						getEditorValueHook().updateListeners(getEditorValue());
						if (!param.setValue(getEditorValue())) {
							setFromParameter();
						}
					});
			
			if (style.size != null) {
				button.withSize(style.size);
			}
			
			buttons.put(t, button);
			block.add(button);
		}

		panel = new OptionBlocksPanel(block);
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(this::setEnabled);

	}
	

	@Override
	public boolean expandVertical() {
		return false;
	}

	@Override
	public boolean expandHorizontal() {
		return true;
	}

	@Override
	public LabelStyle getLabelStyle() {
		return LabelStyle.LABEL_HIDDEN;
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public void setEditorValue(T value) {
		OptionRadioButton button = buttons.get(value);
		if (!button.isSelected()) {
			button.setSelected(true);
		}
	}

	@Override
	public T getEditorValue() {
		return editorValue;
	}
	
	
	@Override
	protected void setEnabled(boolean enabled) {
		panel.setEnabled(enabled);
		for (var button : buttons.values()) {
			button.setEnabled(enabled);
		}
	}
	
	
}
