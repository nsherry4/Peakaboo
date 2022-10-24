package org.peakaboo.framework.swidget.widgets.layerpanel;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.jdesktop.swingx.border.DropShadowBorder;
import org.peakaboo.framework.swidget.widgets.ClearPanel;

public class ModalLayer implements Layer {
	private JLayer<JComponent> layer;
	private JComponent component;
	protected LayerPanel owner;
	
	private ComponentAdapter listener;
	private boolean sizeWithParent = false;
	
	
	public ModalLayer(LayerPanel owner, JComponent component) {
		this(owner, component, false);
	}
	
	public ModalLayer(LayerPanel owner, JComponent component, boolean sizeWithParent) {
		this.owner = owner;
		this.component = component;
		this.sizeWithParent = sizeWithParent;
		
		this.layer = makeModalLayer();
		
	}
	

	/* (non-Javadoc)
	 * @see swidget.widgets.tabbedinterface.layer.Layer#getLayer()
	 */
	@Override
	public JLayer<JComponent> getJLayer() {
		return layer;
	}


	/* (non-Javadoc)
	 * @see swidget.widgets.tabbedinterface.layer.Layer#getComponent()
	 */
	@Override
	public JComponent getComponent() {
		return component;
	}

	//clean up after we're done with this modal layer.
	/* (non-Javadoc)
	 * @see swidget.widgets.tabbedinterface.layer.Layer#discard()
	 */
	@Override
	public void discard() {
		if (listener == null) {
			return;
		}
		owner.removeComponentListener(listener);
	}

	private JLayer<JComponent> makeModalLayer() {
		
		JPanel modalPanel = new ClearPanel();
		
		modalPanel.addMouseListener(new MouseAdapter() {});
		modalPanel.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				e.consume();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				e.consume();
			}
		});
		
		setModalPanelComponent(modalPanel, this.component);
		
		//set blur in case another layer is placed on top of this one
		LayerBlurUI<JComponent> blurUI = new LayerBlurUI<JComponent>(this.owner, this.component) {
			@Override
			public void eventDispatched(AWTEvent e, JLayer<? extends JComponent> l) {
				((InputEvent) e).consume();
			}
		};
		JLayer<JComponent> layer = new JLayer<JComponent>(modalPanel, blurUI);
		
		layer.setVisible(true);
		layer.setOpaque(false);
		layer.setBackground(new Color(0, 0, 0, 0f));
		
		return layer;
				
		
	}
	

	
	private void setModalPanelComponent(JPanel modalPanel, Component component) {
		
		JPanel wrap = new JPanel(new BorderLayout());
		wrap.setOpaque(false);
		Component wrapped = wrapComponent(component);
		
		
		wrap.add(wrapped, BorderLayout.CENTER);
		Border border;
		if (LayerPanelConfig.blur) {
			border = new DropShadowBorder(Color.BLACK, 12, 0.3f, 20, true, true, true, true);
		} else {
			border = new MatteBorder(1, 1, 1, 1, Color.BLACK);
		}
		wrap.setBorder(border);
		
		modalPanel.removeAll();
		
		if (sizeWithParent) {
			modalPanel.setLayout(new BorderLayout());
			modalPanel.add(wrap, BorderLayout.CENTER);
		} else {
			modalPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 3;
			c.gridwidth = 3;
			c.weightx = 0f;
			c.weighty = 0f;
			c.gridx = 1;
			c.gridy = 1;
			
			modalPanel.add(wrap, c);
			
			updateScrolledContentDimensions(wrapped);
			listener = new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					updateScrolledContentDimensions(wrapped);
				}
			};
			owner.addComponentListener(listener);
			
			SwingUtilities.invokeLater(() -> {
				updateScrolledContentDimensions(wrapped);
			});
		}
		
	}
	
	protected static JScrollPane scrolled(Component component) {
		JScrollPane scroller = new JScrollPane();
		scroller.setViewportView(component);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		return scroller;
	}
	
	/**
	 * Wraps the component (for the layer body). By default it is wrapped in a JScrollPane
	 * @param component
	 * @return
	 */
	protected Component wrapComponent(Component component) {
		return scrolled(component);
	}
	
	/**
	 * Given the component added to this ModalLayer, looks up the JScrollPane component within. Subclasses that add more chrome can narrow the scrolled area.
	 */
	protected JScrollPane getScroller(Component component) {
		if (component == null) {
			return null; 
		}
		if (component instanceof JScrollPane) {
			return (JScrollPane) component;
		}
		return null;
	}
	
	/**
	 * When overriding getScrolledFromComponent and wrapComponent to only scroll 
	 * part of the contents of the layer, this allows you to specify the size of 
	 * the space not scrolled. This is used in calculating the preferred size of
	 * the scrolled component based on the parent/owner's size.
	 * @return
	 */
	
	protected Dimension getNonScrolledSize() {
		return new Dimension(0, 0);
	}
		
	private void updateScrolledContentDimensions(Component component) {
		JScrollPane scroller = getScroller(component);
		if (scroller == null) {
			return;
		}
		Component modal = scroller.getViewport().getView();
		if (modal == null) {
			return;
		}
	
		Dimension nonscrolled = getNonScrolledSize();
		Dimension ownerSize = owner.getSize();

		int newWidth = (int)Math.max(50, Math.min(ownerSize.getWidth()-40-nonscrolled.width, modal.getPreferredSize().getWidth()));
		int newHeight = (int)Math.max(50, Math.min(ownerSize.getHeight()-40-nonscrolled.height, modal.getPreferredSize().getHeight()));

		scroller.getViewport().setPreferredSize(new Dimension(newWidth, newHeight));
		scroller.getViewport().setMinimumSize(new Dimension(newWidth, newHeight));
		scroller.revalidate();
	}
	
	
	@Override
	public boolean modal() {
		return true;
	}
	
	protected void remove() {
		owner.removeLayer(this);
	}


}