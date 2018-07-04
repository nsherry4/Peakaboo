package stratus.components;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.synth.SynthComboBoxUI;

import stratus.StratusLookAndFeel;



public class StratusComboBoxUI extends SynthComboBoxUI {

	
    @Override
    protected ComboPopup createPopup() {
    	BasicComboPopup p = (BasicComboPopup) super.createPopup();
    	
    	Color control = (Color) UIManager.get("control");
    	p.setBorder(new LineBorder(control, 1));
    	p.setLightWeightPopupEnabled(!StratusLookAndFeel.HEAvYWEIGHT_POPUPS);
        return p;
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new StratusComboBoxUI();
    }

	
}

