package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuUtils {


	static JCheckBoxMenuItem createMenuCheckItem(PlotPanel plot, String title, ImageIcon icon, String description, Consumer<Boolean> listener, KeyStroke key, Integer mnemonic)
	{
		
		JCheckBoxMenuItem menuItem;
		if (icon != null) {
			menuItem = new JCheckBoxMenuItem(title, icon);
		} else {
			menuItem = new JCheckBoxMenuItem(title);
		}
		
		Consumer<ActionEvent> checkListener = e -> {
			boolean orig = menuItem.isSelected();
			boolean state = !orig;
			if (e.getSource() == menuItem) {
				state = orig;
			}
			menuItem.setSelected(state);
			listener.accept(state);
		};
		
		configureMenuItem(plot, menuItem, description, checkListener, key, mnemonic);
		
		return menuItem;
		
	}
	
	static JRadioButtonMenuItem createMenuRadioItem(PlotPanel plot, String title, ImageIcon icon, String description, Consumer<ActionEvent> listener, KeyStroke key, Integer mnemonic)
	{
		
		JRadioButtonMenuItem menuItem;
		if (icon != null) {
			menuItem = new JRadioButtonMenuItem(title, icon);
		} else {
			menuItem = new JRadioButtonMenuItem(title);
		}
		
		Consumer<ActionEvent> checkListener = e -> {
			menuItem.setSelected(true);
			listener.accept(e);
		};
		
		configureMenuItem(plot, menuItem, description, checkListener, key, mnemonic);
		
		return menuItem;
		
	}
	
	public static JMenuItem createMenuItem(JComponent plot, String title, ImageIcon icon, String description, Consumer<ActionEvent> listener, KeyStroke key, Integer mnemonic)
	{
		JMenuItem menuItem;
		if (icon != null) {
			menuItem = new JMenuItem(title, icon);
		} else {
			menuItem = new JMenuItem(title);
		}
		
		configureMenuItem(plot, menuItem, description, listener, key, mnemonic);
		
		return menuItem;
		
	}
	
	static void configureMenuItem(JComponent plot, JMenuItem menuItem, String description, Consumer<ActionEvent> listener, KeyStroke key, Integer mnemonic)
	{
		
		
			
		Action action = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.accept(e);
			}
		};

			
		//You'd think this would fail with tabs because they'd both try to handle the
		//key event, but it actually works perfectly. Maybe the tab component itself 
		//redirects input to only the focused tab?
		if (key != null) {
			plot.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, key.toString());
			plot.getActionMap().put(key.toString(), action);
		}
		
		
		
		//Even though this isn't how actions are performed anymore, we still want it to show up
		if (key != null) menuItem.setAccelerator(key);
		
		if (mnemonic != null) menuItem.setMnemonic(mnemonic);
		if (description != null) menuItem.getAccessibleContext().setAccessibleDescription(description);
		if (description != null) menuItem.setToolTipText(description);
		if (listener != null) menuItem.addActionListener(e -> listener.accept(e));
	}
	
	
}
