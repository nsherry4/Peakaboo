package org.peakaboo.framework.stratus.laf.painters;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public abstract class StatefulPainter extends SimpleThemed implements Painter<JComponent> {


    protected ArrayList<ButtonState> states;
    private boolean selected, pressed, disabled, enabled, mouseover, focused, def;

    public StatefulPainter(Theme theme, ButtonState... buttonStates) {
    	super(theme);
    	this.states = new ArrayList<>(Arrays.asList(buttonStates));
    	
    	selected = states.contains(ButtonState.SELECTED);
    	pressed = states.contains(ButtonState.PRESSED);
    	disabled = states.contains(ButtonState.DISABLED);
    	enabled = states.contains(ButtonState.ENABLED);
    	mouseover = states.contains(ButtonState.MOUSEOVER);
    	focused = states.contains(ButtonState.FOCUSED);
    	def = states.contains(ButtonState.DEFAULT);
    }
	
    protected boolean isSelected() {
    	return selected;
    }
    
    protected boolean isPressed() {
    	return pressed;
    }
    
    protected boolean isDisabled() {
    	return disabled;
    }
    
    protected boolean isEnabled() {
    	return enabled;
    }
    
    protected boolean isMouseOver() {
    	return mouseover;
    }

    protected boolean isFocused() {
    	return focused;
    }
    
    protected boolean isDefault() {
    	return def;
    }

	
}
