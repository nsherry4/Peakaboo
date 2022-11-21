package org.peakaboo.framework.stratus.laf.components;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.synth.SynthComboBoxUI;

import org.peakaboo.framework.stratus.laf.StratusLookAndFeel;



public class StratusComboBoxUI extends SynthComboBoxUI {

	
    @Override
    protected ComboPopup createPopup() {
    	BasicComboPopup p = (BasicComboPopup) super.createPopup();
    	
    	Color control = (Color) UIManager.get("control");
    	p.setBorder(new LineBorder(control, 1));
    	p.setLightWeightPopupEnabled(!StratusLookAndFeel.HEAVYWEIGHT_POPUPS);
        return p;
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new StratusComboBoxUI();
    }

	
}

