package swidget.widgets.widgetlist;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;

import swidget.widgets.ClearPanel;

public abstract class LabeledComponent<T extends Component> extends ClearPanel {

	private JLabel label = new JLabel();
	private T component;
	
	private boolean lastState = true;

	
	public LabeledComponent(T c) {
		component = c;
		setLayout(new BorderLayout());
		
		label.setOpaque(false);

		add(component);
		add(label);
	}
	
	public void setEnabled(boolean enabled) {
		
		if (enabled == lastState) return;
		lastState = enabled;
		
		component.setEnabled(enabled);
		removeAll();
		
		if (enabled) {
			add(component, BorderLayout.CENTER);
		} else {
			label.setText(getStringDescription(component));
			add(label, BorderLayout.CENTER);
		}
		
		repaint();
	}
	
	public boolean getEnabled()
	{
		return lastState;
	}
	
	public abstract String getStringDescription(T component);
	
	public JLabel getDisabledLabel()
	{
		return label;
	}
	
	
}
