package org.peakaboo.framework.stratus.components.ui.tabui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Component to be used as tabComponent;
 * Contains a JLabel to show the text and 
 * a JButton to close the tab it belongs to 
 */
public class TabbedInterfaceTitle extends JPanel {
    
	private final TabbedInterface<?> owner;
	private JLabel label;
	private Runnable onDoubleClick = () -> {};
	
	public TabbedInterfaceTitle(String title, int width) {
		this(null, width, false);
		setTitle(title);
	}
	
	public TabbedInterfaceTitle(final TabbedInterface<?> owner, int width) {
		this(owner, width, true);
	}
	
    public TabbedInterfaceTitle(final TabbedInterface<?> owner, int width, boolean closeButton) {
        super(new BorderLayout());
        if (owner == null && closeButton == true) {
            throw new NullPointerException("Owner cannot be null if close buttons are to be used");
        }
        this.owner = owner;
        setOpaque(false);
         
        //make JLabel read titles from JTabbedPane
        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);

        add(label, BorderLayout.CENTER);
        
        if (closeButton) {
	        //add more space between the label and the button
	        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
	        //tab button
	        JButton button = new TabButton();
	        add(button, BorderLayout.EAST);
        }
        
        setMinimumSize(new Dimension(width, (int)getMinimumSize().getHeight()));
        setPreferredSize(new Dimension(width, (int)getPreferredSize().getHeight()));
        MouseListener doubleClickListener = new MouseListener() {
			
			// We have to make sure that we don't eat events that ought to go to the
			// tab/frame itself, so we redispatch all events when we're done with them
			private void redispatch(MouseEvent e) {
				Component source = e.getComponent();
				Component target = source.getParent();
				while (true) {
					if (target == null) {
						break;
					}
					if (target instanceof JTabbedPane) {
						break;
					}
					target = target.getParent();
				}
				if (target != null) {
					MouseEvent targetEvent = SwingUtilities.convertMouseEvent(source, e, target);
					target.dispatchEvent(targetEvent);
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//Double-click triggers the action, nothing else does
				if (!  (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2)) {
					redispatch(e);
					return;
				}
				onDoubleClick.run();
				redispatch(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				redispatch(e);
			}
		};
		this.addMouseListener(doubleClickListener);
		label.addMouseListener(doubleClickListener);

        
    }
    
    protected void setTitle(String title) {
    	label.setText(title);
    }
    
    protected String getTitle() {
    	return label.getText();
    }
 
    private class TabButton extends JButton implements ActionListener {
    	
    	int size = 16;
    	
        public TabButton() {
            
        	super();
        	
        	

            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            //setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
        	
            setPreferredSize(new Dimension(size, size));
        	setToolTipText("Close Tab");
        	

            //Making nice rollover effect
            //we use the same listener for all buttons
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
            
        }
 
        public void actionPerformed(ActionEvent e) {
        	
            int i = owner.getJTabbedPane().indexOfTabComponent(TabbedInterfaceTitle.this);
            owner.closeTab(i);
        }
 
        //we don't want to update UI for this button
        public void updateUI() {}
 
        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
            	g2.setColor(new Color(0.64f, 0f, 0f));
            	g2.fillOval(1, 1, size - 2, size - 2);
            	g2.setColor(Color.WHITE);
                
            }
            
            int delta = 5;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
        
    }

	public void setOnDoubleClick(Runnable onDoubleClick) {
		this.onDoubleClick = onDoubleClick;
	}

    

}