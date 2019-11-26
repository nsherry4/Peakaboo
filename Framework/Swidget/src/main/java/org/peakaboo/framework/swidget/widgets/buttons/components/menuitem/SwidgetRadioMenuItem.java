package org.peakaboo.framework.swidget.widgets.buttons.components.menuitem;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRadioButtonMenuItem;

import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;

public class SwidgetRadioMenuItem extends JRadioButtonMenuItem implements SwidgetMenuItemFluentAPI<SwidgetRadioMenuItem, SwidgetMenuItemConfig> {

	private SwidgetMenuItemConfig config = new SwidgetMenuItemConfig();
	
	public SwidgetRadioMenuItem() {
		this.addActionListener(this::action);
		makeWidget();
	}
	
	@Override
	public SwidgetRadioMenuItem getSelf() {
		return this;
	}

	@Override
	public SwidgetMenuItemConfig getComponentConfig() {
		return config;
	}

	@Override
	public void makeWidget() {
		this.setText(config.text);
		this.setToolTipText(config.tooltip);
		
		if (config.imagename == null) {
			this.setIcon(null);
		} else {
			this.setIcon(IconFactory.getImageIcon(config.imagename, IconSize.BUTTON));
		}
		this.setMnemonic(config.mnemonic == null ? 0 : config.mnemonic);

		if (config.keystroke != null && config.keystrokeParent != null) {
			Action action = new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					SwidgetRadioMenuItem.this.action(e);
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
