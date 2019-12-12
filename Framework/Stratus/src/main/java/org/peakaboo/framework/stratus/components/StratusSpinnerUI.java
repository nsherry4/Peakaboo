package org.peakaboo.framework.stratus.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthSpinnerUI;

public class StratusSpinnerUI extends SynthSpinnerUI {

    /**
     * Returns a new instance of StratusSpinnerUI.
     *
     * @param c the JSpinner (not used)
     * @see ComponentUI#createUI
     * @return a new StratusSpinnerUI object
     */
    public static ComponentUI createUI(JComponent c) {
        return new StratusSpinnerUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return new SynthContext(c, SynthLookAndFeel.getRegion(c), super.getContext(c).getStyle(), state);
    }
    
    protected int getComponentState(JComponent c) {
    	int state = ENABLED;
    	
        if (!c.isEnabled()) {
            state = DISABLED;
        }

        //Secondary states
        if (containsFocus(c)) {
            state |= FOCUSED;
        }
           	
    	return state;
    }
    
    /**
     * Tests this component to see if it or any child component currently has focus
     */
    private boolean containsFocus(Container c) {
    	for (Component child : c.getComponents()) {
    		if (child.isFocusOwner()) { return true; }
    		if (child instanceof Container) {
    			if (containsFocus((Container) child)) { return true; }
    		}
    	}
    	return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutManager createLayout() {
        return new SpinnerLayout();
    }

    private static class SpinnerLayout implements LayoutManager, UIResource
    {
        private Component nextButton = null;
        private Component previousButton = null;
        private Component editor = null;
        //Need a little bit of space to keep elements from running into the background border
        private static final Insets pad = new Insets(0, 2, 0, 2);
        
        public void addLayoutComponent(String name, Component c) {
            if ("Next".equals(name)) {
                nextButton = c;
            }
            else if ("Previous".equals(name)) {
                previousButton = c;
            }
            else if ("Editor".equals(name)) {
                editor = c;
				// Changing the editor component after the widget is in use seems to cause it to
				// become opaque and break the look of the spinner. We force it to not draw a
				// background here
                ((JComponent)editor).setOpaque(false);
            }
        }

        public void removeLayoutComponent(Component c) {
            if (c == nextButton) {
                nextButton = null;
            }
            else if (c == previousButton) {
                previousButton = null;
            }
            else if (c == editor) {
                editor = null;
            }
        }

        private Dimension preferredSize(Component c) {
            return (c == null) ? new Dimension(0, 0) : c.getPreferredSize();
        }

        public Dimension preferredLayoutSize(Container parent) {
            Dimension nextD = preferredSize(nextButton);
            Dimension previousD = preferredSize(previousButton);
            Dimension editorD = preferredSize(editor);

            /* Force the editors height to be a multiple of 2
             */
            //not needed when buttons are side-by-side
            //editorD.height = ((editorD.height + 1) / 2) * 2;

            Dimension size = new Dimension(editorD.width, editorD.height);
            size.width += Math.max(nextD.width, previousD.width)*2;
            Insets insets = parent.getInsets();
            size.width += insets.left + pad.left + insets.right + pad.right;
            size.height += insets.top + pad.top + insets.bottom + pad.bottom;
            return size;
        }

        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        private void setBounds(Component c, int x, int y, int width, int height) {
            if (c != null) {
                c.setBounds(x, y, width, height);
            }
        }

        public void layoutContainer(Container parent) {
            Insets insets = parent.getInsets();
            
            int availWidth = parent.getWidth() - (insets.left + pad.left + insets.right + pad.right);
            int availHeight = parent.getHeight() - (insets.top + pad.top + insets.bottom + pad.bottom);
            Dimension nextD = preferredSize(nextButton);
            Dimension previousD = preferredSize(previousButton);
            int buttonWidth = Math.max(nextD.width, previousD.width);
            int buttonsWidth = buttonWidth*2;
            int editorWidth = availWidth - buttonsWidth;

            /* Deal with the spinners componentOrientation property.
             */
            int editorX, buttonsX;
            if (parent.getComponentOrientation().isLeftToRight()) {
                editorX = insets.left + pad.left;
                buttonsX = editorX + editorWidth;
            }
            else {
                buttonsX = insets.left + pad.left;
                editorX = buttonsX + buttonsWidth;
            }

            setBounds(editor, editorX, insets.top + pad.top, editorWidth, availHeight);
            setBounds(previousButton, buttonsX, insets.top + pad.top, buttonWidth, availHeight);
            setBounds(nextButton, buttonsX+buttonWidth, insets.top + pad.top, buttonWidth, availHeight);
        }
    }
    
	
}
