package org.peakaboo.framework.swidget.widgets.fluent.menuitem;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JRadioButtonMenuItem;

import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;

public class FluentRadioMenuItem extends JRadioButtonMenuItem implements FluentMenuItemAPI<FluentRadioMenuItem, FluentMenuItemConfig> {

	private FluentMenuItemConfig config = new FluentMenuItemConfig();
	
	public FluentRadioMenuItem() {
		this.addActionListener(this::action);
		makeWidget();
	}
	
	@Override
	public FluentRadioMenuItem getSelf() {
		return this;
	}

	@Override
	public FluentMenuItemConfig getComponentConfig() {
		return config;
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
					FluentRadioMenuItem.this.action(e);
				}
			};
			config.keystrokeParent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(config.keystroke, config.keystroke.toString());
			config.keystrokeParent.getActionMap().put(config.keystroke.toString(), null);
			config.keystrokeParent.getActionMap().put(config.keystroke.toString(), action);
		}
		
	}
	
	private void action(ActionEvent e) {
		this.setSelected(true);
		if (config.onAction != null) {
			config.onAction.run();
		}
	}

}
