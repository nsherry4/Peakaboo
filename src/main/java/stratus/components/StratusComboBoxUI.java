package stratus.components;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.synth.SynthComboBoxUI;

import stratus.Stratus;
import stratus.StratusLookAndFeel;
import stratus.painters.Themed;
import stratus.theme.Theme;



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

