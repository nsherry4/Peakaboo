package org.peakaboo.framework.swidget.widgets.fluent.menuitem;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;

import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;

public class FluentCheckMenuItem extends JCheckBoxMenuItem implements FluentMenuItemAPI<FluentCheckMenuItem, FluentMenuItemConfig> {

	private FluentMenuItemConfig config = new FluentMenuItemConfig();
	
	public FluentCheckMenuItem() {
		this.addActionListener(this::action);
		makeWidget();
	}
	
	public FluentCheckMenuItem(String text) {
		this();
		withText(text);
	}
	
	@Override
	public FluentCheckMenuItem getSelf() {
		return this;
	}
	
	@Override
	public FluentMenuItemConfig getComponentConfig() {
		return config;
	}
	
	public FluentCheckMenuItem withAction(Consumer<Boolean> action) {
		if (action == null) {
			getComponentConfig().onAction = null;
		} else {
			getComponentConfig().onAction = () -> action.accept(this.isSelected());
		}
		return getSelf();
	}

	public FluentCheckMenuItem withSelected(boolean selected) {
		setSelected(selected);
		return getSelf();
	}
	
	@Override
	public void makeWidget() {
		this.setText(config.text);
		this.setToolTipText(config.tooltip);
		
		if (config.imagename == null) {
			this.setIcon(null);
		} else {
			ImageIcon icon;
			if (config.symbolic) {
				icon = IconFactory.getSymbolicIcon(config.imagename, IconSize.BUTTON);
			} else {
				icon = IconFactory.getImageIcon(config.imagename, IconSize.BUTTON);
			}
			this.setIcon(icon);
		}
		this.setMnemonic(config.mnemonic == null ? 0 : config.mnemonic);

		if (config.keystroke != null && config.keystrokeParent != null) {
			Action action = new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					FluentCheckMenuItem.this.action(e);
				}
			};
			config.keystrokeParent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(config.keystroke, config.keystroke.toString());
			config.keystrokeParent.getActionMap().put(config.keystroke.toString(), null);
			config.keystrokeParent.getActionMap().put(config.keystroke.toString(), action);
		}
		
	}

	private void action(ActionEvent e) {
		
		boolean orig = this.isSelected();
		boolean state = !orig;
		if (e.getSource() == this) {
			//event is from this checkbox so it's state has already been flipped
			state = orig;
		} else {
			//event is from somewhere else, like a keystroke bound to this checkbox
			//so we have to flip the widget state manually.
			this.setSelected(state);
		}
		
		//now that the widget check state is consistent, call our action
		config.onAction.run();

	}
	
}
